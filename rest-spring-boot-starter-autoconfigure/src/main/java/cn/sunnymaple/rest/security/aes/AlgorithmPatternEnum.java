package cn.sunnymaple.rest.security.aes;

/**
 * AES - 算法模式
 * 算法模式有：
 * 1、电子密码本 Electronic Code Book  简称ECB
 * 2、加密块链 Cipher Block Chaining   简称CBC
 * 3、加密反馈 Cipher Feed Back        简称CFB
 * 4、输出反馈 Output Feed Back        简称OFB
 * @author wangzb
 * @date 2019/6/14 17:57
 */
public enum AlgorithmPatternEnum {
    /**
     * 电子密码本 Electronic Code Book  简称ECB
     */
    ECB("ECB"),
    /**
     * 加密块链 Cipher Block Chaining   简称CBC
     */
    CBC("CBC"),
    /**
     * 加密反馈 Cipher Feed Back        简称CFB
     */
    CFB("CFB"),
    /**
     * 输出反馈 Output Feed Back        简称OFB
     */
    OFB("OFB");

    /**
     * 模式
     */
    private String pattern;

    AlgorithmPatternEnum(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
