package com.tangl.pan.server.modules.file.converter;

import com.tangl.pan.server.modules.file.context.CreateFolderContext;
import com.tangl.pan.server.modules.file.po.CreateFolderPO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-08-10T23:03:33+0800",
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
}
