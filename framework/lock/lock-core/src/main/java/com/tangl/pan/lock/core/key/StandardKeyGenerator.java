package com.tangl.pan.lock.core.key;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.tangl.pan.lock.core.LockContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标准的 key 生成器
 */
@Component
public class StandardKeyGenerator extends AbstractKeyGenerator {

    /**
     * 标准 key 的生成方法
     * 生成格式：className:methodName:parameterType1:...:value1:value2...
     *
     * @param lockContext lockContext
     * @param keyValueMap keyValueMap
     * @return 锁的 key
     */
    @Override
    protected String doGeneratorKey(LockContext lockContext, Map<String, String> keyValueMap) {
        List<String> keyList = Lists.newArrayList();
        keyList.add(lockContext.getClassName());
        keyList.add(lockContext.getMethodName());

        Class<?>[] parameterTypes = lockContext.getParameterTypes();
        if (ArrayUtils.isNotEmpty(parameterTypes)) {
            for (Class<?> parameterType : parameterTypes) {
                keyList.add(parameterType.toString());
            }
        } else {
            keyList.add(Void.class.toString());
        }

        Collection<String> values = keyValueMap.values();
        if (CollectionUtils.isNotEmpty(values)) {
            keyList.addAll(values);
        }

//        return keyList.stream().collect(Collectors.joining(","));
        return Joiner.on(",").join(keyList);
    }

}
