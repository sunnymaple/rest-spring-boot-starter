package cn.sunnymaple.rest.response;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * AppResponseHandler配置文件
 * @author wangzb
 * @date 2019/6/5 13:50
 */
@ConfigurationProperties(prefix = "response.handler")
@Data
public class AppResponseHandlerProperties implements ApplicationContextAware {

    private static AppResponseHandlerProperties appResponseHandlerProperties;

    /**
     * 获取实例
     * @return
     */
    public static AppResponseHandlerProperties getInstance(){
        return appResponseHandlerProperties;
    }

    /**
     * 是否启用AppResponseHandler处理器
     * true 启用
     * false 停用
     */
    private boolean enabled;
    /**
     * 定义不需要使用AppResponseHandler的接口，同@NoResponseHandler
     * 支持通配符：？ 匹配单个字符，如/demo?  则/demo1、/demo2会被匹配  而/demo12则不被匹配
     *             *  除/外的任意字符  如：/demo* 则 /demo1、/demo2、/demo12会被匹配 而/demo/1不会被匹配
     *                                 如：/demo/* 可以匹配/demo/1 /demo/2  /demo/12
     *             ** 匹配任意的多个目录  如：/demo/** 可以匹配/demo/1 /demo/2  /demo/12 /demo/1/2等
     */
    private String[] nonResponseHandler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appResponseHandlerProperties = applicationContext.getBean(AppResponseHandlerProperties.class);
    }
}
