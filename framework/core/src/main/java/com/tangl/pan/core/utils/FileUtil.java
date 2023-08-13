package com.tangl.pan.core.utils;

import com.tangl.pan.core.constants.TPanConstants;
import org.apache.commons.lang3.StringUtils;

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
}
