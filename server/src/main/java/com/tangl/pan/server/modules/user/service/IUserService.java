package com.tangl.pan.server.modules.user.service;

import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;

/**
 * @author tangl
 * @description 用户业务层
 * @createDate 2023-07-23 23:38:02
 */
public interface IUserService extends IService<TPanUser> {

    /**
     * 用户注册业务
     *
     * @param userRegisterContext userRegisterContext
     * @return userId
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     *
     * @param userLoginContext userLoginContext
     * @return accessToken
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户退出登录业务
     *
     * @param userId userId
     */
    void exit(Long userId);

    /**
     * 校验用户名
     *
     * @param checkUsernameContext checkUsernameContext
     * @return question
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 校验密保答案
     *
     * @param checkAnswerContext checkAnswerContext
     * @return question
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 重置用户密码
     *
     * @param resetPasswordContext resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);

    /**
     * 用户在线修改密码
     *
     * @param changePasswordContext changePasswordContext
     */
    void changePassword(ChangePasswordContext changePasswordContext);

    /**
     * 查询登录用户的基本信息
     *
     * @param userId 用户 ID
     * @return UserInfoVO
     */
    UserInfoVO info(Long userId);
}
