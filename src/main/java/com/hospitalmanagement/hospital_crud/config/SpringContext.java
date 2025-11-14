package com.hospitalmanagement.hospital_crud.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (ctx == null) {
            throw new IllegalStateException("Spring ApplicationContext not yet initialized.");
        }
        return ctx.getBean(clazz);
    }
}
