// package com.tangl.pan.server.common.event.log;
//
// import lombok.*;
// import org.springframework.context.ApplicationEvent;
//
// /**
//  * 错误日志事件
//  */
// @Setter
// @Getter
// @EqualsAndHashCode(callSuper = false)
// @ToString
// public class ErrorLogEvent extends ApplicationEvent {
//
//     private static final long serialVersionUID = 4814882850368805113L;
//
//     /**
//      * 错误日志的内容
//      */
//     private String errorMsg;
//
//     /**
//      * 当前的登录用户ID
//      */
//     private Long userId;
//
//     public ErrorLogEvent(Object source, String errorMsg, Long userId) {
//         super(source);
//         this.errorMsg = errorMsg;
//         this.userId = userId;
//     }
//
//     public ErrorLogEvent(Object source) {
//         super(source);
//     }
// }
