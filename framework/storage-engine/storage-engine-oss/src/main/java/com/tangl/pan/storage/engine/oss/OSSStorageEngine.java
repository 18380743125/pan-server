package com.tangl.pan.storage.engine.oss;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.exception.TPanFrameworkException;
import com.tangl.pan.core.utils.FileUtil;
import com.tangl.pan.core.utils.UUIDUtil;
import com.tangl.pan.storage.engine.core.AbstractStorageEngine;
import com.tangl.pan.storage.engine.core.context.*;
import com.tangl.pan.storage.engine.oss.config.OSSStorageEngineConfig;
import lombok.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 基于 OSS 的文件存储引擎实现类
 * @create 2023-08-14 21:46
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {

    private static final Integer TEN_THOUSAND = 10000;

    private static final String CACHE_KEY_TEMPLATE = "oss_cache_upload_id_%s_%s";


    private static final String IDENTIFIER_KEY = "identifier";

    private static final String UPLOAD_ID_KEY = "uploadId";

    private static final String USER_ID_KEY = "userId";

    private static final String PART_NUMBER_KEY = "partNumber";

    private static final String E_TAG_KEY = "eTag";

    private static final String PART_SIZE_KEY = "partSize";

    private static final String PART_CRC_KEY = "partCRC";

    @Autowired
    protected OSSStorageEngineConfig config;

    @Autowired
    private OSSClient client;

    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String realPath = getFilePath(FileUtil.getFileSuffix(context.getFilename(), true));
        client.putObject(config.getBucketName(), realPath, context.getInputStream());
        context.setRealPath(realPath);
    }

    /**
     * 删除物理文件
     * 1、获取所有需要删除的文件存储路径
     * 2、如果该存储路径是一个文件分片的路径，截取出对应的 Object 的 name，然后取消文件分片的操作
     * 3、如果是一个正常的文件存储路径，直接执行物理删除即可
     *
     * @param context 删除物理文件的上下文实体
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        List<String> realFilePathList = context.getRealFilePathList();
        realFilePathList.forEach(realPath -> {
            // 是一个分片的存储路径
            if (checkHaveParams(realPath)) {
                JSONObject params = analysisUrlParams(realPath);
                if (!params.isEmpty()) {
                    String uploadId = params.getString(UPLOAD_ID_KEY);
                    String identifier = params.getString(IDENTIFIER_KEY);
                    Long userId = params.getLong(USER_ID_KEY);
                    String cacheKey = getCacheKey(identifier, userId);
                    getCache().evict(cacheKey);
                    try {
                        AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(config.getBucketName(),
                                getBaseUrl(realPath), uploadId);
                        client.abortMultipartUpload(request);
                    } catch (Exception ignored) {

                    }
                }
            } else {
                // 普通文件的物理删除
                client.deleteObject(config.getBucketName(), realPath);
            }
        });
    }

    /**
     * OSS文件分片上传的步骤：
     * 1、初始化文件分片上传，获取一个全局唯一的 uploadId
     * 2、并发上传文件分片，每一个文件分片都需要带有初始化返回的 uploadId
     * 3、所有分片上传完成，触发文件分片合并的操作
     * <p>
     * 难点：
     * 1、我们的分片上传是在一个多线程并发环境下运行的，我们的程序需要保证我们的初始化分片上传的操作只有一个线程可以做
     * 2、我们的所有文件分片都需要带有一个全局唯一的 uploadId，该 uploadId 需要放置到一个线程的共享空间中
     * 3、我们需要保证每一个文件分片都能够单独取消文件分片上传，而不是依赖于全局的 uploadId
     * <p>
     * 解决方案：
     * 1、加锁：我们目前首先按照单体架构考虑，使用 JVM 的锁去保证一个线程初始化文件分片上传，如果后续扩展成分布式的架构，需更换分布式锁
     * 2、使用缓存：缓存分为本地缓存以及分布式缓存（如 redis），我们当前是一个单体架构，可以考虑使用本地缓存，但是后期项目的分布式架构
     * 升级后同样需要升级我们的缓存架构，所以我们第一版本就支持分布式缓存比较好
     * 3、我们要想把每一个文件的 key 都能够通过文件的 url 来获取，就需要定义一种数据格式支持我们添加附件数据，并且很方便解析出来，我们的的实现方案
     * 可以参考网络请求 url 的格式：filePath?paramKey=paramValue
     * <p>
     * 具体实现逻辑
     * 1、校验为文件分片数不得大于 10000
     * 2、获取缓存 key
     * 3、通过缓存 key 获取初始化后的实体对象，获取全局的 uploadId 和 ObjectName
     * 4、如果获取为空，直接初始化
     * 5、否则执行文件分片上传的操作
     * 6、上传完成后将全局的参数封装成一个可识别的 url，保存在上下文里面，用于业务的落库操作
     *
     * @param context 存储物理文件分片的上下文实体
     */
    @Override
    protected synchronized void doStoreChunk(StoreFileChunkContext context) {
        if (context.getTotalChunks() > TEN_THOUSAND) {
            throw new TPanFrameworkException("分片数超过了限制，分片数不得大于：" + TEN_THOUSAND);
        }

        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());

        ChunkUploadEntity entity = getCache().get(cacheKey, ChunkUploadEntity.class);

        if (Objects.isNull(entity)) {
            entity = initChunkUpload(context.getFilename(), cacheKey);
        }

        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(config.getBucketName());
        request.setKey(entity.getObjectKey());
        request.setUploadId(entity.getUploadId());
        request.setInputStream(context.getInputStream());
        request.setPartSize(context.getCurrentChunkSize());
        request.setPartNumber(context.getChunkNumber());

        UploadPartResult result = client.uploadPart(request);

        if (Objects.isNull(result)) {
            throw new TPanFrameworkException("文件分片上传失败");
        }

        PartETag partETag = result.getPartETag();

        // 拼装文件分片的 url
        JSONObject params = new JSONObject();
        params.put(IDENTIFIER_KEY, context.getIdentifier());
        params.put(UPLOAD_ID_KEY, entity.getUploadId());
        params.put(USER_ID_KEY, context.getUserId());
        params.put(E_TAG_KEY, partETag.getETag());
        params.put(PART_NUMBER_KEY, partETag.getPartNumber());
        params.put(PART_SIZE_KEY, partETag.getPartSize());
        params.put(PART_CRC_KEY, partETag.getPartCRC());
        String realPath = assembleUrl(entity.getObjectKey(), params);
        context.setRealPath(realPath);
    }

    /**
     * 文件分片合并
     * 1、获取缓存信息，拿到全局的 uploadId
     * 2、从上下文信息里获取所有的分片的 URL，解析出需要执行文件合并请求的参数
     * 3、执行文件合并的请求
     * 4、清除缓存
     * 5、设置返回结果
     *
     * @param context 上下文实体
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String cacheKey = getCacheKey(context.getIdentifier(), context.getUserId());

        ChunkUploadEntity entity = getCache().get(cacheKey, ChunkUploadEntity.class);

        if (Objects.isNull(entity)) {
            throw new TPanFrameworkException("文件分片合并失败，文件的唯一标识为：" + context.getIdentifier());
        }

        List<String> chunkPaths = context.getRealPathList();

        List<PartETag> partETags = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(chunkPaths)) {
            partETags = chunkPaths.stream()
                    .filter(StringUtils::isNotBlank)
                    .map(this::analysisUrlParams)
                    .filter(jsonObject -> !jsonObject.isEmpty())
                    .map(jsonObject -> new PartETag(jsonObject.getIntValue(PART_NUMBER_KEY),
                            jsonObject.getString(E_TAG_KEY),
                            jsonObject.getLongValue(PART_SIZE_KEY),
                            jsonObject.getLong(PART_CRC_KEY)
                    )).collect(Collectors.toList());
        }

        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(config.getBucketName(),
                entity.getObjectKey(), entity.getUploadId(), partETags);
        CompleteMultipartUploadResult result = client.completeMultipartUpload(request);
        if (Objects.isNull(result)) {
            throw new TPanFrameworkException("文件分片合并失败，文件的唯一标识为：" + context.getIdentifier());
        }

        getCache().evict(cacheKey);

        context.setRealPath(entity.getObjectKey());
    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        OSSObject ossObject = client.getObject(config.getBucketName(), context.getReadPath());
        if (Objects.isNull(ossObject)) {
            throw new TPanFrameworkException("文件读取失败，文件路径为：" + context.getReadPath());
        }
        FileUtil.writeStream2StreamNormal(ossObject.getObjectContent(), context.getOutputStream());
    }


    /**
     * 分析 URL 参数
     *
     * @param url url
     * @return JSONObject
     */
    private JSONObject analysisUrlParams(String url) {
        JSONObject result = new JSONObject();
        if (!checkHaveParams(url)) {
            return result;
        }
        String paramsPart = url.split(getSplitMark(TPanConstants.QUESTION_MARK_STR))[1];
        if (StringUtils.isNotBlank(paramsPart)) {
            List<String> paramPairList = Splitter.on(TPanConstants.AND_MARK_STR).splitToList(paramsPart);
            paramPairList.forEach(paramPair -> {
                String[] paramArr = paramPair.split(getSplitMark(TPanConstants.EQUALS_MARK_STR));
                if (paramArr.length == TPanConstants.TWO_INT) {
                    result.put(paramArr[0], paramArr[1]);
                }
            });
        }
        return result;
    }

    /**
     * 拼装URL
     *
     * @param baseUrl ObjectKey
     * @param params  参数
     * @return baseUrl?paramKey1=paramValue1&paramKey2=paramValue2
     */
    private String assembleUrl(String baseUrl, JSONObject params) {
        if (Objects.isNull(params) || params.isEmpty()) {
            return baseUrl;
        }
        StringBuffer urlStringBuffer = new StringBuffer(baseUrl);
        urlStringBuffer.append(TPanConstants.QUESTION_MARK_STR);
        List<String> paramsList = Lists.newArrayList();
        StringBuffer urlParamsStringBuffer = new StringBuffer();
        params.forEach((key, value) -> {
            urlParamsStringBuffer.setLength(TPanConstants.ZERO_INT);
            urlParamsStringBuffer.append(key);
            urlParamsStringBuffer.append(TPanConstants.EQUALS_MARK_STR);
            urlParamsStringBuffer.append(value);
            paramsList.add(urlParamsStringBuffer.toString());
        });
        return urlStringBuffer.append(Joiner.on(TPanConstants.AND_MARK_STR).join(paramsList)).toString();
    }

    /**
     * 获取基础 URL
     *
     * @param url url
     * @return baseUrl
     */
    private String getBaseUrl(String url) {
        if (StringUtils.isBlank(url)) {
            return TPanConstants.EMPTY_STR;
        }
        if (checkHaveParams(url)) {
            return url.split(getSplitMark(TPanConstants.QUESTION_MARK_STR))[0];
        }
        return url;
    }

    /**
     * 获取截取字符串的关键标识
     * 由于java的字符串分割会按照正则去截取
     * 我们的 URL 会影响标识的识别，故添加左右中括号去分组
     *
     * @param mark mark
     * @return String
     */
    private String getSplitMark(String mark) {
        return new StringBuffer(TPanConstants.LEFT_BRACKET_STR)
                .append(mark)
                .append(TPanConstants.RIGHT_BRACKET_STR)
                .toString();
    }

    /**
     * 检查是否是含有参数的 URL
     *
     * @param url url
     * @return boolean
     */
    private boolean checkHaveParams(String url) {
        return StringUtils.isNotBlank(url) && url.indexOf(TPanConstants.QUESTION_MARK_STR) != TPanConstants.MINUS_ONE_INT;
    }


    /**
     * 初始化文件分片上传
     * 1、初始化请求
     * 2、保存初始化结果到缓存中
     *
     * @param filename 文件名称
     * @param cacheKey 缓存 key
     * @return ChunkUploadEntity
     */
    private ChunkUploadEntity initChunkUpload(String filename, String cacheKey) {
        String filePath = getFilePath(FileUtil.getFileSuffix(filename, true));

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), filePath);
        InitiateMultipartUploadResult result = client.initiateMultipartUpload(request);
        if (Objects.isNull(result)) {
            throw new TPanFrameworkException("文件分片上传初始化失败");
        }

        ChunkUploadEntity entity = new ChunkUploadEntity();
        entity.setUploadId(result.getUploadId());
        entity.setObjectKey(filePath);

        getCache().put(cacheKey, entity);

        return entity;
    }

    /**
     * 获取分片上传的缓存 key
     *
     * @param identifier 文件唯一标识
     * @param userId     用户ID
     * @return String
     */
    private String getCacheKey(String identifier, Long userId) {
        return String.format(CACHE_KEY_TEMPLATE, identifier, userId);
    }

    /**
     * 获取对象的完整名称
     * 年/月/日/UUID.fileSuffix
     *
     * @param fileSuffix 文件后缀
     * @return filePath
     */
    private String getFilePath(String fileSuffix) {
        return new StringBuffer()
                .append(DateUtil.thisYear())
                .append(TPanConstants.SLASH_STR)
                .append(DateUtil.thisMonth() + 1)
                .append(TPanConstants.SLASH_STR)
                .append(DateUtil.thisDayOfMonth())
                .append(TPanConstants.SLASH_STR)
                .append(UUIDUtil.getUUID())
                .append(fileSuffix)
                .toString();
    }

    /**
     * 文件分片上传初始化后的全局信息载体
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    @ToString
    public static class ChunkUploadEntity implements Serializable {

        private static final long serialVersionUID = 779396653999907657L;

        /**
         * 分片上传全局唯一的 uploadId
         */
        private String uploadId;

        /**
         * 文件分片上传的实体名称
         */
        private String objectKey;
    }
}
