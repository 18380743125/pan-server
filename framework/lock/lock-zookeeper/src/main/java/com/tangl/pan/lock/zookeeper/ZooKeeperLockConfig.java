package com.tangl.pan.lock.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

/**
 * @author tangl
 * @description 基于 zookeeper 的分布式锁配置类
 * @create 2023-09-24 16:04
 */
@SpringBootConfiguration
@Slf4j
public class ZooKeeperLockConfig {

    @Autowired
    private ZooKeeperLockProperties properties;

    /**
     * 配置zk的客户端
     *
     * @return CuratorFrameworkFactoryBean
     */
    @Bean
    public CuratorFrameworkFactoryBean curatorFrameworkFactoryBean() {
        return new CuratorFrameworkFactoryBean(properties.getHost());
    }

    /**
     * 配置zk分布式锁的注册器
     *
     * @param curatorFramework curatorFramework
     * @return LockRegistry
     */
    @Bean
    public LockRegistry zookeeperLockRegistry(CuratorFramework curatorFramework) {
        ZookeeperLockRegistry lockRegistry = new ZookeeperLockRegistry(curatorFramework);
        log.info("the zookeeper lock is loaded successfully!");
        return lockRegistry;
    }
}
