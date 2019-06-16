package cn.sunnymaple.rest.security.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 参数加密配置类
 * @auther: wangzb
 * @date: 2019/6/16 09:17
 */
@ConfigurationProperties(prefix = "security")
@Data
public class SecurityProperties {
    /**
     * 是否开启参数加密，默认为false
     */
    private boolean enabled = false;

    /**
     * 不参与接口参数加密的接口uri
     * 在enabled = true成立的条件下，excludePathPatterns指定的接口请求参数和响应参数不经过解密或者加密操作
     * 支持 “？”、“*”以及“**”通配符
     */
    private String[] excludePathPatterns;

    /**
     * AES相关配置
     */
    private AesProperties aes = new AesProperties();

    /**
     * RSA相关配置
     */
    private RsaProperties rsa = new RsaProperties();
}
