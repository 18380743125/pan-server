// package com.tangl.pan.server.common.listener.search;
//
// import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
// import com.baomidou.mybatisplus.core.toolkit.Wrappers;
// import com.tangl.pan.core.utils.IdUtil;
// import com.tangl.pan.server.common.event.search.UserSearchEvent;
// import com.tangl.pan.server.modules.user.entity.PanUserSearchHistory;
// import com.tangl.pan.server.modules.user.service.IUserSearchHistoryService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.event.EventListener;
// import org.springframework.dao.DuplicateKeyException;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;
//
// import java.util.Date;
//
// /**
//  * 用户搜索文件事件监听器
//  */
// @Component
// public class UserSearchEventListener {
//
//     @Autowired
//     private IUserSearchHistoryService userSearchHistoryService;
//
//     /**
//      * 监听用户搜索文件事件，将其保存到用户的搜索历史中
//      *
//      * @param event 搜索事件
//      */
//     @EventListener(classes = UserSearchEvent.class)
//     @Async(value = "eventListenerTaskExecutor")
//     public void saveSearchHistory(UserSearchEvent event) {
//         PanUserSearchHistory record = new PanUserSearchHistory();
//         record.setId(IdUtil.get());
//         record.setUserId(event.getUserId());
//         record.setSearchContent(event.getKeyword());
//         record.setCreateTime(new Date());
//         record.setUpdateTime(new Date());
//
//         try {
//             userSearchHistoryService.save(record);
//         } catch (DuplicateKeyException e) {
//             UpdateWrapper<PanUserSearchHistory> updateWrapper = Wrappers.update();
//             updateWrapper.eq("user_id", event.getUserId());
//             updateWrapper.eq("search_content", event.getKeyword());
//             updateWrapper.set("update_time", new Date());
//             userSearchHistoryService.update(updateWrapper);
//         }
//     }
// }
