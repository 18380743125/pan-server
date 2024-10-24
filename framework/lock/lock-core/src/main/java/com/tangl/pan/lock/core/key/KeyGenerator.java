package com.tangl.pan.lock.core.key;

import com.tangl.pan.lock.core.LockContext;

/**
 * 锁的 key 生成器顶级接口
 */
public interface KeyGenerator {

    /**
     * 生成锁的 key
     *
     * @param lockContext 锁的上下文
     * @return key
     */
    String generateKey(LockContext lockContext);

}
