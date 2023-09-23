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

    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    @Mapping(target = "password", ignore = true)
    TPanUser userRegisterContext2TPanUser(UserRegisterContext userRegisterContext);

    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);

    CheckUsernameContext checkUsernamePO2CheckUsernameContext(CheckUsernamePO checkUsernamePO);

    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "userFile.fileId", target = "rootFileId")
    @Mapping(source = "userFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(TPanUser user, TPanUserFile userFile);
}
