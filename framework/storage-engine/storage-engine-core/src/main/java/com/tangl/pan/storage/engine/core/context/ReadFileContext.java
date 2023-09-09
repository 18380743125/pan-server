package com.tangl.pan.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serializable;

/**
 * @author tangl
 * @description 文件读取的上下文实体
 * @create 2023-09-09 12:37
 */
@Data
public class ReadFileContext implements Serializable {

    private static final long serialVersionUID = 2200297035246717177L;

    /**
     * 文件的真实存储路径
     */
    private String readPath;

    /**
     * 文件的输出流
     */
    OutputStream outputStream;
}
