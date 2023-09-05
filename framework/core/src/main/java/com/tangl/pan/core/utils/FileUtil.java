package com.tangl.pan.core.utils;

import cn.hutool.core.date.DateUtil;
import com.tangl.pan.core.constants.TPanConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Objects;

/**
 * @author tangl
 * @description 文件相关的工具类
 * @create 2023-08-13 22:46
 */
public class FileUtil {
    /**
     * 根据文件名称获取后缀
     *
     * @param filename 文件名称
     * @return 文件后缀
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.indexOf(TPanConstants.POINT_STR) == TPanConstants.MINUS_ONE_INT) {
            return StringUtils.EMPTY;
        }
        return filename.substring(filename.lastIndexOf(TPanConstants.POINT_STR));
    }

    /**
     * 通过文件大小转化文件大小的展示名称
     *
     * @param totalSize 文件大小字节数
     * @return 文件大小的展示名称
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return TPanConstants.Empty_STR;
        }
        return FileUtils.byteCountToDisplaySize(totalSize);
    }

    /**
     * 批量删除物理文件
     *
     * @param realFilePathList 物理文件的物理路径列表
     */
    public static void deleteFiles(List<String> realFilePathList) throws IOException {
        if (CollectionUtils.isEmpty(realFilePathList)) {
            return;
        }
        for (String realFilePath : realFilePathList) {
            FileUtils.forceDelete(new File(realFilePath));
        }
    }

    /**
     * 生成文件的存储路径
     * 生成规则：基础路径 + 年 + 月 + 日 + 随机的文件名称
     *
     * @param basePath 基础路径
     * @param filename 文件名称
     * @return 物理文件的真实路径
     */
    public static String generateStoreFileRealPath(String basePath, String filename) {
        return basePath + File.separator + DateUtil.thisYear() + File.separator + (DateUtil.thisMonth() + 1) + File.separator + DateUtil.thisDayOfMonth() + File.separator + UUIDUtil.getUUID() + getFileSuffix(filename);
    }

    /**
     * 将文件的输入流写入到文件中
     * 使用底层的 sendFile 零拷贝来提高传输效率
     *
     * @param inputStream 文件输入流
     * @param targetFile  文件对象
     * @param totalSize   文件大小
     */
    public static void writeStream2File(InputStream inputStream, File targetFile, Long totalSize) throws IOException {
        createFile(targetFile);
        RandomAccessFile randomAccessFile = new RandomAccessFile(targetFile, "rw");
        FileChannel outputChannel = randomAccessFile.getChannel();
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        outputChannel.transferFrom(inputChannel, 0L, totalSize);
        inputChannel.close();
        outputChannel.close();
        randomAccessFile.close();
        inputStream.close();
    }

    /**
     * 创建文件
     * 包含父文件一起视情况去创建
     *
     * @param targetFile 文件对象
     */
    public static void createFile(File targetFile) throws IOException {
        if (!targetFile.getParentFile().exists()) {
            targetFile.getParentFile().mkdirs();
        }
        targetFile.createNewFile();
    }

    /**
     * 生成默认的文件存储路径
     * 生成规则：当前登录用户的文件目录 + tpan
     *
     * @return 默认的存储路径
     */
    public static String generateDefaultStoreFileRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("tpan").toString();
    }
}
