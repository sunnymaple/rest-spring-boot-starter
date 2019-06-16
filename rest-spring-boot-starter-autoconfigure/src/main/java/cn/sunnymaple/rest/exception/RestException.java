package cn.sunnymaple.rest.exception;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import lombok.Data;

/**
 * Rest接口异常
 * @author wangzb
 * @date 2019/6/12 18:08
 */
@Data
public class RestException extends RuntimeException {
    /**
     * 状态编码
     */
    private Integer status;
    /**
     * 异常信息
     */
    private String message;

    public RestException(Integer status,String message){
        super(message);
        this.message = message;
        this.status = status;
    }

    public RestException(Integer status,String message,Throwable e){
        super(message,e);
        this.message = message;
        this.status = status;
    }

    public RestException(String message) {
        super(message);
        this.message = message;
    }

    public RestException(String message,Throwable e) {
        super(message,e);
        this.message = message;
    }

    public RestException(HttpStatusEnum httpStatusEnum, Throwable e) {
        super(httpStatusEnum.getMessage(),e);
        this.status = httpStatusEnum.getStatus();
        this.message = httpStatusEnum.getMessage();
    }

    public RestException(HttpStatusEnum httpStatusEnum) {
        super(httpStatusEnum.getMessage());
        this.status = httpStatusEnum.getStatus();
        this.message = httpStatusEnum.getMessage();
    }

}
