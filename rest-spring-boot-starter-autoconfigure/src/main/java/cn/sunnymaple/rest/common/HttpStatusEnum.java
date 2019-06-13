package cn.sunnymaple.rest.common;

import org.springframework.http.HttpStatus;

/**
 * 定义Http响应状态码
 * @author wangzb
 * @date 2019/6/5 11:49
 */
public enum HttpStatusEnum {
    /**
     * Http请求状态码 - 请求成功 200
     */
    OK(HttpStatus.OK.value(),"请求成功！"),
    /**
     * 404
     */
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "资源未找到！"),
    /**
     * Http请求状态码 - 请求失败 500
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),"系统繁忙，请稍后重试！"),
    /**
     * 参数提交异常，异常信息由使用者定义
     */
    PARAM_EXCEPTION(1000),
    /**
     * 用户名密码错误提示信息
     */
    USER_PASSWORD_ERROR(1001, "用户名或密码错误!"),

    LOGIN_EXCEPTION(1002,"未登录，或者没有权限！"),
    /**
     * 不支持的请求方式，如指定了post请求，却使用get请求之后抛出的异常
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(),"不支持的请求方式！");

    HttpStatusEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    HttpStatusEnum(int status) {
        this.status = status;
    }

    /**
     * 状态码
     */
    private int status;
    /**
     * 状态码对应的信息
     */
    private String message;

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
