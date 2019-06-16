package cn.sunnymaple.rest.security.rsa;

/**
 * RSA密钥长度异常
 * @auther: wangzb
 * @date: 2019/6/15 23:41
 * @company 矽甲（上海）信息科技有限公司
 */
public class RsaSecretKeyLengthException extends RuntimeException{

    public RsaSecretKeyLengthException() {
        super("RSA的密钥长度必须在" + RsaUtils.MIN_LENGTH + "到" + RsaUtils.MAX_LENGTH + "字节之间");
    }
}
