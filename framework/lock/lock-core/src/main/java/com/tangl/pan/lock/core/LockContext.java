package com.tangl.pan.lock.core;

import com.tangl.pan.lock.core.annotation.Lock;
import lombok.Data;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author tangl
 * @description 锁实体的上下文信息
 * 主要做切点的实体解析，为整体逻辑所公用
 * @create 2023-09-24 0:53
 */
@Data
public class LockContext {

    /**
     * 切点方法所属类名称
     */
    private String className;

    /**
     * 切点方法的名称
     */
    private String methodName;

    /**
     * 切点方法上标注的自定义锁注解
     */
    private Lock annotation;

    /**
     * 类的 Class 对象
     */
    private Class<?> classType;

    /**
     * 当前调用方法的实体
     */
    private Method method;

    /**
     * 参数列表实体
     */
    private Object[] args;

    /**
     * 参数列表类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 代理对象实体
     */
    private Object target;

    /**
     * 初始化实体对象
     *
     * @param proceedingJoinPoint joinPoint
     * @return LockContext
     */
    public static LockContext init(ProceedingJoinPoint proceedingJoinPoint) {
        LockContext lockContext = new LockContext();
        doInit(lockContext, proceedingJoinPoint);
        return lockContext;
    }

    /**
     * 执行初始化的动作
     *
     * @param lockContext         lockContext
     * @param proceedingJoinPoint joinPoint
     */
    private static void doInit(LockContext lockContext, ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        Object[] args = proceedingJoinPoint.getArgs();
        Object target = proceedingJoinPoint.getTarget();
        String methodName = signature.getName();
        Class<?> classType = signature.getDeclaringType();
        String className = signature.getDeclaringTypeName();
        Class<?>[] parameterTypes = ((MethodSignature) signature).getParameterTypes();
        Method method = ((MethodSignature) signature).getMethod();
        Lock annotation = method.getAnnotation(Lock.class);

        lockContext.setArgs(args);
        lockContext.setTarget(target);
        lockContext.setMethodName(methodName);
        lockContext.setClassType(classType);
        lockContext.setClassName(className);
        lockContext.setParameterTypes(parameterTypes);
        lockContext.setAnnotation(annotation);
        lockContext.setMethod(method);
    }

}
