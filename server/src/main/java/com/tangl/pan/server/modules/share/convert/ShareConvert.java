package com.tangl.pan.server.modules.share.convert;

import com.tangl.pan.server.modules.share.context.CreateShareUrlContext;
import com.tangl.pan.server.modules.share.po.CreateShareUrlPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tangl
 * @description 分享模块实体转化工具类
 * @create 2023-09-16 15:39
 */
@Mapper(componentModel = "spring")
public interface ShareConvert {
    @Mapping(target = "userId", expression = "java(com.tangl.pan.server.common.utils.UserIdUtil.get())")
    CreateShareUrlContext createShareUrlPO2CreateShareUrlContext(CreateShareUrlPO createShareUrlPO);
}
