package cn.sunnymaple.rest.response;

import java.lang.annotation.*;

/**
 * 此注解可以加在controller层的类和方法上
 * 1、加在类上表示整个controller层的接口都不使用ResponseHandler，即接口响应参数不封装成固定的结果集
 * 2、加在方法上，表示该接口不使用ResponseHandler
 * @author wangzb
 * @date 2019/6/5 12:00
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonResponseHandler {
}
