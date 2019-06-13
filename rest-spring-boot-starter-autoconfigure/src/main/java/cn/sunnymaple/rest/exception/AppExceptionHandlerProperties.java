package cn.sunnymaple.rest.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AppExceptionHandler配置类
 * @author wangzb
 * @date 2019/6/13 14:46
 * @company 矽甲（上海）信息科技有限公司
 */
@ConfigurationProperties(prefix = "exception.handler")
@Data
public class AppExceptionHandlerProperties {
    /**
     * 是否启用AppExceptionHandler处理器
     * true 启用
     * false 停用
     */
    private boolean enabled;
}
