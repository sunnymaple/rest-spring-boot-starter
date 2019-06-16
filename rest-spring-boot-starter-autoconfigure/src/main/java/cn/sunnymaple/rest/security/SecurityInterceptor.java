package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.Utils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 * 由于执行顺序：过滤器 > 拦截器
 * 所以为了避免ArgumentsHashTable中的参数过多，无法释放内存，所以在拦截器的目的是删除ArgumentsHashTable中的参数
 * @auther: wangzb
 * @date: 2019/6/16 23:26
 */
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求体参数
        ArgumentsHashTable argumentsHashTable = ArgumentsHashTable.getInstance();
        ArgumentData argumentData = argumentsHashTable.get(Thread.currentThread());
        if (!Utils.isEmpty(argumentData)){
            HandlerMethod methodHandle = (HandlerMethod) handler;
            Integer size = methodHandle.getMethod().getParameters().length;
            argumentData.setUseSize(size);
        }
        return true;
    }
}
