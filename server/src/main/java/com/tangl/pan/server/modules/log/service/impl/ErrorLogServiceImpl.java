package com.tangl.pan.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.server.modules.log.entity.PanErrorLog;
import com.tangl.pan.server.modules.log.mapper.PanErrorLogMapper;
import com.tangl.pan.server.modules.log.service.IErrorLogService;
import org.springframework.stereotype.Service;

/**
 * 错误日志表业务层
 */
@Service
public class ErrorLogServiceImpl extends ServiceImpl<PanErrorLogMapper, PanErrorLog>
        implements IErrorLogService {

}
