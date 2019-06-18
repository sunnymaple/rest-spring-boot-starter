package cn.sunnymaple.rest.exception;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 统一的异常处理 - 自动配置类
 * @author wangzb
 * @date 2019/6/10 10:25
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "exception.handler.enabled",havingValue = "true",matchIfMissing = true)
@EnableConfigurationProperties(AppExceptionHandlerProperties.class)
@Import({BasicRestExceptionController.class})
public class AppExceptionHandlerAutoConfiguration {

    /**
     * 将异常处理类添加到spring容器中
     * @return
     */
    @ConditionalOnMissingBean
    @Bean
    public AppExceptionHandler appExceptionHandler(){
        return new AppExceptionHandler();
    }
}
