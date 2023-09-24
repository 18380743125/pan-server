package com.tangl.pan.lock.core.key;

import com.tangl.pan.lock.core.LockContext;

/**
 * @author tangl
 * @description 锁的 key 生成器顶级接口
 * @create 2023-09-24 0:51
 */
public interface KeyGenerator {

    /**
     * 生成锁的 key
     * @param lockContext 锁的上下文
     * @return key
     */
    String generateKey(LockContext lockContext);

}
