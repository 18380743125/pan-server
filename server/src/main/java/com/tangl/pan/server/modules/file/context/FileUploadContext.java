package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author tangl
 * @description 单文件上传的上下文实体
 * @create 2023-08-14 21:56
 */
@Data
public class FileUploadContext implements Serializable {

    private static final long serialVersionUID = 5827412689710890833L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件大小
     */
    private Long totalSize;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 要上传的文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件实体记录
     */
    private TPanFile record;
}
