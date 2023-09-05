package com.tangl.pan.server.modules.file.mapper;

import com.tangl.pan.server.modules.file.context.QueryFileListContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tangl
 * @description t_pan_user_file (用户文件信息表) 的数据库操作 Mapper
 * @create 2023-07-23 23:41:43
 * @entity com.tangl.pan.server.modules.file.entity.TPanUserFile
 */
public interface TPanUserFileMapper extends BaseMapper<TPanUserFile> {

    /**
     * 查询用户的文件列表
     *
     * @param context 查询文件列表的上下文实体
     * @return List<UserFileVO>
     */
    List<UserFileVO> selectFileList(@Param("param") QueryFileListContext context);
}
