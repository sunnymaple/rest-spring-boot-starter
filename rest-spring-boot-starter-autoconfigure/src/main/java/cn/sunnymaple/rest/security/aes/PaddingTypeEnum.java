package cn.sunnymaple.rest.security.aes;

/**
 * AES - 填充方式
 * PKCS5Padding/PKCS7Padding
 * @author wangzb
 * @date 2019/6/14 18:05
 */
public enum PaddingTypeEnum {
    /**
     * PKCS5Padding
     */
    PKCS5_PADDING("PKCS5Padding"),
    /**
     * PKCS7Padding
     */
    PKCS7_PADDING("PKCS7Padding");
    /**
     * 填充方式
     */
    private String paddingType;

    PaddingTypeEnum(String paddingType) {
        this.paddingType = paddingType;
    }

    public String getPaddingType() {
        return paddingType;
    }
}
