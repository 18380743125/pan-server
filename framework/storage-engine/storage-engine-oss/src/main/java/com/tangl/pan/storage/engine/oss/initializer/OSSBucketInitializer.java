package com.tangl.pan.storage.engine.oss.initializer;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.tangl.pan.core.exception.TPanFrameworkException;
import com.tangl.pan.storage.engine.oss.config.OSSStorageEngineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author tangl
 * @description OSS 桶初始化器
 * @create 2023-09-14 21:32
 */
@Component
@Slf4j
public class OSSBucketInitializer implements CommandLineRunner {

    @Autowired
    private OSSStorageEngineConfig config;

    @Autowired
    private OSSClient client;

    @Override
    public void run(String... args) throws Exception {
        boolean bucketExist = client.doesBucketExist(config.getBucketName());

        if (!bucketExist && config.getAutoCreateBucket()) {
            client.createBucket(config.getBucketName());
        }

        if (!bucketExist && !config.getAutoCreateBucket()) {
            throw new TPanFrameworkException("the bucket " + config.getBucketName() + " is not available");
        }

        log.info("the bucket " + config.getBucketName() + " has been created!");
    }
}
