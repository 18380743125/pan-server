package com.tangl.pan.server.modules.user.service;

import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.TPanUserSearchHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;

import java.util.List;

/**
 * @author tangl
 * @description 用户搜索历史业务层
 * @createDate 2023-07-23 23:38:02
 */
public interface IUserSearchHistoryService extends IService<TPanUserSearchHistory> {

    /**
     * 查询用户的搜索历史记录，默认十条
     *
     * @param context 上下文实体
     */
    List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context);

}
