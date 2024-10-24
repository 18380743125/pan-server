package com.tangl.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.PanUserSearchHistory;
import com.tangl.pan.server.modules.user.service.IUserSearchHistoryService;
import com.tangl.pan.server.modules.user.mapper.PanUserSearchHistoryMapper;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户搜索历史业务层
 */
@Service(value = "userSearchHistoryService")
public class UserSearchHistoryServiceImpl extends ServiceImpl<PanUserSearchHistoryMapper, PanUserSearchHistory> implements IUserSearchHistoryService {

    @Override
    public List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context) {
        return baseMapper.selectUserSearchHistories(context);
    }
}




