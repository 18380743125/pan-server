package com.tangl.pan.server.common.aspect;

import com.tangl.pan.cache.core.constants.CacheConstants;
import com.tangl.pan.core.response.R;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.JwtUtil;
import com.tangl.pan.server.common.annotation.LoginIgnore;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.user.constants.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 统一的登录拦截校验切面逻辑实现类
 */
@Component
@Aspect
@Slf4j
public class CommonLoginAspect {
    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证 key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    private static final String POINT_CUT = "execution(* com.tangl.pan.server.modules.*.controller..*(..))";

    @Autowired
    private CacheManager cacheManager;

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {
    }

    /**
     * 切点的环绕增强逻辑
     * 1、判断需不需要校验登录信息
     * 2、校验登录信息
     * a、获取 token 从请求头或者参数
     * b、从缓存中获取 token 进行比对
     * c、解析 token
     * d、解析的 userId 存入线程上下文中, 供下游使用
     *
     * @param joinPoint joinPoint
     */
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (checkNeedCheckLoginInfo(joinPoint)) {
            // 登录信息校验流程
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert servletRequestAttributes != null;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestURI = request.getRequestURI();
            log.info("成功拦截到请求, URI为：{}", requestURI);
            if (!checkAndSaveUserId(request)) {
                log.warn("成功拦截到请求, URI为{}, 检测到用户未登录, 将跳转至登录页面", requestURI);
                return R.fail(ResponseCode.NEED_LOGIN);
            }
            log.info("成功拦截到请求, URI为：{}, 请求通过", requestURI);
        }
        return joinPoint.proceed();
    }

    /**
     * 校验 token 并 提取 userId
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        if (Objects.isNull(userId)) {
            return false;
        }
        Cache cache = cacheManager.getCache(CacheConstants.PAN_CACHE_NAME);
        assert cache != null;
        String redisAccessToken = cache.get(UserConstants.USER_LOGIN_PREFIX + userId, String.class);
        if (Objects.isNull(redisAccessToken)) {
            return false;
        }
        if (Objects.equals(accessToken, redisAccessToken)) {
            saveUserId(userId);
            return true;
        }
        return false;
    }

    /**
     * 保存用户 ID 到线程上下文中
     *
     * @param userId 用户 ID
     */
    private void saveUserId(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }

    /**
     * 校验是否需要校验登录信息
     *
     * @param joinPoint joinPoint
     * @return true 需要校验登录信息  false 不需要
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
