package cn.sunnymaple.rest.response;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * AppResponseHandler自动配置类
 * @author wangzb
 * @date 2019/6/5 13:57
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({AppResponseHandlerProperties.class})
@Import({AppResponseHandler.class})
public class AppResponseHandlerAutoConfiguration {
}
