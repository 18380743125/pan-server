package com.tangl.pan.server.common.stream.consumer.search;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.stream.channel.PanChannels;
import com.tangl.pan.server.common.stream.event.search.UserSearchEvent;
import com.tangl.pan.server.modules.user.entity.PanUserSearchHistory;
import com.tangl.pan.server.modules.user.service.IUserSearchHistoryService;
import com.tangl.pan.stream.core.AbstractConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户搜索文件事件监听器
 */
@Component
public class UserSearchEventConsumer extends AbstractConsumer {

    @Autowired
    private IUserSearchHistoryService userSearchHistoryService;

    /**
     * 监听用户搜索文件事件，将其保存到用户的搜索历史中
     *
     * @param message 搜索事件的消息对象
     */
    @StreamListener(PanChannels.USER_SEARCH_INPUT)
    public void saveSearchHistory(Message<UserSearchEvent> message) {
        if (isEmptyMessage(message)) {
            return;
        }
        printLog(message);
        UserSearchEvent event = message.getPayload();
        PanUserSearchHistory record = new PanUserSearchHistory();
        record.setId(IdUtil.get());
        record.setUserId(event.getUserId());
        record.setSearchContent(event.getKeyword());
        record.setCreateTime(new Date());
        record.setUpdateTime(new Date());
        try {
            userSearchHistoryService.save(record);
        } catch (DuplicateKeyException e) {
            UpdateWrapper<PanUserSearchHistory> updateWrapper = Wrappers.update();
            updateWrapper.eq("user_id", event.getUserId());
            updateWrapper.eq("search_content", event.getKeyword());
            updateWrapper.set("update_time", new Date());
            userSearchHistoryService.update(updateWrapper);
        }
    }
}
