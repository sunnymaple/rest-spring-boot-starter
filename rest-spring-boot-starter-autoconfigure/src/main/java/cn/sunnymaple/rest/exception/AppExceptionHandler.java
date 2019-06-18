package cn.sunnymaple.rest.exception;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import cn.sunnymaple.rest.common.Utils;
import cn.sunnymaple.rest.response.AppResponseHandlerProperties;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 异常处理
 * @author wangzb
 * @date 2019/6/10 10:05
 */
@ControllerAdvice
@Slf4j
public class AppExceptionHandler {

    /**
     * 定义异常处理接口
     */
    private static final String FORWARD_EXCEPTION_URI = "/restException";

    @Autowired
    private HttpServletRequest request;

    /**
     * SpringBoot定义的异常处理接口 {@link BasicErrorController}
     */
    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;


    /**
     * 打印日志并设置异常信息
     * @param e 异常
     * @param message 异常message
     * @param status 错误状态码
     */
    private void printLogAndSetAttribute(Throwable e, String message,Integer status){
        //打印日志
        log.error(e.getClass().getSimpleName() + "-->" + message,e);
        //修改http响应状态码为异常状态码status
        request.setAttribute("javax.servlet.error.status_code",status);
        //设置异常信息
        if (Utils.isEmpty(status)){
            status = HttpStatusEnum.INTERNAL_SERVER_ERROR.getStatus();
        }
        request.setAttribute("status",status);
        request.setAttribute("message",message);
        request.setAttribute("isException","1");
    }

    /**
     * 参数绑定异常
     * @param e {@see BindException}
     * @return
     */
    @ExceptionHandler(BindException.class)
    public String exceptionHandle(BindException e,HandlerMethod handlerMethod,HttpServletRequest request){
            String message = "";
            ParamException paramException = null;
            List<ObjectError> allErrors = e.getAllErrors();
            if (!Utils.isEmpty(allErrors)){
                ObjectError error = allErrors.get(0);
                if (error instanceof FieldError){
                    FieldError fieldError = (FieldError) error;
                    //获取参数异常信息
                    message = fieldError.getDefaultMessage();
                    //定义异常为ParamException参数异常
                    paramException = new ParamException(fieldError.getField(),fieldError.getRejectedValue(),message,e);
                    message = paramException.getMessage();
                }
            }
            HttpStatusEnum httpStatusEnum =  HttpStatusEnum.PARAM_EXCEPTION;
            Integer code = httpStatusEnum.getStatus();
            //打印日志并设置异常信息
            printLogAndSetAttribute(paramException,message,code);
            //转发到异常处理接口
        return forward(handlerMethod);
    }

    /**
     * 抛出参数验证异常
     * @param e {@see ConstraintViolationException}
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public String exceptionHandle(ConstraintViolationException e, HandlerMethod handlerMethod){
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        String message = "";
        ParamException paramException = null;
        if (!Utils.isEmpty(constraintViolations)){
            Iterator<ConstraintViolation<?>> it = constraintViolations.iterator();
            ConstraintViolation<?> constraintViolation = it.next();
            PathImpl propertyPath = (PathImpl) constraintViolation.getPropertyPath();
            //获取异常信息
            message = constraintViolation.getMessage();
            //定义异常
            paramException = new ParamException(propertyPath.getLeafNode().toString(),propertyPath.getLeafNode().getValue(),message,e);
        }
        HttpStatusEnum httpStatusEnum =  HttpStatusEnum.PARAM_EXCEPTION;
        Integer code = httpStatusEnum.getStatus();
        //打印日志并设置异常信息
        printLogAndSetAttribute(paramException,message,code);
        //转发到异常处理接口
        return forward(handlerMethod);
    }

    /**
     * 不支持的请求方式异常处理
     * 如接口指定了只有Get请求，而实际以Post的方式请求，就会抛出该异常
     * 注意：该接口不能传入{@link HandlerMethod}对象，否则捕捉不到该异常
     * @param e HttpRequestMethodNotSupportedException
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String exceptionHandle(HttpRequestMethodNotSupportedException e){
        HttpStatusEnum definitionEnum = HttpStatusEnum.METHOD_NOT_ALLOWED;
        Integer code = definitionEnum.getStatus();
        String message = "不支持" + e.getMethod() + "请求！";
        printLogAndSetAttribute(e,message,code);
        //转发到异常处理接口
        return "forward:" + FORWARD_EXCEPTION_URI;
    }

    /**
     * 异常RestException
     * @param e
     * @param handlerMethod
     * @return
     */
    @ExceptionHandler(RestException.class)
    public String exceptionHandle(RestException e,HandlerMethod handlerMethod){
        printLogAndSetAttribute(e,e.getMessage(),e.getStatus());
        //转发到异常处理接口
        return forward(handlerMethod);
    }

    /**
     * 其他异常
     * @param e
     * @param handlerMethod
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHandle(Exception e, HandlerMethod handlerMethod){
        Throwable cause = e.getCause();
        if (cause instanceof RestException){
            exceptionHandle((RestException) cause,handlerMethod);
        }
        HttpStatusEnum httpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR;
        printLogAndSetAttribute(e,httpStatusEnum.getMessage(),httpStatusEnum.getStatus());
        //转发到异常处理接口
        return forward(handlerMethod);
    }


    /**
     * 转发
     * @param handlerMethod
     * @return
     */
    private String forward(HandlerMethod handlerMethod){
        return isResponseBody(handlerMethod) ? ("forward:" + FORWARD_EXCEPTION_URI) : ("forward:" + errorPath);
    }

    /**
     * 判断是否是请求接口是否是ResponseBody
     * @param handlerMethod
     * @return
     */
    private boolean isResponseBody(HandlerMethod handlerMethod){
        //判断是否使用了responseHandler
        AppResponseHandlerProperties properties = AppResponseHandlerProperties.getInstance();
        if (Utils.isEmpty(properties) || !properties.isEnabled()){
            return false;
        }
        //接口方法
        Method method = handlerMethod.getMethod();
        //接口类
        Class<?> aClass = handlerMethod.getBean().getClass();
        //先判断Controller类对象是否有RestController/ResponseBody注解
        RestController restController = aClass.getAnnotation(RestController.class);
        ResponseBody responseBody = aClass.getAnnotation(ResponseBody.class);
        if (!Utils.isEmpty(restController) || !Utils.isEmpty(responseBody)){
            //获取返回值类型
            Class<?> returnType = method.getReturnType();
            if (returnType == ModelAndView.class){
                return false;
            }
        }
        //判断方法（接口）是否有ResponseBody注解
        ResponseBody rBody = handlerMethod.getMethodAnnotation(ResponseBody.class);
        if (Utils.isEmpty(rBody)){
            return false;
        }
        return true;
    }
}
