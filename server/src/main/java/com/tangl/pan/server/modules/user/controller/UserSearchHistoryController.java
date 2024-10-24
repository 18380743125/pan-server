package com.tangl.pan.server.modules.user.controller;

import com.tangl.pan.core.response.R;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.user.context.QueryUserSearchHistoryContext;
import com.tangl.pan.server.modules.user.service.IUserSearchHistoryService;
import com.tangl.pan.server.modules.user.vo.UserSearchHistoryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户搜索历史控制器
 */
@Api(tags = "用户搜索历史模块")
@RestController
public class UserSearchHistoryController {

    @Autowired
    private IUserSearchHistoryService userSearchHistoryService;

    @ApiOperation(
            value = "获取用户最新的搜索历史记录，默认十条",
            notes = "该接口提供了获取用户最新的搜索历史记录的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("user/search/histories")
    public R<List<UserSearchHistoryVO>> getUserSearchHistories() {
        QueryUserSearchHistoryContext context = new QueryUserSearchHistoryContext();
        context.setUserId(UserIdUtil.get());
        List<UserSearchHistoryVO> result = userSearchHistoryService.getUserSearchHistories(context);
        return R.data(result);
    }

}
