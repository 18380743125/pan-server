package com.tangl.pan.server.modules.user.converter;

import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.po.*;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tangl
 * @description 用户模块实体转换工具类
 * @create 2023-07-28 22:14
 */
@Mapper(componentModel = "spring")
public interface UserConverter {
    /**
     * UserRegisterPO 转 UserRegisterContext
     *
     * @param userRegisterPO po
     * @return UserRegisterPO
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * UserRegisterContext 转 TPanUser
     *
     * @param userRegisterContext userRegisterContext
     * @return TPanUser
     */
    @Mapping(target = "password", ignore = true)
    TPanUser userRegisterContext2TPanUser(UserRegisterContext userRegisterContext);

    /**
     * UserLoginPO 转 UserLoginContext
     *
     * @param userLoginPO userLoginPO
     * @return UserLoginContext
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);

    /**
     * CheckUsernamePO 转 CheckUsernameContext
     *
     * @param checkUsernamePO checkUsernamePO
     * @return UserLoginContext
     */
    CheckUsernameContext checkUsernamePO2CheckUsernameContext(CheckUsernamePO checkUsernamePO);

    /**
     * CheckAnswerPO 转 CheckAnswerContext
     *
     * @param checkAnswerPO checkAnswerPO
     * @return CheckAnswerContext
     */
    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    /**
     * ResetPasswordPO 转 ResetPasswordContext
     *
     * @param resetPasswordPO resetPasswordPO
     * @return ResetPasswordContext
     */
    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    /**
     * ChangePasswordPO 转 ChangePasswordContext
     *
     * @param changePasswordPO changePasswordPO
     * @return ChangePasswordContext
     */
    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);

    /**
     * 拼装用户基本信息返回实体
     *
     * @param user     user
     * @param userFile userFile
     * @return UserInfoVO
     */
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "userFile.fileId", target = "rootFileId")
    @Mapping(source = "userFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(TPanUser user, TPanUserFile userFile);
}
