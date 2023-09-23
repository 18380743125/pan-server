package com.tangl.pan.core.utils;

import cn.hutool.core.date.DateUtil;
import com.tangl.pan.core.constants.TPanConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
     * @param filename     文件名称
     * @param isContainDot 是否包含 dot
     * @return 文件后缀
     */
    public static String getFileSuffix(String filename, boolean isContainDot) {
        if (StringUtils.isBlank(filename) || filename.indexOf(TPanConstants.POINT_STR) == TPanConstants.MINUS_ONE_INT) {
            return StringUtils.EMPTY;
        }
        if (isContainDot) {
            return filename.substring(filename.lastIndexOf(TPanConstants.POINT_STR));
        } else {
            return filename.substring(filename.lastIndexOf(TPanConstants.POINT_STR) + TPanConstants.ONE_INT);
        }
    }

    /**
     * 通过文件大小转化文件大小的展示名称
     *
     * @param totalSize 文件大小字节数
     * @return 文件大小的展示名称
     */
    public static String byteCountToDisplaySize(Long totalSize) {
        if (Objects.isNull(totalSize)) {
            return TPanConstants.EMPTY_STR;
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
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(getFileSuffix(filename, true))
                .toString();
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
                .append("tpan")
                .toString();
    }

    public static String generateDefaultStoreFileChunkRealPath() {
        return new StringBuffer(System.getProperty("user.home"))
                .append(File.separator)
                .append("tpan")
                .append(File.separator)
                .append("chunks")
                .toString();
    }

    public static String generateStoreFileChunkRealPath(String basePath, String identifier, Integer chunkNumber) {
        return new StringBuffer(basePath)
                .append(File.separator)
                .append(DateUtil.thisYear())
                .append(File.separator)
                .append(DateUtil.thisMonth() + 1)
                .append(File.separator)
                .append(DateUtil.thisDayOfMonth())
                .append(File.separator)
                .append(identifier)
                .append(File.separator)
                .append(UUIDUtil.getUUID())
                .append(TPanConstants.COMMON_SEPARATOR)
                .append(chunkNumber)
                .toString();
    }

    /**
     * 追加写文件
     *
     * @param target 写的目的文件
     * @param source 源文件
     */
    public static void appendWrite(Path target, Path source) throws IOException {
        Files.write(target, Files.readAllBytes(source), StandardOpenOption.APPEND);
    }

    /**
     * 利用零拷贝技术读取文件内容并写入到文件输出流中
     *
     * @param fileInputStream 文件输入流
     * @param outputStream    输出流
     * @param length          文件的长度
     */
    public static void writeFile2OutputStream(FileInputStream fileInputStream, OutputStream outputStream, long length) throws IOException {
        FileChannel fileChannel = fileInputStream.getChannel();
        WritableByteChannel writableByteChannel = Channels.newChannel(outputStream);
        fileChannel.transferTo(TPanConstants.ZERO_INT, length, writableByteChannel);
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
        fileChannel.close();
        writableByteChannel.close();
    }

    /**
     * 普通流对流数据传输
     *
     * @param inputStream  objectContent
     * @param outputStream 响应输出流
     */
    public static void writeStream2StreamNormal(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != TPanConstants.MINUS_ONE_INT) {
            outputStream.write(buffer, TPanConstants.ZERO_INT, len);
        }
        outputStream.flush();
        inputStream.close();
        outputStream.close();
    }
}
