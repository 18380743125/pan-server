package com.tangl.pan.server.modules.file.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件预览的上下文实体
 * @create 2023-09-09 11:49
 */
@Data
public class FilePreviewContext implements Serializable {


    private static final long serialVersionUID = -5429444528699618624L;
    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 请求响应对象
     */
    private HttpServletResponse response;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}
