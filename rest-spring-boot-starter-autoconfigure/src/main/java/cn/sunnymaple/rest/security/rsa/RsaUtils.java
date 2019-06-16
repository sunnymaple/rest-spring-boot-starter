package cn.sunnymaple.rest.security.rsa;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

/**
 * RAS工具类
 * @auther: wangzb
 * @date: 2019/6/15 22:52
 */
public class RsaUtils {

    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";

    /**
     * 默认的签名算法
     */
    private static final String DEFAULT_SIGNATURE_ALGORITHM = SignatureAlgorithmEnum.MD5_WITH_RSA.getAlgorithm();
    /**
     * RSA的最小密钥长度 单位字节
     */
    public static final Integer MIN_LENGTH = 64;
    /**
     * RSA的最大密钥长度 单位字节
     */
    public static final Integer MAX_LENGTH = 8192;

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成密钥对(公钥和私钥)
     * @param secretKeyLength 密钥长度（单位：字节）
     *                        长度为64~8192之间的数
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair(Integer secretKeyLength) throws Exception {
        if (secretKeyLength <MIN_LENGTH || secretKeyLength>MAX_LENGTH){
            throw new RsaSecretKeyLengthException();
        }
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
        keyPairGen.initialize(secretKeyLength * 8,secureRandom);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        return keyPair;
    }
    /**
     * 生成密钥对(公钥和私钥)
     * @return
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        return getKeyPair(MIN_LENGTH);
    }

    /**
     * 私钥获取签名
     * @param data  签名的内容
     * @param signatureAlgorithm 签名算法
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String signature(byte[] data,String signatureAlgorithm, String privateKey) throws Exception {
        byte[] keyBytes = Base64Utils.decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initSign(privateK);
        signature.update(data);
        return Base64Utils.encode(signature.sign());
    }

    /**
     * 私钥获取签名
     * @param data  签名的内容
     * @param signatureAlgorithm 签名算法
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String signature(String data,String signatureAlgorithm, String privateKey) throws Exception {
        return signature(data.getBytes(),signatureAlgorithm,privateKey);
    }

    /**
     * 私钥获取签名(默认使用MD5withRSA算法)
     * @param data  签名的内容
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String signature(byte[] data, String privateKey) throws Exception {
        return signature(data,DEFAULT_SIGNATURE_ALGORITHM,privateKey);
    }

    /**
     * 私钥获取签名(默认使用MD5withRSA算法)
     * @param data  签名的内容
     * @param privateKey 私钥
     * @return
     * @throws Exception
     */
    public static String signature(String data, String privateKey) throws Exception {
        return signature(data,DEFAULT_SIGNATURE_ALGORITHM,privateKey);
    }

    /**
     * 使用公钥验证签名
     * @param data 签名的内容
     * @param signatureAlgorithm 签名算法
     * @param publicKey 公钥
     * @param sign 签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String signatureAlgorithm, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64Utils.decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64Utils.decode(sign));
    }

    /**
     * 使用公钥验证签名
     * @param data 签名的内容
     * @param signatureAlgorithm 签名算法
     * @param publicKey 公钥
     * @param sign 签名
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String signatureAlgorithm, String publicKey, String sign)
            throws Exception {
        return verify(data.getBytes(), signatureAlgorithm, publicKey, sign);
    }

    /**
     * 使用公钥验证签名
     * @param data 签名的内容
     * @param publicKey 公钥
     * @param sign 签名
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        return verify(data, DEFAULT_SIGNATURE_ALGORITHM, publicKey, sign);
    }

    /**
     * 使用公钥验证签名
     * @param data 签名的内容
     * @param publicKey 公钥
     * @param sign 签名
     * @return
     * @throws Exception
     */
    public static boolean verify(String data, String publicKey, String sign)
            throws Exception {
        return verify(data.getBytes(), DEFAULT_SIGNATURE_ALGORITHM, publicKey, sign);
    }

    /**
     * 获取私钥 并进行Base64编码
     * @param keyPair 密钥对（公钥和私钥）
     * @return 返回一个 Base64 公钥编码后的字符串
     */
    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 获取私钥(并进行Base64编码，返回一个 Base64 编码后的字符串)
     * @param keyPair 密钥对（公钥和私钥）
     * @return 返回一个 Base64 编码后的私钥字符串
     */
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将Base64编码后的公钥转换成 PublicKey 对象
     * @param pubStr 公钥字符串
     * @return PublicKey
     */
    public static PublicKey pubStr2PublicKey(String pubStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将Base64编码后的私钥转换成 PrivateKey 对象
     * @param priStr 私钥字符串
     * @return PrivateKey
     */
    public static PrivateKey priStr2PrivateKey(String priStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] bytes = Base64.getDecoder().decode(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    /**
     * 私钥解密
     * 注意：解密要求密文最大长度为128字节
     * @param cipherText 密文
     * @param privateKey 私钥
     * @return 解密后的明文
     * @throws Exception
     */
    public static String privateKeyDecrypt(byte[] cipherText, String privateKey) throws Exception {
        PrivateKey key = priStr2PrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        //对数据分段解密
        int inputLen = cipherText.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(cipherText, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(cipherText, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        //明文
        byte[] plaintext = out.toByteArray();
        out.close();
        //将明文转base64字符串
        return Base64.getEncoder().encodeToString(plaintext);
    }

    /**
     * 私钥解密
     * @param cipherText 密文
     * @param privateKey 私钥
     * @return 解密后的明文
     * @throws Exception
     */
    public static String privateKeyDecrypt(String cipherText, String privateKey)
            throws Exception {
        return privateKeyDecrypt(Base64.getDecoder().decode(cipherText),privateKey);
    }

    /**
     * 公钥加密
     * @param plaintext 明文
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static String publicKeyEncrypt(byte[] plaintext, String publicKey)
            throws Exception {
        //获取公钥
        PublicKey key = pubStr2PublicKey(publicKey);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        int inputLen = plaintext.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(plaintext, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(plaintext, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        //密文
        byte[] cipherText = out.toByteArray();
        out.close();
        //将密文转字符串
        return Base64.getEncoder().encodeToString(cipherText);
    }

    /**
     * 公钥加密
     * @param plaintext 明文
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static String publicKeyEncrypt(String plaintext, String publicKey) throws Exception {
        return publicKeyEncrypt(Base64.getDecoder().decode(plaintext),publicKey);
    }




}
