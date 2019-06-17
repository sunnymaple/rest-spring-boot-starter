package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.NumberValidationUtils;
import cn.sunnymaple.rest.common.Utils;
import cn.sunnymaple.rest.exception.ParamException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Type;

/**
 * Controller层参数解析器
 * @auther: wangzb
 * @date: 2019/6/16 15:35
 */
@Slf4j
public class SecurityArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * 验证那些参数需要解析
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        ArgumentsThreadLocal argumentsHashTable = ArgumentsThreadLocal.getInstance();
        if (Utils.isEmpty(argumentsHashTable.get())){
            //如果请求参数为空，则不调用resolveArgument
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        ArgumentsThreadLocal argumentsHashTable = ArgumentsThreadLocal.getInstance();
        //获取参数名称
        String paramName = methodParameter.getParameterName();
        //参数类型
        Type parameterClass = methodParameter.getGenericParameterType();
        //获取参数值
        Object paramValue = argumentsHashTable.getParamByKey(paramName);
        if (parameterClass == String.class){
            //参数String
            return paramValue;
        }
        if (parameterClass == Integer.TYPE || parameterClass == Integer.class){
            //整数Integer
            return parseInteger(paramValue,paramName,parameterClass);
        }
        if (parameterClass == Double.class || parameterClass == Double.TYPE){
            return parseDouble(paramValue,paramName,parameterClass);
        }
        if (parameterClass == Float.class || parameterClass == Float.TYPE){
            return parseFloat(paramValue,paramName,parameterClass);
        }
        if (Utils.isEmpty(paramValue)){
            JSONObject params = argumentsHashTable.get().getParams();
            return Utils.parseObject(params.toJSONString(),(Class) parameterClass);
        }
        return Utils.parseObject(paramValue.toString(),(Class) parameterClass);
    }

    /**
     * 整数
     * @param value
     * @param paramName
     * @param parameterClass
     * @return
     */
    private Integer parseInteger(Object value,String paramName,Type parameterClass){
        if (!Utils.isEmpty(value)){
            if (NumberValidationUtils.isWholeNumber(value.toString())){
                return Integer.parseInt(value.toString());
            }else {
                throw new ParamException(paramName,value,"参数" + paramName + "应为整数");
            }
        }
        if (parameterClass == Integer.TYPE){
            log.warn("参数" + paramName + "值为空，建议使用封装类型，而不是int");
            return 0;
        }
        return null;
    }

    /**
     * 双精度浮点型
     * @param value
     * @param paramName
     * @param parameterClass
     * @return
     */
    private Double parseDouble(Object value,String paramName,Type parameterClass){
        if (!Utils.isEmpty(value)){
            if (NumberValidationUtils.isDecimal(value.toString())){
                return Double.parseDouble(value.toString());
            }else {
                throw new ParamException(paramName,value,"参数" + paramName + "应为浮点类型");
            }
        }
        if (parameterClass == Double.TYPE){
            log.warn("参数" + paramName + "值为空，建议使用封装类型，而不是double");
            return 0.0;
        }
        return null;
    }

    /**
     * 浮点型
     * @param value
     * @param paramName
     * @param parameterClass
     * @return
     */
    private Float parseFloat(Object value,String paramName,Type parameterClass){
        if (!Utils.isEmpty(value)){
            if (NumberValidationUtils.isDecimal(value.toString())){
                return Float.parseFloat(value.toString());
            }else {
                throw new ParamException(paramName,value,"参数" + paramName + "应为浮点类型");
            }
        }
        if (parameterClass == Double.TYPE){
            log.warn("参数" + paramName + "值为空，建议使用封装类型，而不是double");
            return 0.0f;
        }
        return null;
    }
}
