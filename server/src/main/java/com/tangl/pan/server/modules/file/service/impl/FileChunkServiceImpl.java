package com.tangl.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.server.modules.file.entity.TPanFileChunk;
import com.tangl.pan.server.modules.file.service.IFileChunkService;
import com.tangl.pan.server.modules.file.mapper.TPanFileChunkMapper;
import org.springframework.stereotype.Service;

/**
* @author 25050
* @description 针对表【t_pan_file_chunk(文件分片信息表)】的数据库操作Service实现
* @createDate 2023-07-23 23:41:43
*/
@Service
public class FileChunkServiceImpl extends ServiceImpl<TPanFileChunkMapper, TPanFileChunk>
    implements IFileChunkService {

}




