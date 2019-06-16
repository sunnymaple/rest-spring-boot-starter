package cn.sunnymaple.rest.security.property;

import cn.sunnymaple.rest.security.rsa.SignatureAlgorithmEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * RSA相关配置
 * @auther: wangzb
 * @date: 2019/6/16 09:34
 */
@Data
public class RsaProperties {
    /**
     * 是否开启RSA数字签名认证，开启需要传递参数sign
     * 默认是开启签名验证
     */
    private boolean signatureEnable = true;

    /**
     * 当signatureEnable为true时，可以指定签名算法
     * 默认为MD5withRSA
     */
    private String signatureAlgorithm = SignatureAlgorithmEnum.MD5_WITH_RSA.getAlgorithm();

    /**
     * 客户端公钥
     * 为了满足多个客户端（安卓端、iOS端、小程序端或者其他第三方api使用者）之间的密钥对的不同，采用Map的键值对形式存储客户端的公钥
     * key：客户端名称、id或者key的任意字符，客户端在请求接口必须以参数appKey的参数名传递该值
     * value:key对应的客户端公钥
     */
    private Map<String,String> clientPublicKey = new HashMap<>();

    /**
     * 服务端公钥
     */
    private String serverPublicKey;

    /**
     * 服务端私钥
     */
    private String serverPrivateKey;

}
