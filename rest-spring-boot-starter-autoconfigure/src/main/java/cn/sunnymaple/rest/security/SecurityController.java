package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import cn.sunnymaple.rest.exception.ParamException;
import cn.sunnymaple.rest.exception.RestException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 加解密异常后转发到该接口
 * @auther: wangzb
 * @date: 2019/6/16 15:22
 */
@RestController
public class SecurityController {
    /**
     * 签名认证异常
     */
    @RequestMapping(value = "/sign/paramVerify")
    public void paramVerify(){
        throw new SecurityVerifyException();
    }

    /**
     * 系统bug
     * @param message
     */
    @RequestMapping(value = "/sign/systemError")
    public void error(String message){
        throw new RestException(HttpStatusEnum.INTERNAL_SERVER_ERROR);
    }

    /**
     * 参数异常
     * @param message
     */
    @RequestMapping(value = "/sign/paramError")
    public void paramError(String message){
        throw new ParamException(message);
    }
}
