package com.tangl.pan.lock.core.key;

import com.google.common.collect.Maps;
import com.tangl.pan.core.utils.SpElUtil;
import com.tangl.pan.lock.core.LockContext;
import com.tangl.pan.lock.core.annotation.Lock;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Map;

/**
 * 锁的 key 生成器的公用父类
 */
public abstract class AbstractKeyGenerator implements KeyGenerator {

    /**
     * 具体逻辑由子类实现
     *
     * @param lockContext lockContext
     * @param keyValueMap keyValueMap
     * @return 锁的 key
     */
    protected abstract String doGeneratorKey(LockContext lockContext, Map<String, String> keyValueMap);

    /**
     * 生成锁的 key
     *
     * @param lockContext 锁的上下文
     * @return key
     */
    @Override
    public String generateKey(LockContext lockContext) {
        Lock annotation = lockContext.getAnnotation();
        String[] keys = annotation.keys();
        Map<String, String> keyValueMap = Maps.newHashMap();
        if (ArrayUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                keyValueMap.put(key,
                        SpElUtil.getStringValue(key,
                                lockContext.getClassName(),
                                lockContext.getMethodName(),
                                lockContext.getClassType(),
                                lockContext.getMethod(),
                                lockContext.getArgs(),
                                lockContext.getParameterTypes(),
                                lockContext.getTarget()
                        ));
            }
        }
        return doGeneratorKey(lockContext, keyValueMap);
    }
}
