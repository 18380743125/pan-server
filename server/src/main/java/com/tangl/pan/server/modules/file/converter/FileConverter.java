package com.tangl.pan.server.modules.file.converter;

import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.context.DeleteFileContext;
import com.tangl.pan.server.modules.file.context.UpdateFilenameContext;
import com.tangl.pan.server.modules.file.entity.TPanUserFile;
import com.tangl.pan.server.modules.file.po.CreateFolderPO;
import com.tangl.pan.server.modules.file.po.DeleteFilePO;
import com.tangl.pan.server.modules.file.po.UpdateFilenamePO;
import com.tangl.pan.server.modules.user.context.*;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.po.*;
import com.tangl.pan.server.modules.user.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author tangl
 * @description 文件模块实体转换工具类
 * @create 2023-07-28 22:14
 */
@Mapper(componentModel = "spring")
public interface FileConverter {
    @Mapping(target = "parentId", expression = "java(com.tangl.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()))")
    @Mapping(target = "userId", expression = "java(com.tangl.pan.server.common.utils.UserIdUtil.get())")
    CreateFolderContext createFolderPO2CreateFolderContext(CreateFolderPO createFolderPO);

    UpdateFilenameContext updateFilenamePO2UpdateFilenameContext(UpdateFilenamePO updateFilenamePO);

    @Mapping(target = "userId", expression = "java(com.tangl.pan.server.common.utils.UserIdUtil.get())")
    DeleteFileContext deleteFilePO2DeleteFileContext(DeleteFilePO deleteFilePO);
}
