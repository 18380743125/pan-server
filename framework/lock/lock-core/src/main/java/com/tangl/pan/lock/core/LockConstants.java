package com.tangl.pan.lock.core;

/**
 * @author tangl
 * @description 锁相关公用常量类
 * @create 2023-09-24 0:44
 */
public interface LockConstants {

    /**
     * 公用 lock 的名称
     */
    String T_PAN_LOCK = "t-pan-lock;";

    /**
     * 公用 lock 的 path
     * 主要针对 zk 等节点型软件
     */
    String T_PAN_LOCK_PATH = "/t-pan-lock";
}
