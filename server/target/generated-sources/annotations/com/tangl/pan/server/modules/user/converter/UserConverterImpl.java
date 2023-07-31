package com.tangl.pan.server.modules.user.converter;

import com.tangl.pan.server.modules.user.context.UserRegisterContext;
import com.tangl.pan.server.modules.user.entity.TPanUser;
import com.tangl.pan.server.modules.user.po.UserRegisterPO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-07-29T00:50:27+0800",
    comments = "version: 1.5.2.Final, compiler: javac, environment: Java 20.0.1 (Oracle Corporation)"
)
@Component
public class UserConverterImpl implements UserConverter {

    @Override
    public UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO) {
        if ( userRegisterPO == null ) {
            return null;
        }

        UserRegisterContext userRegisterContext = new UserRegisterContext();

        userRegisterContext.setUsername( userRegisterPO.getUsername() );
        userRegisterContext.setPassword( userRegisterPO.getPassword() );
        userRegisterContext.setQuestion( userRegisterPO.getQuestion() );
        userRegisterContext.setAnswer( userRegisterPO.getAnswer() );

        return userRegisterContext;
    }

    @Override
    public TPanUser userRegisterContext2TPanUser(UserRegisterContext userRegisterContext) {
        if ( userRegisterContext == null ) {
            return null;
        }

        TPanUser tPanUser = new TPanUser();

        tPanUser.setUsername( userRegisterContext.getUsername() );
        tPanUser.setQuestion( userRegisterContext.getQuestion() );
        tPanUser.setAnswer( userRegisterContext.getAnswer() );

        return tPanUser;
    }
}
