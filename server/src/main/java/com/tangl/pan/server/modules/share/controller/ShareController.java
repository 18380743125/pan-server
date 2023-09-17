package com.tangl.pan.server.modules.share.controller;

import com.google.common.base.Splitter;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.annotation.LoginIgnore;
import com.tangl.pan.server.common.annotation.NeedShareCode;
import com.tangl.pan.server.common.utils.ShareIdUtil;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.share.context.*;
import com.tangl.pan.server.modules.share.convert.ShareConvert;
import com.tangl.pan.server.modules.share.po.CancelSharePO;
import com.tangl.pan.server.modules.share.po.CheckShareCodePO;
import com.tangl.pan.server.modules.share.po.CreateShareUrlPO;
import com.tangl.pan.server.modules.share.vo.ShareDetailVO;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.vo.ShareSimpleDetailVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlListVO;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 分享模块控制器
 * @create 2023-09-16 15:37
 */
@Api(tags = "分享模块")
@RestController
public class ShareController {

    @Autowired
    private IShareService shareService;

    @Autowired
    private ShareConvert shareConvert;

    @ApiOperation(
            value = "创建分享链接",
            notes = "该接口提供了创建分享链接的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("share")
    public R<ShareUrlVO> create(@Validated @RequestBody CreateShareUrlPO createShareUrlPO) {
        CreateShareUrlContext context = shareConvert.createShareUrlPO2CreateShareUrlContext(createShareUrlPO);

        String shareFileIds = createShareUrlPO.getShareFileIds();
        List<Long> shareFileIdList = Splitter.on(TPanConstants.COMMON_SEPARATOR).splitToList(shareFileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());

        context.setShareFileIdList(shareFileIdList);

        ShareUrlVO vo = shareService.create(context);
        return R.data(vo);
    }

    @ApiOperation(
            value = "查询分享链接列表",
            notes = "该接口提供了查询分享链接列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("shares")
    public R<List<ShareUrlListVO>> getShares() {
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(UserIdUtil.get());
        List<ShareUrlListVO> result = shareService.getShares(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "取消分享",
            notes = "该接口提供了取消分享的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @DeleteMapping("share")
    public R<?> cancelShare(@Validated @RequestBody CancelSharePO cancelSharePO) {
        CancelShareContext context = new CancelShareContext();
        context.setUserId(UserIdUtil.get());
        String shareIds = cancelSharePO.getShareIds();
        List<Long> shareIdList = Splitter.on(TPanConstants.COMMON_SEPARATOR).splitToList(shareIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setShareIdList(shareIdList);
        shareService.cancelShare(context);
        return R.success();
    }

    @ApiOperation(
            value = "校验分享码",
            notes = "该接口提供了校验分享码的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @PostMapping("share/code/check")
    public R<String> checkShareCode(@Validated @RequestBody CheckShareCodePO checkShareCodePO) {
        CheckShareCodeContext context = new CheckShareCodeContext();
        context.setShareId(IdUtil.decrypt(checkShareCodePO.getShareId()));
        context.setShareCode(checkShareCodePO.getShareCode());
        String token = shareService.checkShareCode(context);
        return R.data(token);
    }

    @ApiOperation(
            value = "查询分享详情",
            notes = "该接口提供了查询分享详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @LoginIgnore
    @NeedShareCode
    @GetMapping("share")
    public R<ShareDetailVO> detail() {
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVO vo = shareService.detail(context);
        return R.data(vo);
    }

    @ApiOperation(
            value = "查询分享的简单详情",
            notes = "该接口提供了查询分享的简单详情的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("share/simple")
    public R<ShareSimpleDetailVO> simpleDetail(@NotBlank(message = "分享的ID不能为空") @RequestParam(value = "shareId", required = false) String shareId) {
        QueryShareSimpleDetailContext context = new QueryShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVO vo = shareService.simpleDetail(context);
        return R.data(vo);
    }
}
