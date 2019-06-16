package cn.sunnymaple.rest.security.rsa;

/**
 * 签名算法
 * 签名算法有：
 * 算法             密钥长度                           默认长度        签名长度
 * MD2withRSA       512~65536之间的数，但是64的倍数     1024           与密钥相同
 * MD5withRSA       512~65536之间的数，但是64的倍数     1024           与密钥相同
 * SHA1withRSA      512~65536之间的数，但是64的倍数     1024           与密钥相同
 *
 * @auther: wangzb
 * @date: 2019/6/15 22:38
 */
public enum SignatureAlgorithmEnum {
    /**
     * MD2withRSA
     */
    MD2_WITH_RSA("MD2withRSA"),
    /**
     * MD5withRSA
     */
    MD5_WITH_RSA("MD5withRSA"),
    /**
     * SHA1withRSA
     */
    SHA1_WITH_RSA("SHA1withRSA");
    /**
     * 算法
     */
    private String algorithm;

    SignatureAlgorithmEnum(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getAlgorithm() {
        return algorithm;
    }
}
