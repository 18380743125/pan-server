package com.tangl.pan.server.modules.user.service;

import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.PanUserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;

import java.util.List;

/**
 * 用户搜索历史业务层
 */
public interface IUserSearchHistoryService extends IService<PanUserSearchHistory> {

    /**
     * 查询用户的搜索历史记录，默认十条
     *
     * @param context 上下文实体
     */
    List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context);

}
