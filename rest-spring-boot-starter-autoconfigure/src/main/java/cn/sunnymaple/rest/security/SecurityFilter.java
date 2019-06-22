package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import cn.sunnymaple.rest.exception.ParamException;
import cn.sunnymaple.rest.security.aes.AesUtils;
import cn.sunnymaple.rest.security.property.RsaProperties;
import cn.sunnymaple.rest.security.property.SecurityProperties;
import cn.sunnymaple.rest.security.rsa.RsaUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 安全加密机制的过滤器
 * 按理说解密操作在拦截器中做比较合适，但是考虑接口参数可能会使用@RequestBody，直接从流中获取参数
 * 由于@RequestBody指定的参数是通过流中获取的，它有个特点，就是只能拿一次，为避免接口加了@RequestBody注解的参数获取不到参数
 * 该过滤器目的是将解密后的参数复制一份到流中
 * @auther: wangzb
 * @date: 2019/6/16 10:56
 */
@WebFilter(urlPatterns = "/**", filterName = "SecurityFilter")
@Slf4j
public class SecurityFilter implements Filter {

    /**
     * 解密或者签名异常跳转的接口
     */
    public static final String PARAMS_DISPATCHER_PATH = "/sign/paramError";
    /**
     * 参数签名认证异常
     */
    public static final String PARAM_VERIFY_DISPATCHER_PATH = "/sign/paramVerify";
    /**
     * 系统bug
     */
    public static final String SYSTEM_DISPATCHER_PATH = "/sign/systemError";

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //判断加密接口过滤规则
        List<String> excludePathPatterns = securityProperties.getExcludePathPatterns();
        if (!Utils.uriMatching(request.getServletPath(),excludePathPatterns)){
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            //解密/签名认证
            try {
                decode(request,response);
                // 这里将原始request传入，将解密后的参数复制一份到流中
                ServletRequest requestWrapper = new SecurityRequestWrapper(request);
                chain.doFilter(requestWrapper, servletResponse);
            }catch (ParamException e){
//                ServletRequest requestWrapper = new SecurityRequestWrapper(request,response,PARAMS_DISPATCHER_PATH);
//                chain.doFilter(requestWrapper, servletResponse);
                throw e;
            }catch (Exception e){
                ServletRequest requestWrapper = new SecurityRequestWrapper(request,response,SYSTEM_DISPATCHER_PATH);
                chain.doFilter(requestWrapper, servletResponse);
            }
        }else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    /**
     * 参数解密、签名认证
     * @param request
     * @param response
     */
    private void decode(HttpServletRequest request,HttpServletResponse response) throws Exception {
//        try {
            //获取请求参数
            BaseRequestBody requestBody = getRequestBody(request);
            RsaProperties rsa = securityProperties.getRsa();
            //通过key获取客户端公钥
            Map<String, String> clientPublicKeys = rsa.getClientPublicKey();
            String appKey = requestBody.getAppKey();
            String clientPublicKey = clientPublicKeys.get(appKey);
            if (Utils.isEmpty(clientPublicKey)){
//                forward(request,response,PARAMS_DISPATCHER_PATH);
//                return;
                throw new ParamException("不合法的appKey！");
            }
            if (rsa.isSignatureEnable()){
                //通过客户端公钥认证
                boolean verify = RsaUtils.verify(requestBody.getSignParam(), clientPublicKey, requestBody.getSign());
                if (!verify){
                    forward(request,response,PARAM_VERIFY_DISPATCHER_PATH);
                    return;
                }
            }
            String data = requestBody.getData();
            if (!Utils.isEmpty(data)){
                //使用服务端RSA私钥对AES密钥进行解密
                String key = RsaUtils.privateKeyDecrypt(requestBody.getKey(),rsa.getServerPrivateKey());
                //使用AES密钥对请求参数进行解密
                String plaintext = AesUtils.aesDecode(key, data, securityProperties.getAes());
                JSONObject plaintextJsonObject = JSONObject.parseObject(plaintext);
                //存储参数
                ArgumentsThreadLocal arguments = ArgumentsThreadLocal.getInstance();
                ArgumentData argumentData = new ArgumentData(appKey,plaintextJsonObject);
                arguments.set(argumentData);
            }
//        } /*catch (ParamException e){
//            log.error(e.getMessage(),e);
//            forward(request,response,PARAMS_DISPATCHER_PATH);
//        }*/ catch (Exception e) {
//            log.error(e.getMessage(),e);
//            forward(request,response,SYSTEM_DISPATCHER_PATH);
//        }
    }

    /**
     * 转发
     * @param request
     * @param response
     * @param uri
     */
    private void forward(HttpServletRequest request,HttpServletResponse response,String uri){
        try {
            request.getRequestDispatcher(uri).forward(request,response);
        } catch (Exception e1) {}
    }

    /**
     * 获取请求参数
     * @param request
     * @return
     * @throws IOException
     */
    private BaseRequestBody getRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = null;
        BufferedReader reader = null;
        try {
            inputStream = cloneInputStream(request.getInputStream());
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            if (Utils.isEmpty(requestBody)){
                throw new ParamException("加密参数不能为空！");
            }
            BaseRequestBody baseRequestBody = Utils.parseObject(requestBody,BaseRequestBody.class);
            if (Utils.isEmpty(baseRequestBody.getKey())){
                throw new ParamException("AES的密钥不能为空！");
            }
            if (Utils.isEmpty(baseRequestBody.getAppKey())){
                throw new ParamException("appKey不能为空！");
            }
            return baseRequestBody;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 复制请求流
     * @param inputStream
     * @return
     */
    public InputStream cloneInputStream(ServletInputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) > -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        byteArrayOutputStream.flush();
        InputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        return byteArrayInputStream;
    }

}
