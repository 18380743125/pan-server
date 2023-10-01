package com.tangl.pan.server.common.stream.channel;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author tangl
 * @description 事件通道定义类
 * @create 2023-09-24 21:34
 */
public interface PanChannels {

    String TEST_INPUT = "testInput";

    String TEST_OUTPUT = "testOutput";

    String ERROR_LOG_INPUT = "errorLogInput";

    String ERROR_LOG_OUTPUT = "errorLogOutput";

    String FILE_DELETE_INPUT = "fileDeleteInput";

    String FILE_DELETE_OUTPUT = "fileDeleteOutput";

    String FILE_RESTORE_INPUT = "fileRestoreInput";

    String FILE_RESTORE_OUTPUT = "fileRestoreOutput";

    String PHYSICAL_FILE_DELETE_INPUT = "physicalFileDeleteInput";

    String PHYSICAL_FILE_DELETE_OUTPUT = "physicalFileDeleteOutput";

    String USER_SEARCH_INPUT = "userSearchInput";

    String USER_SEARCH_OUTPUT = "userSearchOutput";

    @Input(TEST_INPUT)
    SubscribableChannel testInput();

    @Output(TEST_OUTPUT)
    MessageChannel testOutput();

    @Input(ERROR_LOG_INPUT)
    SubscribableChannel errorLogInput();

    @Output(ERROR_LOG_OUTPUT)
    MessageChannel errorLogOutput();

    @Input(FILE_DELETE_INPUT)
    SubscribableChannel fileDeleteInput();

    @Output(FILE_DELETE_OUTPUT)
    MessageChannel fileDeleteOutput();

    @Input(FILE_RESTORE_INPUT)
    SubscribableChannel fileRestoreInput();

    @Output(FILE_RESTORE_OUTPUT)
    MessageChannel fileRestoreOutput();

    @Input(PHYSICAL_FILE_DELETE_INPUT)
    SubscribableChannel physicalFileDeleteInput();

    @Output(PHYSICAL_FILE_DELETE_OUTPUT)
    MessageChannel physicalFileDeleteOutput();

    @Input(USER_SEARCH_INPUT)
    SubscribableChannel userSearchInput();

    @Output(USER_SEARCH_OUTPUT)
    MessageChannel userSearchOutput();

}
