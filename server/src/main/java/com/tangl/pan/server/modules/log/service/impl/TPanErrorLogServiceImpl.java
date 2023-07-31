package com.tangl.pan.server.modules.log.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.server.modules.log.entity.TPanErrorLog;
import com.tangl.pan.server.modules.log.service.TPanErrorLogService;
import com.tangl.pan.server.modules.log.mapper.TPanErrorLogMapper;
import org.springframework.stereotype.Service;

/**
* @author 25050
* @description 针对表【t_pan_error_log(错误日志表)】的数据库操作Service实现
* @createDate 2023-07-23 23:42:07
*/
@Service
public class TPanErrorLogServiceImpl extends ServiceImpl<TPanErrorLogMapper, TPanErrorLog>
    implements TPanErrorLogService{

}




