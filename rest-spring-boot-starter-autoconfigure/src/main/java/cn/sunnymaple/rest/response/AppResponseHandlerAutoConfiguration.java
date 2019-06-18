package cn.sunnymaple.rest.response;

import cn.sunnymaple.rest.common.Utils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

/**
 * AppResponseHandler自动配置类
 * @author wangzb
 * @date 2019/6/5 13:57
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties({AppResponseHandlerProperties.class})
@Import({AppResponseHandler.class,RestResultContainer.class})
public class AppResponseHandlerAutoConfiguration implements ApplicationContextAware {

//    @Autowired(required = false)
//    private RestResultContainerAware restResultContainerAware;

    private ApplicationContext applicationContext;

//    @PostConstruct
//    private void init(){
//        if (!Utils.isEmpty(restResultContainerAware)){
//            RestResultContainer restResultContainer = applicationContext.getBean(RestResultContainer.class);
//            restResultContainerAware.setRestResult(restResultContainer);
//        }
//    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
