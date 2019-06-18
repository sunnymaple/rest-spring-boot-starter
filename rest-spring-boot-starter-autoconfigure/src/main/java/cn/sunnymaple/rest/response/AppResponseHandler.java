package cn.sunnymaple.rest.response;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import cn.sunnymaple.rest.common.Utils;
import cn.sunnymaple.rest.exception.RestException;
import cn.sunnymaple.rest.security.ArgumentData;
import cn.sunnymaple.rest.security.ArgumentsThreadLocal;
import cn.sunnymaple.rest.security.BaseResponseBody;
import cn.sunnymaple.rest.security.aes.AesUtils;
import cn.sunnymaple.rest.security.property.AesProperties;
import cn.sunnymaple.rest.security.property.RsaProperties;
import cn.sunnymaple.rest.security.property.SecurityProperties;
import cn.sunnymaple.rest.security.rsa.RsaUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
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
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * 拦截有使用@ResponseBody的接口或者在控制层加了@RestController的所有接口
 * 目的是将接口的响应结果集封装成固定格式的参数
 * @author wangzb
 * @date 2019/6/5 11:42
 */
@ControllerAdvice
@Slf4j
public class AppResponseHandler implements ResponseBodyAdvice , ApplicationContextAware {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AppResponseHandlerProperties properties;

    private IRestResultFactory restResultFactory;

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }
    /**
     * Response响应参数拦截规则，即可以在此方法中定义哪些接口将被AppResponseHandler拦截
     * 如果方法返回true则会调用beforeBodyWrite方法
     * @see ResponseBodyAdvice
     * @param methodParameter
     * @return true 将被拦截，即在接口响应后会调用beforeBodyWrite方法
     *         false 不被拦截，即不调用beforeBodyWrite方法
     */
    private boolean supports(MethodParameter methodParameter){
        if (!properties.isEnabled()){
            return false;
        }
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

    /**
     * 响应参数是否加密
     * @param securityProperties
     * @return
     */
    private boolean resultIsSecurity(SecurityProperties securityProperties){
        return !Utils.isEmpty(securityProperties) && securityProperties.isEnabled()
                && !Utils.uriMatching(request.getServletPath(),securityProperties.getExcludePathPatterns());
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Object result = null;
        Type returnType = o.getClass();
        BeforeBodyWriteParameter beforeBodyWriteParameter =
                new BeforeBodyWriteParameter(methodParameter,mediaType,aClass,serverHttpRequest,serverHttpResponse);
        try {
            if (o instanceof Resource){
                //如果o为资源文件，则直接返回
                return o;
            }
            //判断是否加密
            SecurityProperties securityProperties = SecurityProperties.getInstance();
            if (resultIsSecurity(securityProperties)){
                //对结果集进行加密，并获取AES密钥
                ArgumentsThreadLocal argumentsHashTable = ArgumentsThreadLocal.getInstance();
                ArgumentData argumentData = argumentsHashTable.get();
                if (!Utils.isEmpty(argumentData)){
                    String appKey = argumentData.getAppKey();
                    //加密
                    o = encrypt(o,securityProperties,appKey);
                }
            }
            if (supports(methodParameter)) {
                //对异常进行处理
                HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
                Object isException = request.getAttribute("isException");
                if (!Utils.isEmpty(isException) && Objects.equals(isException.toString(), "1")) {
                    //接口异常
                    String message = request.getAttribute("message").toString();
                    Integer status = Integer.valueOf(request.getAttribute("status").toString());
                    result = restResultFactory.failure(message,status,beforeBodyWriteParameter);
                } else {
                    //封装统一格式的响应参数
                    result = restResultFactory.success(o,beforeBodyWriteParameter);
                }
            }else {
                //没有启用Response或者使用了@NonResponseHandler注解，直接返回结果对象
                result = o;
            }
        } catch (Exception e) {
            HttpStatusEnum statusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR;
            result = restResultFactory.failure(statusEnum.getMessage(),statusEnum.getStatus(),beforeBodyWriteParameter);
            log.info("创建统一格式的响应数据异常！",e);
        }
        return writeValueAsString(result,returnType);
    }

    /**
     * 对响应结果对象加密
     * @param o
     * @return
     */
    private BaseResponseBody encrypt(Object o,SecurityProperties securityProperties,String appKey) {
        try {
            AesProperties aes = securityProperties.getAes();
            //获取AES密钥
            String secretKey = AesUtils.getSecretKey(aes.getSecretKeyLength());
            //使用AES密钥对响应结果加密
            String data = AesUtils.aesEncode(secretKey, o.toString(), aes);
            //使用客户端公钥对AES密钥进行加密
            RsaProperties rsa = securityProperties.getRsa();
            String clientPublicKey = rsa.getClientPublicKey().get(appKey);
            String cipherText = RsaUtils.publicKeyEncrypt(secretKey, clientPublicKey);
            return new BaseResponseBody(cipherText,data);
        }catch (Exception e){
            throw new RestException(e.getMessage(),e);
        }
    }

    /**
     * 将对象转json字符串，并序列化
     * 支持实体类对象、Map集合、Collection集合
     * @param result 目标对象
     * @return
     * @throws JsonProcessingException
     */
    private static Object writeValueAsString(Object result, Type returnType) {
        if (returnType == String.class){
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(result);
            } catch (JsonProcessingException e) { }
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        IRestResultFactory restResultFactory = applicationContext.getBean(IRestResultFactory.class);
        Optional<IRestResultFactory> op = Optional.ofNullable(restResultFactory);
        this.restResultFactory = op.orElse(new DefaultRestResultFactory());
    }
}
