package com.tangl.pan.server.modules.user.service;

import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author tangl
* @description 用户业务层
* @createDate 2023-07-23 23:38:02
*/
public interface IUserService extends IService<TPanUser> {

    Long register(UserRegisterContext userRegisterContext);
}
