package com.tangl.pan.lock.core;

/**
 * 锁相关公用常量类
 */
public interface LockConstants {

    /**
     * 公用 lock 的名称
     */
    String PAN_LOCK = "pan-lock;";

    /**
     * 公用 lock 的 path
     * 主要针对 zk 等节点型软件
     */
    String PAN_LOCK_PATH = "/pan-lock";
}
