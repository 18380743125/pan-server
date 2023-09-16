package com.tangl.pan.server.common.aspect;

import com.tangl.pan.core.response.R;
import com.tangl.pan.core.response.ResponseCode;
import com.tangl.pan.core.utils.JwtUtil;
import com.tangl.pan.server.common.utils.ShareIdUtil;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.share.constants.ShareConstants;
import com.tangl.pan.server.modules.user.constants.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author tangl
 * @description 统一的分享码校验切面逻辑实现类
 * @create 2023-09-16 23:20
 */
@Component
@Aspect
@Slf4j
public class ShareCodeAspect {

    /**
     * 分享 token 参数名称
     */
    private static final String SHARE_CODE_AUTH_PARAM_NAME = "shareToken";

    /**
     * 分享 token 请求头 key
     */
    private static final String SHARE_CODE_AUTH_REQUEST_HEADER_NAME = "Share-Token";

    /**
     * 切点表达式
     */
    private final static String POINT_CUT = "annotation(com.tangl.pan.server.common.annotation.NeedShareCode)";

    @Pointcut(value = POINT_CUT)
    public void shareCodeAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * 1、判断需不需要校验分享 token 信息
     * 2、校验分享 token 信息
     * a、获取 token 从请求头或者参数
     * b、解析 token
     * c、解析的 shareId 存入线程上下文中, 供下游使用
     *
     * @param joinPoint joinPoint
     */
    @Around("shareCodeAuth()")
    public Object shareCodeAuthAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 分享信息校验流程
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(servletRequestAttributes).getRequest();
        String requestURI = request.getRequestURI();
        log.info("成功拦截到请求, URI为：{}", requestURI);
        if (!checkAndSaveShareId(request)) {
            log.warn("成功拦截到请求, URI为{}, 检测到分享码失效, 将跳转至分享码校验页面", requestURI);
            return R.fail(ResponseCode.ACCESS_DENIED);
        }
        log.info("成功拦截到请求, URI为：{}, 请求通过", requestURI);

        return joinPoint.proceed();
    }

    /**
     * 校验 token 并 提取 userId
     *
     * @param request HttpServletRequest
     * @return boolean
     */
    private boolean checkAndSaveShareId(HttpServletRequest request) {
        String accessToken = request.getHeader(SHARE_CODE_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(SHARE_CODE_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        Object shareId = JwtUtil.analyzeToken(accessToken, ShareConstants.SHARE_ID);
        if (Objects.isNull(shareId)) {
            return false;
        }
        saveShareId(shareId);
        return true;
    }

    /**
     * 保存分享 ID 到线程上下文中
     *
     * @param shareId 分享 ID
     */
    private void saveShareId(Object shareId) {
        ShareIdUtil.set(Long.valueOf(String.valueOf(shareId)));
    }
}
