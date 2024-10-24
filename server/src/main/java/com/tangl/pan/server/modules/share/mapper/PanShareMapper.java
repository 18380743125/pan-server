package com.tangl.pan.server.modules.share.mapper;

import com.tangl.pan.server.modules.share.entity.PanShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * pan_share (用户分享表) 的数据库操作 Mapper
 */
public interface PanShareMapper extends BaseMapper<PanShare> {

    /**
     * @param userId 当前的登录用户 ID
     * @return List<ShareUrlListVO>
     */
    List<ShareUrlListVO> selectShareVOListByUserId(@Param("userId") Long userId);

    /**
     * 滚动查询已存在的分享 ID
     *
     * @param startId 开始
     * @param limit   条数
     * @return 分享 ID 列表
     */
    List<Long> rollingQueryShareId(@Param("startId") long startId, @Param("limit") long limit);
}
