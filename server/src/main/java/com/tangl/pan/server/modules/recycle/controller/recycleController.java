package com.tangl.pan.server.modules.recycle.controller;

import com.google.common.base.Splitter;
import com.tangl.pan.core.constants.PanConstants;
import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.recycle.context.DeleteContext;
import com.tangl.pan.server.modules.recycle.context.QueryRecycleFileListContext;
import com.tangl.pan.server.modules.recycle.context.RestoreContext;
import com.tangl.pan.server.modules.recycle.po.DeletePO;
import com.tangl.pan.server.modules.recycle.po.RestorePO;
import com.tangl.pan.server.modules.recycle.service.IRecycleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 回收站模块控制器
 */
@RestController
@Api(tags = "回收站模块")
@Validated
public class recycleController {

    @Autowired
    private IRecycleService recycleService;

    @ApiOperation(
            value = "获取回收站文件列表",
            notes = "该接口提供了获取回收站文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("recycles")
    public R<List<UserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(UserIdUtil.get());
        List<UserFileVO> result = recycleService.recycles(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "回收站批量还原",
            notes = "该接口提供了回收站批量还原的功能",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PutMapping("recycle/restore")
    public R<?> restore(@Validated @RequestBody RestorePO restorePO) {
        RestoreContext context = new RestoreContext();
        context.setUserId(UserIdUtil.get());
        String fileIds = restorePO.getFileIds();
        List<Long> fileIdList = Splitter.on(PanConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        recycleService.restore(context);
        return R.success();
    }

    @ApiOperation(
            value = "文件批量彻底删除",
            notes = "该接口提供了文件批量彻底删除的功能",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @DeleteMapping("recycle")
    public R<?> delete(@Validated @RequestBody DeletePO deletePO) {
        DeleteContext context = new DeleteContext();
        context.setUserId(UserIdUtil.get());
        String fileIds = deletePO.getFileIds();
        List<Long> fileIdList = Splitter.on(PanConstants.COMMON_SEPARATOR).splitToList(fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        recycleService.delete(context);
        return R.success();
    }
}
