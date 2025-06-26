package com.tangl.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.exception.PanBusinessException;
import com.tangl.pan.core.exception.PanFrameworkException;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.core.utils.JwtUtil;
import com.tangl.pan.core.utils.PasswordUtil;
import com.tangl.pan.server.common.cache.AnnotationCacheService;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.user.constants.UserConstants;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.converter.UserConverter;
import com.tangl.pan.server.modules.user.entity.PanUser;
import com.tangl.pan.server.modules.user.mapper.PanUserMapper;
import com.tangl.pan.server.modules.user.service.IUserService;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户业务层实现
 */
@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<PanUserMapper, PanUser> implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Qualifier(value = "userAnnotationCacheService")
    private AnnotationCacheService<PanUser> cacheService;

    /**
     * 用户注册的业务实现
     * 需要实现的功能点：
     * 1、注册用户信息
     * 2、创建新用户的根目录
     * 需要实现的技术难点：
     * 1、该业务是幂等的
     * 2、保证用户名全局唯一
     * 实现技术难点的处理方案
     * 1、幂等性通过数据库对于用户名字段添加唯一索引，上层业务捕获对应的冲突异常，转换返回
     *
     * @param context context
     * @return 用户ID
     */
    @Override
    public Long register(UserRegisterContext context) {
        assembleUserEntity(context);
        doRegister(context);
        createUserRootFolder(context);
        return context.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * 1、用户的登录信息校验
     * 2、生成一个具有时效性的 accessToken
     * 3、将 accessToken 缓存起来, 去实现单机登录
     *
     * @param context userLoginContext
     * @return accessToken
     */
    @Override
    public String login(UserLoginContext context) {
        checkLoginInfo(context);
        generateAndSaveAccessToken(context);
        return context.getAccessToken();
    }

    /**
     * 用户退出登录
     * 1、清除用户的登录凭证缓存
     *
     * @param userId 用户ID
     */
    @Override
    public void exit(Long userId) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.PAN_CACHE_NAME);
            assert cache != null;
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            throw new PanBusinessException("退出登录失败");
        }
    }

    /**
     * 用户忘记密码-校验用户名
     *
     * @param context 上下文实体
     * @return 密保问题
     */
    @Override
    public String checkUsername(CheckUsernameContext context) {
        String question = baseMapper.selectQuestionByUsername(context.getUsername());
        if (StringUtils.isBlank(question)) {
            throw new PanBusinessException("没有此用户");
        }
        return question;
    }

    /**
     * 校验密保答案
     *
     * @param context 上下文实体
     * @return 校验通过后生成的 token 临时凭证
     */
    @Override
    public String checkAnswer(CheckAnswerContext context) {
        QueryWrapper<PanUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", context.getUsername());
        queryWrapper.eq("question", context.getQuestion());
        queryWrapper.eq("answer", context.getAnswer());
        int count = count(queryWrapper);
        if (count == 0) {
            throw new PanBusinessException("密保答案错误");
        }
        return generateCheckAnswerToken(context);
    }

    /**
     * 重置用户密码
     * 1、校验 token 是否有效
     * 2、重置密码
     *
     * @param context 上下文实体
     */
    @Override
    public void resetPassword(ResetPasswordContext context) {
        checkForgetPasswordToken(context);
        checkAndResetUserPassword(context);
    }

    /**
     * 在线修改密码
     * 1、校验旧密码
     * 2、重置新密码
     * 3、退出当前的登录状态
     *
     * @param context changePasswordContext
     */
    @Override
    public void changePassword(ChangePasswordContext context) {
        checkOldPassword(context);
        doChangePassword(context);
        exitLoginStatus(context);
    }

    /**
     * 查询用户的基本信息
     * 1、查询用户的基本信息实体
     * 2、查询用户的根文件夹信息
     * 3、拼装 vo 对象返回
     *
     * @param userId 用户ID
     * @return 用户的基本信息
     */
    @Override
    public UserInfoVO info(Long userId) {
        PanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new PanBusinessException("用户信息查询失败");
        }
        TPanUserFile userFile = getUserRootFileInfo(userId);
        if (Objects.isNull(userFile)) {
            throw new PanBusinessException("查询根文件夹信息失败");
        }

        return userConverter.assembleUserInfoVO(entity, userFile);
    }

    @Override
    public boolean removeById(Serializable id) {
        return cacheService.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        throw new PanBusinessException("请更换手动缓存");
    }

    @Override
    public boolean updateById(PanUser entity) {
        return cacheService.updateById(entity.getUserId(), entity);
    }

    @Override
    public boolean updateBatchById(Collection<PanUser> entityList) {
        throw new PanBusinessException("请更换手动缓存");
    }

    @Override
    public PanUser getById(Serializable id) {
        return cacheService.getById(id);
    }

    @Override
    public List<PanUser> listByIds(Collection<? extends Serializable> idList) {
        throw new PanBusinessException("请更换手动缓存");
    }

    /**
     * 获取用户根文件夹信息实体
     *
     * @param userId 用户ID
     * @return 用户的根目录
     */
    private TPanUserFile getUserRootFileInfo(Long userId) {
        return userFileService.getUserRootFile(userId);
    }

    /**
     * 退出用户的登录状态
     *
     * @param context 上下文实体
     */
    private void exitLoginStatus(ChangePasswordContext context) {
        exit(context.getUserId());
    }

    /**
     * 修改新密码
     *
     * @param context 上下文实体
     */
    private void doChangePassword(ChangePasswordContext context) {
        String newPassword = context.getNewPassword();
        PanUser entity = context.getEntity();
        String salt = entity.getSalt();
        String encNewPassword = PasswordUtil.encryptPassword(salt, newPassword);
        entity.setPassword(encNewPassword);
        if (!updateById(entity)) {
            throw new PanBusinessException("修改用户密码失败");
        }
    }

    /**
     * 校验用户旧密码
     * 查询并封装用户的实体信息到上下文对象中
     *
     * @param context 上下文实体
     */
    private void checkOldPassword(ChangePasswordContext context) {
        Long userId = context.getUserId();
        String oldPassword = context.getOldPassword();
        PanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new PanBusinessException("用户信息不存在");
        }
        context.setEntity(entity);
        String encOldPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        String dbOldPassword = entity.getPassword();
        if (!Objects.equals(encOldPassword, dbOldPassword)) {
            throw new PanBusinessException("旧密码不正确");
        }
    }

    /**
     * 校验用户信息并重置用户密码
     *
     * @param context 上下文实体
     */
    private void checkAndResetUserPassword(ResetPasswordContext context) {
        String username = context.getUsername();
        String password = context.getPassword();
        PanUser entity = getTPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new PanBusinessException("用户信息不存在");
        }
        String newDbPassword = PasswordUtil.encryptPassword(entity.getSalt(), password);
        entity.setPassword(newDbPassword);
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new PanBusinessException("重置用户密码失败");
        }
    }

    /**
     * 验证忘记密码的 token 是否有效
     *
     * @param context 上下文实体
     */
    private void checkForgetPasswordToken(ResetPasswordContext context) {
        String token = context.getToken();
        String username = context.getUsername();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        if (Objects.isNull(value)) {
            throw new PanBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = String.valueOf(value);
        if (!Objects.equals(tokenUsername, username)) {
            throw new PanBusinessException("token错误");
        }
    }

    /**
     * 用户忘记密码-校验密保答案通过的临时 token
     * token 失效的时间为 5 分钟之后
     *
     * @param context 上下文实体
     * @return 临时 token
     */
    private String generateCheckAnswerToken(CheckAnswerContext context) {
        return JwtUtil.generateToken(context.getUsername(), UserConstants.FORGET_USERNAME, context.getUsername(), UserConstants.FIVE_MINUTES_LONG);
    }

    /**
     * 生成并保存登录之后的凭证
     *
     * @param context 上下文实体
     */
    private void generateAndSaveAccessToken(UserLoginContext context) {
        PanUser entity = context.getEntity();
        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);
        Cache cache = cacheManager.getCache(CacheConstants.PAN_CACHE_NAME);
        if (Objects.isNull(cache)) throw new PanFrameworkException("获取缓存管理器失败");
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);
        context.setAccessToken(accessToken);
    }

    /**
     * 校验用户名密码
     *
     * @param context context
     */
    private void checkLoginInfo(UserLoginContext context) {
        String username = context.getUsername();
        String password = context.getPassword();
        PanUser entity = getTPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new PanBusinessException("用户名不存在");
        }
        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encPassword, dbPassword)) {
            throw new PanBusinessException("用户名或密码不正确");
        }
        context.setEntity(entity);
    }

    /**
     * 通过用户名获取用户实体信息
     *
     * @param username 用户名
     * @return entity
     */
    private PanUser getTPanUserByUsername(String username) {
        QueryWrapper<PanUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 创建用户的根目录信息
     *
     * @param context context
     */
    private void createUserRootFolder(UserRegisterContext context) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        createFolderContext.setUserId(context.getEntity().getUserId());
        userFileService.createFolder(createFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突
     *
     * @param context context
     */
    private void doRegister(UserRegisterContext context) {
        PanUser entity = context.getEntity();
        if (Objects.isNull(entity)) {
            throw new PanBusinessException(ResponseCode.ERROR);
        }
        try {
            if (!save(entity)) {
                throw new PanBusinessException("用户注册失败");
            }
        } catch (DuplicateKeyException e) {
            throw new PanBusinessException("用户名已存在");
        }
    }

    /**
     * 实体转换
     * 上下文信息转换成用户实体
     *
     * @param context 上下文实体
     */
    private void assembleUserEntity(UserRegisterContext context) {
        PanUser entity = userConverter.userRegisterContext2TPanUser(context);
        String salt = PasswordUtil.getSalt();
        String password = PasswordUtil.encryptPassword(salt, context.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(password);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        context.setEntity(entity);
    }
}
