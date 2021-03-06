package com.openIdeas.apps.apisflorea.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 访问Spring上下文的工具类
 * 
 * @author Evan Mn
 * 
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;

    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    /**
     * 根据Bean名称返回String类型的BEAN
     * 
     * @param beanName
     * @return
     */
    public static String getStringBean(String beanName) {
        return context.getBean(beanName, String.class);
    }

}
