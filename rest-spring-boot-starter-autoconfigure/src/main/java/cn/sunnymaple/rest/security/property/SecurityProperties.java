package cn.sunnymaple.rest.security.property;

import lombok.Data;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

/**
 * 参数加密配置类
 * @auther: wangzb
 * @date: 2019/6/16 09:17
 */
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties implements ApplicationContextAware {

    private static SecurityProperties securityProperties;

    /**
     * 是否开启参数加密，默认为false
     */
    private boolean enabled = false;

    /**
     * 不参与接口参数加密的接口uri
     * 在enabled = true成立的条件下，excludePathPatterns指定的接口请求参数和响应参数不经过解密或者加密操作
     * 支持 “？”、“*”以及“**”通配符
     */
    private List<String> excludePathPatterns = new ArrayList<>();

    /**
     * AES相关配置
     */
    private AesProperties aes = new AesProperties();

    /**
     * RSA相关配置
     */
    private RsaProperties rsa = new RsaProperties();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        securityProperties = applicationContext.getBean(SecurityProperties.class);
    }

    /**
     * 获取实例
     * @return
     */
    public static SecurityProperties getInstance(){
        return securityProperties;
    }
}
