package cn.sunnymaple.rest.security.aes;

import cn.sunnymaple.rest.security.property.AesProperties;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * Aes工具类
 * @author wangzb
 * @date 2019/6/14 18:17
 */
public class AesUtils {
    /**
     * AES
     */
    public static final String AES  =  "AES";

    /**
     * 获取一个指定长度的随机数密钥
     * @param secretKeyLength 密钥长度
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSecretKey(Integer secretKeyLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        //获取当前时间戳
        byte[] timestamp = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
        //根据时间戳获取随机密钥
        SecureRandom secureRandom = new SecureRandom(timestamp);
        keyGen.init(secretKeyLength, secureRandom);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 获取一个指定长度的随机数密钥
     * @param secretKeyLength SecretKeyLengthEnum枚举对象
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSecretKey(SecretKeyLengthEnum secretKeyLength) throws NoSuchAlgorithmException {
        return getSecretKey(secretKeyLength.getLength());
    }

    /**
     * 获取一个长度为256位的随机密钥
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getSecretKey() throws NoSuchAlgorithmException {
        return getSecretKey(SecretKeyLengthEnum.SECRET_KEY_LENGTH_256.getLength());
    }

    /**
     * 获取密码器
     * @param type 加密/解密操作
     * @param secretKey 密钥
     * @param algorithmPattern 算法模式
     * @param paddingType 填充模式
     * @param vector 向量
     * @return
     * @throws Exception
     */
    private static Cipher createCipher(Integer type,String secretKey, String algorithmPattern, String paddingType, String vector) throws Exception{
        //1.构造密钥生成器，指定为AES算法,不区分大小写
        KeyGenerator keygen = KeyGenerator.getInstance(AES);
        //2.根据解密或者加密规则初始化密钥生成器
        byte[] secretKeyBites = Base64.getDecoder().decode(secretKey);
        Integer secretKeyLength = secretKeyBites.length * 8;
        keygen.init(secretKeyLength);
        //3.根据字节数组生成AES密钥
        SecretKey key = new SecretKeySpec(secretKeyBites, AES);
        //4.根据指定算法AES自成密码器
        Cipher cipher = Cipher.getInstance(AES + "/" + algorithmPattern + "/" + paddingType);
        //5.初始化密码器，第一个参数为加密(Encrypt_mode)或者解密解密(Decrypt_mode)操作，第二个参数为使用的KEY
        if (!Objects.equals(AlgorithmPatternEnum.ECB.getPattern(),algorithmPattern)) {
            IvParameterSpec iv = new IvParameterSpec(vector.getBytes());
            cipher.init(type, key,iv);
        }else {
            cipher.init(type, key);
        }
        return cipher;
    }

    /**
     * AES加密
     * @param secretKey 密钥
     * @param plaintext 明文
     * @param algorithmPattern 算法模式
     * @param paddingType 填充模式
     * @param vector 向量
     * @return
     * @throws Exception
     */
    public static String aesEncode(String secretKey,String plaintext, String algorithmPattern,String paddingType,String vector) throws Exception{
        //获取密码器
        Cipher cipher = createCipher(Cipher.ENCRYPT_MODE,secretKey,algorithmPattern,paddingType,vector);
        //获取明文的字节数组(这里要设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
        byte [] byteEncode = plaintext.getBytes(StandardCharsets.UTF_8);
        //根据密码器的初始化方式--加密：将数据加密
        byte [] byteAes = cipher.doFinal(byteEncode);
        //将加密后的数据转换为字符串
        String result = new BASE64Encoder().encode(byteAes);
        //由于base64的字节长度最多为74，所以超过74个字符后会加上/r/n的换行符
        return result.replaceAll("[\\s*\t\n\r]","");
    }

    /**
     * AES加密
     * @param secretKey 密钥
     * @param plaintext 明文
     * @param aesProperties AES配置类
     * @return
     * @throws Exception
     */
    public static String aesEncode(String secretKey,String plaintext, AesProperties aesProperties) throws Exception{
        return aesEncode(secretKey, plaintext, aesProperties.getAlgorithmPattern(), aesProperties.getPaddingType(),aesProperties.getVector());
    }

    /**
     * AES加密
     * @param secretKey 密钥
     * @param plaintext 明文
     * @param algorithmPattern 算法模式
     * @param paddingType 填充模式
     * @return
     * @throws Exception
     */
    public static String aesEncode(String secretKey,String plaintext,String algorithmPattern,String paddingType) throws Exception{
        return aesEncode(secretKey, plaintext, algorithmPattern, paddingType,null);
    }

    /**
     * AES加密，默认为:ECB + PKCS5Padding
     * @param secretKey 密钥
     * @param plaintext 明文
     * @return
     * @throws Exception
     */
    public static String aesEncode(String secretKey,String plaintext) throws Exception{
        return aesEncode(secretKey, plaintext, AlgorithmPatternEnum.ECB.getPattern(), PaddingTypeEnum.PKCS5_PADDING.getPaddingType());
    }

    /**
     * AES解密
     * @param secretKey 密钥
     * @param cipherText 密文
     * @param algorithmPattern 算法模式
     * @param paddingType 填充模式
     * @param vector 向量
     * @return
     * @throws Exception
     */
    public static String aesDecode(String secretKey,String cipherText, String algorithmPattern,String paddingType,String vector) throws Exception{
        //获取密码器
        Cipher cipher = createCipher(Cipher.DECRYPT_MODE,secretKey,algorithmPattern,paddingType,vector);
        //将密文解码成字节数组
        byte [] byteContent =  new BASE64Decoder().decodeBuffer(cipherText);
        //解密
        byte [] byteDecode = cipher.doFinal(byteContent);
        return new String(byteDecode, StandardCharsets.UTF_8);
    }
    /**
     * AES解密
     * @param secretKey 密钥
     * @param cipherText 密文
     * @param algorithmPattern 算法模式
     * @param paddingType 填充模式
     * @return
     * @throws Exception
     */
    public static String aesDecode(String secretKey,String cipherText, String algorithmPattern,String paddingType) throws Exception{
        return aesDecode(secretKey,cipherText,algorithmPattern,paddingType,null);
    }

    /**
     * AES解密,默认为:ECB + PKCS5Padding
     * @param secretKey 密钥
     * @param cipherText 密文
     * @return
     * @throws Exception
     */
    public static String aesDecode(String secretKey,String cipherText) throws Exception{
        return aesDecode(secretKey,cipherText,AlgorithmPatternEnum.ECB.getPattern(), PaddingTypeEnum.PKCS5_PADDING.getPaddingType());
    }

    /**
     *  AES解密
     * @param secretKey 密钥
     * @param cipherText 密文
     * @param aesProperties Aes配置类
     * @return
     * @throws Exception
     */
    public static String aesDecode(String secretKey,String cipherText, AesProperties aesProperties) throws Exception{
        return aesDecode(secretKey,cipherText,aesProperties.getAlgorithmPattern(), aesProperties.getPaddingType(),aesProperties.getVector());
    }

}
