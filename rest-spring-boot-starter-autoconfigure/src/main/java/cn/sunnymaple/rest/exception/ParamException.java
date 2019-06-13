package cn.sunnymaple.rest.exception;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import lombok.Data;

/**
 * 参数异常
 * @author wangzb
 * @date 2019/6/12 18:13
 */
@Data
public class ParamException extends RestException {

    /**
     * 参数名称
     */
    private String paramName;
    /**
     * 参数值
     */
    private Object paramValue;

    /**
     * 固定异常枚举
     */
    private static final HttpStatusEnum PARAM_EXCEPTION = HttpStatusEnum.PARAM_EXCEPTION;

    public ParamException(String message){
        super(PARAM_EXCEPTION.getStatus(),message);
    }

    public ParamException(String message,Throwable e){
        super(PARAM_EXCEPTION.getStatus(),message,e);
    }

    public ParamException(String paramName,Object paramValue,String message){
        super(PARAM_EXCEPTION.getStatus(),message);
        this.paramName = paramName;
        this.paramValue = paramValue;
    }

    public ParamException(String paramName,Object paramValue,String message,Throwable e){
        super(PARAM_EXCEPTION.getStatus(),message,e);
        this.paramName = paramName;
        this.paramValue = paramValue;
    }
}
