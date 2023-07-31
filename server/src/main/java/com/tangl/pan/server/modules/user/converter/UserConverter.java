package com.tangl.pan.server.modules.user.converter;

import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.po.UserRegisterPO;
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
     * @param userRegisterPO po
     * @return userRegisterPO
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    @Mapping(target = "password", ignore = true)
    TPanUser userRegisterContext2TPanUser(UserRegisterContext userRegisterContext);
}
