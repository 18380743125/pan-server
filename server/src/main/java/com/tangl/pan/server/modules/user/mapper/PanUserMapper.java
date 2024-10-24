package com.tangl.pan.server.modules.user.mapper;

import com.tangl.pan.server.modules.user.entity.PanUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * pan_user (用户信息表) 的数据库操作 Mapper
 */
public interface PanUserMapper extends BaseMapper<PanUser> {

    /**
     * 通过用户名查询用户的密保问题
     *
     * @param username 用户名
     * @return 密保答案
     */
    String selectQuestionByUsername(@Param("username") String username);
}
