package cn.sunnymaple.rest.security;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author wangzb
 * @date 2019/6/17 13:56
 * @company 矽甲（上海）信息科技有限公司
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {
        ArgumentsThreadLocal argumentsHashTable = ArgumentsThreadLocal.getInstance();
        //在此删除当前线程存储在ArgumentsHashTable中的参数
        argumentsHashTable.remove();
    }
}
