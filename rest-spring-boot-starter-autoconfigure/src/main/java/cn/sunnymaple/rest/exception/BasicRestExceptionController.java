package cn.sunnymaple.rest.exception;

import cn.sunnymaple.rest.response.AppResponseHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理接口
 * @author wangzb
 * @date 2019/6/13 10:07
 */
@Controller
@RequestMapping(value = "/restException")
public class BasicRestExceptionController {

    /**
     * SpringBoot定义的异常处理接口 {@link BasicErrorController}
     */
    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;

    /**
     * 对定义了ResponseBody的请求接口，返回异常信息（message）
     * 然后交给{@link AppResponseHandler}处理
     * @param request
     * @return
     */
    @RequestMapping(produces = "application/json")
    @ResponseBody
    public String error(HttpServletRequest request) {
        return request.getAttribute("message").toString();
    }

    @RequestMapping(produces = "text/html")
    public String error() {
        return "forward:" + errorPath;
    }

}
