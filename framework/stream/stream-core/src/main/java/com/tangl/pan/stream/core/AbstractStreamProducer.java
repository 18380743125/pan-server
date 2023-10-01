package com.tangl.pan.stream.core;

import com.google.common.collect.Maps;
import com.tangl.pan.core.exception.TPanFrameworkException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * @author tangl
 * @description 消息发送者顶级抽象父类
 * @create 2023-09-24 17:55
 */
public abstract class AbstractStreamProducer implements IStreamProducer {

    @Autowired
    private Map<String, MessageChannel> channelMap;

    /**
     * 发送消息的前置钩子函数
     *
     * @param message message
     */
    protected abstract void preSend(Message<Object> message);

    /**
     * 发送消息的后置钩子函数
     *
     * @param message message
     * @param result  result
     */
    protected abstract void afterSend(Message<Object> message, boolean result);

    /**
     * 发生消息
     *
     * @param channelName 消息名称
     * @param deploy      负载
     */
    @Override
    public boolean sendMessage(String channelName, Object deploy) {
        return sendMessage(channelName, deploy, Maps.newHashMap());
    }

    /**
     * 发生消息
     * 1、参数校验
     * 2、执行发送前钩子函数
     * 3、执行发送的动作
     * 4、后置钩子函数
     * 5、返回结果
     *
     * @param channelName 消息名称
     * @param deploy      负载
     * @param headers     消息头
     */
    @Override
    public boolean sendMessage(String channelName, Object deploy, Map<String, Object> headers) {
        if (StringUtils.isBlank(channelName) || Objects.isNull(deploy)) {
            throw new TPanFrameworkException("the channelName or deploy can not be empty!");
        }
        if (MapUtils.isEmpty(channelMap)) {
            throw new TPanFrameworkException("the channelMap can not be empty!");
        }

        MessageChannel channel = channelMap.get(channelName);
        if (Objects.isNull(channel)) {
            throw new TPanFrameworkException("the channel named" + channelName + " can not be found!");
        }
        Message<Object> message = MessageBuilder.createMessage(deploy, new MessageHeaders(headers));
        preSend(message);
        boolean result = channel.send(message);
        afterSend(message, result);
        return result;
    }
}
