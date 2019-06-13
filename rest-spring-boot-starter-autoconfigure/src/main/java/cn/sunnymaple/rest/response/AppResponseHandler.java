package cn.sunnymaple.rest.response;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import cn.sunnymaple.rest.common.Utils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 拦截有使用@ResponseBody的接口或者在控制层加了@RestController的所有接口
 * 目的是将接口的响应结果集封装成固定格式的参数
 * @author wangzb
 * @date 2019/6/5 11:42
 */
@ControllerAdvice
@Slf4j
public class AppResponseHandler implements ResponseBodyAdvice {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AppResponseHandlerProperties properties;

    /**
     * Response响应参数拦截规则，即可以在此方法中定义哪些接口将被AppResponseHandler拦截
     * 如果方法返回true则会调用beforeBodyWrite方法
     * @see ResponseBodyAdvice
     * @param methodParameter
     * @param aClass
     * @return true 将被拦截，即在接口响应后会调用beforeBodyWrite方法
     *         false 不被拦截，即不调用beforeBodyWrite方法
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        //判断类是否加上了NoResponseHandler注解，如果是则返回false
        NonResponseHandler classNoResponseHandler = methodParameter.getDeclaringClass().getAnnotation(NonResponseHandler.class);
        if (!Utils.isEmpty(classNoResponseHandler)){
            return false;
        }
        //判断方法（接口）是否加上了NoResponseHandler注解，如果是则返回false
        NonResponseHandler noResponseHandler = methodParameter.getMethodAnnotation(NonResponseHandler.class);
        if (!Utils.isEmpty(noResponseHandler)){
            return false;
        }
        //从配置文件中获取过滤的接口
        String[] nrhs = properties.getNonResponseHandler();
        if (!Utils.isEmpty(nrhs)){
            String path = request.getServletPath();
            for (String nrh : nrhs){
                AntPathMatcher matcher = new AntPathMatcher();
                boolean match = matcher.match(nrh, path);
                if (match){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Object result = null;
        try {
            //对异常进行处理
            HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
            Object isException = request.getAttribute("isException");
            if (!Utils.isEmpty(isException) && Objects.equals(isException.toString(),"1")){
                //接口异常
                String message = request.getAttribute("message").toString();
                Integer status = Integer.parseInt(request.getAttribute("status").toString());
                result = writeValueAsString(new RestResult(status,message));
            }else {
                //封装统一格式的响应参数
                result = restResult(o,mediaType,serverHttpRequest,serverHttpResponse,aClass);
            }
        } catch (Exception e) {
            HttpStatusEnum statusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR;
            result = new RestResult(statusEnum.getStatus(),statusEnum.getMessage());
            log.info("创建统一格式的响应数据异常！",e);
        }
        return result;
    }

    /**
     * 将对象转json字符串，并序列化
     * 支持实体类对象、Map集合、Collection集合
     * @param obj 目标对象
     * @return
     * @throws JsonProcessingException
     */
    private static String writeValueAsString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }

    /**
     * 判断返回结果是否为application/json形式
     * @param mediaType
     * @return
     */
    private boolean isJsonResponse(MediaType mediaType){
        return Objects.equals(mediaType.toString(),"application/json");
    }

    /**
     * 将响应对象o封装为RestResult对象
     * @param o
     * @param mediaType
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param aClass
     * @return
     * @throws IOException
     */
    private Object restResult(Object o,MediaType mediaType,ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse,Class aClass) throws IOException {
        /*
         * 1:判断是否为null
         */
        if (o == null){
            RestResult restResult = RestResult.factory();
            if (isJsonResponse(mediaType) && StringHttpMessageConverter.class == aClass){
                return JSONObject.toJSONString(restResult, SerializerFeature.WriteMapNullValue);
            }
            return restResult;
        }
        /*
         * 2、对于响应的是Resource资源对象（如：文件下载），则直接返回Object对象
         * 对于响应对象本身就是RestResult对象，则不再次封装，直接返回
         */
        if (o instanceof Resource || o instanceof RestResult){
            return o;
        }
        /*
         * 3、判断是否返回的是Map
         *   如果是：判断请求状态码，如果状态码为非200,则说明请求失败，则获取错误信息RestResult
         */
        int successCode = HttpStatusEnum.OK.getStatus();
        if (o instanceof Map){
            HttpServletResponse response =  ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
            Integer status = response.getStatus();
            if (!Objects.equals(status, successCode)){
                HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
                RestResult restResult = (RestResult) request.getAttribute("result");
                if (!Utils.isEmpty(restResult)){
                    return restResult;
                }else {
                    Map<String,Object> error = (Map<String, Object>) o;
                    return Utils.map2Object(error, RestResult.class);
                }
            }else {
                return RestResult.factory(o);
            }
        }
        if (o instanceof String){
            /* 按道理 只要API 返回值是 String / byte[]等
             *  不会由MappingJackson2HttpMessageConverter处理的返回值
             * 都有可能出错，抛出ClassCastException...
             * 目前API 出现的比较多的是String，所以只处理String情况
             * 如果 API返回的是 String，
             */
            try {
                /*
                 *  String返回值 将被StringHttpMessageConverter处理，
                 *  所以此时应该返回RestResult的json序列化后的字符串
                 *  如果此时还是返回RestResult对象，会抛出ClassCastException
                 *  因为StringHttpMessageConverter会把RestResult对象当做String处理
                 */
                return writeValueAsString(RestResult.factory(o));
            }catch (JsonProcessingException e) {
                log.warn("json serialize error", e);
                return o;
            }
        }
        return RestResult.factory(o);
    }
}
