package com.tangl.pan.server.modules.user.mapper;

import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.PanUserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * pan_user_search_history (用户搜索历史表) 的数据库操作 Mapper
 */
public interface PanUserSearchHistoryMapper extends BaseMapper<PanUserSearchHistory> {

    /**
     * 查询用户的最近十条搜索历史记录
     *
     * @param context 上下文实体
     */
    List<UserSearchHistoryVO> selectUserSearchHistories(@Param("param") QueryUserSearchHistoryContext context);
}
