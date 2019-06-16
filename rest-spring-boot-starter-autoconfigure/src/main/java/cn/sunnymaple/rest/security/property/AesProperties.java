package cn.sunnymaple.rest.security.property;

import cn.sunnymaple.rest.security.aes.AlgorithmPatternEnum;
import cn.sunnymaple.rest.security.aes.PaddingTypeEnum;
import cn.sunnymaple.rest.security.aes.SecretKeyLengthEnum;
import lombok.Data;

/**
 * AES相关配置
 * @auther: wangzb
 * @date: 2019/6/16 09:27
 */
@Data
public class AesProperties {

    /**
     * 秘钥长度，默认为128
     */
    private Integer secretKeyLength = SecretKeyLengthEnum.SECRET_KEY_LENGTH_128.getLength();

    /**
     * 算法模式 默认为ECB
     */
    private String algorithmPattern = AlgorithmPatternEnum.ECB.getPattern();

    /**
     * 填充方式 默认为PPKCS5Padding
     */
    private String paddingType = PaddingTypeEnum.PKCS5_PADDING.getPaddingType();

    /**
     * 偏移量
     */
    private String vector;
}
