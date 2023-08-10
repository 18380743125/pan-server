package com.tangl.pan.server.modules.file.controller;

import com.google.common.base.Splitter;
import com.tangl.pan.core.constants.TPanConstants;
import com.tangl.pan.core.response.R;
import com.tangl.pan.core.utils.IdUtil;
import com.tangl.pan.server.common.utils.UserIdUtil;
import com.tangl.pan.server.modules.file.constants.FileConstants;
import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.context.QueryFileListContext;
import com.tangl.pan.server.modules.file.converter.FileConverter;
import com.tangl.pan.server.modules.file.enums.DelFlagEnum;
import com.tangl.pan.server.modules.file.po.CreateFolderPO;
import com.tangl.pan.server.modules.file.service.IUserFileService;
import com.tangl.pan.server.modules.file.vo.UserFileVO;
import com.tangl.pan.server.modules.user.service.IUserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;

/**
 * @author tangl
 * @description 文件模块的控制器
 * @create 2023-08-10 20:34
 */
@RestController
@Validated
public class FileController {
    @Autowired
    private IUserService userService;

    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private FileConverter fileConverter;

    @ApiOperation(
            value = "查询文件列表",
            notes = "该接口提供了用户查询某文件夹下面某些文件类型的文件列表的功能",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @GetMapping("files")
    public R<List<UserFileVO>> list(@NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
                                    @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileType) {
        Long realParentId = IdUtil.decrypt(parentId);
        List<Integer> fileTypesArray = null;
        if (!Objects.equals(FileConstants.ALL_FILE_TYPE, fileType)) {
            fileTypesArray = Splitter.on(TPanConstants.COMMON_SEPARATOR).splitToList(fileType).stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(realParentId);
        context.setFileTypesArray(fileTypesArray);
        context.setUserId(UserIdUtil.get());
        context.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> result = userFileService.getFileList(context);
        return R.data(result);
    }

    @ApiOperation(
            value = "创建文件夹",
            notes = "该接口提供了用户创建文件夹的功能",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    @PostMapping("file/folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderPO createFolderPO) {
        CreateFolderContext context = fileConverter.createFolderPO2CreateFolderContext(createFolderPO);
        Long fileId = userFileService.createFolder(context);
        return R.data(IdUtil.encrypt(fileId));
    }
}
