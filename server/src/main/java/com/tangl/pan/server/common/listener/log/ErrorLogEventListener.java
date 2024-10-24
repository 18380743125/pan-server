// package com.tangl.pan.server.common.listener.log;
//
// import com.tangl.pan.core.utils.IdUtil;
// import com.tangl.pan.server.common.event.log.ErrorLogEvent;
// import com.tangl.pan.server.modules.log.entity.PanErrorLog;
// import com.tangl.pan.server.modules.log.service.IErrorLogService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.event.EventListener;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;
//
// import java.util.Date;
//
// /**
//  * 系统错误日志监听器
//  */
// @Component
// public class ErrorLogEventListener {
//
//     @Autowired
//     private IErrorLogService iErrorLogService;
//
//     /**
//      * 监听系统错误日志事件，并保存到数据库中
//      *
//      * @param event 错误日志事件对象
//      */
//     @EventListener(EventListener.class)
//     @Async(value = "eventListenerTaskExecutor")
//     public void saveErrorLog(ErrorLogEvent event) {
//         PanErrorLog record = new PanErrorLog();
//         record.setId(IdUtil.get());
//         record.setLogContent(event.getErrorMsg());
//         record.setLogStatus(0);
//         record.setCreateUser(event.getUserId());
//         record.setCreateTime(new Date());
//         record.setUpdateTime(new Date());
//         iErrorLogService.save(record);
//     }
// }
