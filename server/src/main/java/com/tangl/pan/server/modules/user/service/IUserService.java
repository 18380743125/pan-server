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
     * 用户注册
     *
     * @param context 上下文实体
     * @return userId
     */
    Long register(UserRegisterContext context);

    /**
     * 用户登录
     *
     * @param context 上下文实体
     * @return accessToken
     */
    String login(UserLoginContext context);

    /**
     * 用户退出登录
     *
     * @param userId 用户ID
     */
    void exit(Long userId);

    /**
     * 用户忘记密码-校验用户名
     *
     * @param context 上下文实体
     * @return 密保问题
     */
    String checkUsername(CheckUsernameContext context);

    /**
     * 用户忘记密码-校验密保答案
     *
     * @param context 上下文实体
     * @return 校验通过后生成的 token 临时凭证
     */
    String checkAnswer(CheckAnswerContext context);

    /**
     * 用户忘记密码-重置用户密码
     *
     * @param context 上下文实体
     */
    void resetPassword(ResetPasswordContext context);

    /**
     * 用户在线修改密码
     *
     * @param context 上下文实体
     */
    void changePassword(ChangePasswordContext context);

    /**
     * 查询登录用户的基本信息
     *
     * @param userId 用户ID
     * @return 用户的基本信息
     */
    UserInfoVO info(Long userId);
}
