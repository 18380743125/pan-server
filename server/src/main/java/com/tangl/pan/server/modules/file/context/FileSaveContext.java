package com.tangl.pan.server.modules.file.context;

import com.tangl.pan.server.modules.file.entity.TPanFile;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author tangl
 * @description 保存单文件的上下文实体
 * @create 2023-08-14 22:20
 */
@Data
public class FileSaveContext implements Serializable {
    private static final long serialVersionUID = -3878820238714733463L;

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

    /**
     * 文件上传的物理路径
     */
    private String realPath;
}
