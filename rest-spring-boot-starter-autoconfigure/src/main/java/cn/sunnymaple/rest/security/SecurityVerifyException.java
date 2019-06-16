package cn.sunnymaple.rest.security;

import cn.sunnymaple.rest.common.HttpStatusEnum;
import cn.sunnymaple.rest.exception.RestException;

/**
 * 参数签名验证异常
 * @auther: wangzb
 * @date: 2019/6/16 14:43
 */
public class SecurityVerifyException extends RestException{

    public SecurityVerifyException() {
        super(HttpStatusEnum.SECURITY_VERIFY.getStatus(),HttpStatusEnum.SECURITY_VERIFY.getMessage());
    }

}
