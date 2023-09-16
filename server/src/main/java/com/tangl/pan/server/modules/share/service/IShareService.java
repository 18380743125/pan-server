package com.tangl.pan.server.modules.share.service;

import com.tangl.pan.server.modules.share.context.CreateShareUrlContext;
import com.tangl.pan.server.modules.share.entity.TPanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;

/**
 * @author 25050
 * @description 针对表【t_pan_share(用户分享表)】的数据库操作Service
 * @createDate 2023-07-23 23:42:53
 */
public interface IShareService extends IService<TPanShare> {

    /**
     * 创建分享链接
     *
     * @param context 上下文实体
     * @return ShareUrlVO
     */
    ShareUrlVO create(CreateShareUrlContext context);
}
