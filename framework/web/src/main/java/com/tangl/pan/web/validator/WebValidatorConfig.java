package com.tangl.pan.web.validator;

import com.tangl.pan.core.constants.PanConstants;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.HibernateValidator;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 统一的参数校验器
 */
@SpringBootConfiguration
@Log4j2
public class WebValidatorConfig {

    private static final String FAIT_FAST_KEY = "hibernate.validator.fail_fast";

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(tPanValidator());
        log.info("The hibernate validator is loaded successfully！");
        return postProcessor;
    }

    /**
     * 构造项目的方法校验器
     *
     * @return Validator
     */
    private Validator tPanValidator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .addProperty(FAIT_FAST_KEY, PanConstants.TRUE_STR)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
