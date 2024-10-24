package com.tangl.pan.server.modules.recycle.service;

import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.recycle.context.DeleteContext;
import com.tangl.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.tangl.pan.server.modules.recycle.context.RestoreContext;

import java.util.List;

/**
 * 回收站模块业务层
 */
public interface IRecycleService {
    /**
     * 查询用户的回收站文件列表
     *
     * @param context 上下文实体
     * @return 回收站文件列表
     */
    List<UserFileVO> recycles(QueryRecycleFileListContext context);

    /**
     * 回收站批量还原
     *
     * @param context 上下文实体
     */
    void restore(RestoreContext context);

    /**
     * 文件彻底删除
     *
     * @param context 上下文实体
     */
    void delete(DeleteContext context);
}
