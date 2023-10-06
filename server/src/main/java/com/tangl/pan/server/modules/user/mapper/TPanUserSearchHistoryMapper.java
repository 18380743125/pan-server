package com.tangl.pan.server.modules.user.mapper;

import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.entity.TPanUserSearchHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author tangl
 * @description t_pan_user_search_history (用户搜索历史表) 的数据库操作 Mapper
 * @create 2023-07-23 23:38:02
 * @entity com.tangl.pan.server.modules.user.entity.TPanUserSearchHistory
 */
public interface TPanUserSearchHistoryMapper extends BaseMapper<TPanUserSearchHistory> {

    /**
     * 查询用户的最近十条搜索历史记录
     *
     * @param context 上下文实体
     */
    List<UserSearchHistoryVO> selectUserSearchHistories(@Param("param") QueryUserSearchHistoryContext context);
}
