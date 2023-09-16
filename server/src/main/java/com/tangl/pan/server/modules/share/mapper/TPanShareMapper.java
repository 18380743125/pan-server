package com.tangl.pan.server.modules.share.mapper;

import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tangl
 * @description t_pan_share (用户分享表) 的数据库操作 Mapper
 * @create 2023-07-23 23:42:53
 * @entity com.tangl.pan.server.modules.share.entity.TPanShare
 */
public interface TPanShareMapper extends BaseMapper<TPanShare> {

    /**
     * @param userId 当前的登录用户 ID
     * @return List<ShareUrlListVO>
     */
    List<ShareUrlListVO> selectShareVOListByUserId(@Param("userId") Long userId);
}
