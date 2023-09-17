package com.tangl.pan.server.modules.share.context;

import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author tangl
 * @description 分享文件下载的上下文实体
 * @create 2023-09-17 16:44
 */
@Data
public class ShareFileDownloadContext implements Serializable {

    private static final long serialVersionUID = -2300188732280974774L;

    /**
     * 要下载的文件 ID
     */
    private Long fileId;

    /**
     * 当前登录的用户 ID
     */
    private Long userId;

    /**
     * 分享的 ID
     */
    private Long shareId;

    /**
     * 响应实体
     */
    private HttpServletResponse response;
}
