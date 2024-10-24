package com.tangl.pan.stream.core;

import java.util.Map;

/**
 * 消息发送者顶级接口
 */
public interface IStreamProducer {

    /**
     * 发生消息
     *
     * @param channelName 消息名称
     * @param deploy      负载
     */
    boolean sendMessage(String channelName, Object deploy);

    /**
     * 发生消息
     *
     * @param channelName 消息名称
     * @param deploy      负载
     * @param headers     消息头
     */
    boolean sendMessage(String channelName, Object deploy, Map<String, Object> headers);

}
