package com.tangl.pan.server.modules.file.converter;

import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.context.DeleteFileContext;
import com.tangl.pan.server.modules.file.context.UpdateFilenameContext;
import com.tangl.pan.server.modules.file.po.CreateFolderPO;
import com.tangl.pan.server.modules.file.po.DeleteFilePO;
import com.tangl.pan.server.modules.file.po.UpdateFilenamePO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-13T17:43:45+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class FileConverterImpl implements FileConverter {

    @Override
    public CreateFolderContext createFolderPO2CreateFolderContext(CreateFolderPO createFolderPO) {
        if ( createFolderPO == null ) {
            return null;
        }

        CreateFolderContext createFolderContext = new CreateFolderContext();

        createFolderContext.setFolderName( createFolderPO.getFolderName() );

        createFolderContext.setParentId( com.tangl.pan.core.utils.IdUtil.decrypt(createFolderPO.getParentId()) );
        createFolderContext.setUserId( com.tangl.pan.server.common.utils.UserIdUtil.get() );

        return createFolderContext;
    }

    @Override
    public UpdateFilenameContext updateFilenamePO2UpdateFilenameContext(UpdateFilenamePO updateFilenamePO) {
        if ( updateFilenamePO == null ) {
            return null;
        }

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();

        updateFilenameContext.setFileId( updateFilenamePO.getFileId() );
        updateFilenameContext.setNewFilename( updateFilenamePO.getNewFilename() );

        return updateFilenameContext;
    }

    @Override
    public DeleteFileContext deleteFilePO2DeleteFileContext(DeleteFilePO deleteFilePO) {
        if ( deleteFilePO == null ) {
            return null;
        }

        DeleteFileContext deleteFileContext = new DeleteFileContext();

        deleteFileContext.setUserId( com.tangl.pan.server.common.utils.UserIdUtil.get() );

        return deleteFileContext;
    }
}
