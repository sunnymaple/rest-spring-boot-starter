package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Request包装类
 * 该类的目的是对请求参数解密并且复制解密参数到流中
 * @auther: wangzb
 * @date: 2019/6/16 11:19
 */
@Slf4j
public class SecurityRequestWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public SecurityRequestWrapper(HttpServletRequest request,HttpServletResponse response,String uri) {
        super(request);
        forward(request,response,uri);
    }

    /**
     * 转发
     * @param request
     * @param response
     * @param uri
     */
    private void forward(HttpServletRequest request, HttpServletResponse response, String uri){
        try {
            request.getRequestDispatcher(uri).forward(request,response);
        } catch (Exception e1) {}
    }

    public SecurityRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ArgumentsThreadLocal argumentsHashTable = ArgumentsThreadLocal.getInstance();
        ArgumentData argumentData = argumentsHashTable.get();
        String data = "";
        if (!Utils.isEmpty(argumentData)){
            data = argumentData.getParams().toJSONString();
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}
