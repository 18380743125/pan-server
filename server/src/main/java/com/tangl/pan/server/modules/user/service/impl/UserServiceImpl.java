package com.tangl.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.core.exception.TPanBusinessException;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.core.utils.PasswordUtil;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.converter.UserConverter;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.mapper.TPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * @author tangl
 * @description 用户业务层实现
 * @createDate 2023-07-23 23:38:02
 */
@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<TPanUserMapper, TPanUser> implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService userFileService;

    /**
     * 用户注册的业务实现
     * 需要实现的功能点：
     * 1、注册用户信息
     * 2、创建新用户的根本目录信息
     * 需要实现的技术难点：
     * 1、该业务是幂等的
     * 2、保证用户名全局唯一
     * 实现技术难点的处理方案
     * 1、幂等性通过数据库对于用户名字段添加唯一索引，上层业务捕获对应的冲突异常，转换返回
     *
     * @param userRegisterContext context
     * @return 用户ID
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 创建用户的根目录信息
     *
     * @param userRegisterContext context
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        userFileService.createFolder(createFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突
     *
     * @param userRegisterContext context
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        TPanUser entity = userRegisterContext.getEntity();
        if (Objects.isNull(entity)) {
            throw new TPanBusinessException(ResponseCode.ERROR);
        }
        try {
            if (!save(entity)) {
                throw new TPanBusinessException("用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new TPanBusinessException("用户名已存在");
        }
    }

    /**
     * 实体转换
     * 由上下文信息转换成用户实体
     *
     * @param userRegisterContext context
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        TPanUser entity = userConverter.userRegisterContext2TPanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt();
        String password = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(password);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);
    }
}




