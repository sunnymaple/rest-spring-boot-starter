package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import cn.sunnymaple.rest.security.property.RsaProperties;
import cn.sunnymaple.rest.security.property.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * 接口安全加密自动配置类
 * @auther: wangzb
 * @date: 2019/6/16 10:34
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnProperty(name = "security.enabled",havingValue = "true")
@EnableConfigurationProperties(SecurityProperties.class)
@Import({SecurityFilter.class,SecurityController.class})
public class SecurityAutoConfiguration implements WebMvcConfigurer {

    @Autowired
    private SecurityProperties properties;

    /**
     * 异常转发的接口
     */
    private static String[] DEFAULT_EXCLUDE_PATH = {"/sign/**","/restException"};

    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;

    /**
     * 添加参数解析规则
     * @param resolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SecurityArgumentResolver());
    }

    /**
     * 添加拦截器，目的是删除当前线程内共享的数据ArgumentData
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor());
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init(){
        List<String> excludePathPatterns = properties.getExcludePathPatterns();
        excludePathPatterns.addAll(Arrays.asList(DEFAULT_EXCLUDE_PATH));
        excludePathPatterns.add(errorPath);
        RsaProperties rsa = properties.getRsa();
        if (rsa.getClientPublicKey().size() == 0){
            throw new SecurityException("客户端公钥不能为空！");
        }
        if (Utils.isEmpty(rsa.getServerPrivateKey())){
            throw new SecurityException("服务端密钥不能为空！");
        }
    }

}
