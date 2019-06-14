package cn.sunnymaple.rest.security.aes;

/**
 * 密钥长度枚举
 * AES标准规范中，分组长度只能是128位
 * 密钥的长度可以使用128位、192位或256位
 * 密钥长度越长越安全
 * @author wangzb
 * @date 2019/6/14 17:44
 */
public enum  SecretKeyLengthEnum {
    /**
     * 128位
     */
    SECRET_KEY_LENGTH_128(128),
    /**
     * 192位
     */
    SECRET_KEY_LENGTH_192(192),
    /**
     * 256位
     */
    SECRET_KEY_LENGTH_256(256);
    /**
     * 密钥长度
     */
    private Integer length;

    SecretKeyLengthEnum(Integer length) {
        this.length = length;
    }

    public Integer getLength() {
        return length;
    }
}
