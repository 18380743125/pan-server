package com.tangl.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.TPanUserSearchHistory;
import com.tangl.pan.server.modules.user.service.IUserSearchHistoryService;
import com.tangl.pan.server.modules.user.mapper.TPanUserSearchHistoryMapper;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 25050
 * @description 针对表【t_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
 * @createDate 2023-07-23 23:38:02
 */
@Service(value = "userSearchHistoryService")
public class UserSearchHistoryServiceImpl extends ServiceImpl<TPanUserSearchHistoryMapper, TPanUserSearchHistory> implements IUserSearchHistoryService {

    @Override
    public List<UserSearchHistoryVO> getUserSearchHistories(QueryUserSearchHistoryContext context) {
        return baseMapper.selectUserSearchHistories(context);
    }
}




