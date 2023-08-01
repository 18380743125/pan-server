package com.tangl.pan.server.modules.user.mapper;

import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 25050
 * @description 针对表【t_pan_user(用户信息表)】的数据库操作Mapper
 * @createDate 2023-07-23 23:38:02
 * @Entity com.tangl.pan.server.modules.user.entity.TPanUser
 */
public interface TPanUserMapper extends BaseMapper<TPanUser> {

    /**
     * 通过用户名查询用户设置的密保问题
     *
     * @param username 用户名
     * @return question
     */
    String selectQuestionByUsername(@Param("username") String username);
}
