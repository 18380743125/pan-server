package com.tangl.pan.server.modules.share.share;

import com.google.common.base.Splitter;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.modules.share.context.CreateShareUrlContext;
import com.tangl.pan.server.modules.share.convert.ShareConvert;
import com.tangl.pan.server.modules.share.po.CreateShareUrlPO;
import com.tangl.pan.server.modules.share.service.IShareService;
import com.tangl.pan.server.modules.share.vo.ShareUrlVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
