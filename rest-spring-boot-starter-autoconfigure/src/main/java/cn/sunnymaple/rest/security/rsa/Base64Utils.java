package cn.sunnymaple.rest.security.rsa;

import org.apache.commons.codec.binary.Base64;

/**
 * Base64解码编码码工具类
 * @author wangzb
 * @date 2018-11-30 17:33
 * @company 矽甲（上海）信息科技有限公司
 */
public class Base64Utils {

    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     * @throws Exception
     */
    public static byte[] decode(String base64) throws Exception {
        return Base64.decodeBase64(base64.getBytes());
    }

    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
            *
            * @param bytes
     * @return
             * @throws Exception
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(Base64.encodeBase64(bytes));
    }

}
