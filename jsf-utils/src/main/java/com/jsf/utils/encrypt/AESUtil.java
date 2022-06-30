package com.jsf.utils.encrypt;

import com.jsf.utils.string.StringUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Created with IntelliJ IDEA.
 * Description: AES加密解密
 * User: xujunfei
 * Date: 2022-06-06
 * Time: 13:11
 */
public class AESUtil {

    /**
     * 加密
     */
    public static String aesEncrypt(String content) throws Exception {
        return base64Encode(aesEncryptToBytes(content, ENCRYPT_KEY));
    }

    /**
     * 解密
     */
    public static String aesDecrypt(String encryptStr) throws Exception {
        return aesDecryptByBytes(base64Decode(encryptStr), ENCRYPT_KEY);
    }

    // aes加密秘钥
    private static final String ENCRYPT_KEY = "A1B3C5D7E2F4G6H8";
    private static Cipher encryptCipher;
    private static Cipher decryptCipher;

    /**
     * AES加密
     */
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
        if (encryptCipher == null) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(encryptKey.getBytes());
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            encryptCipher = cipher;
        }
        return encryptCipher.doFinal(content.getBytes("utf-8"));
    }

    /**
     * AES解密
     */
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
        if (decryptCipher == null) {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(decryptKey.getBytes());
            kgen.init(128, secureRandom);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(kgen.generateKey().getEncoded(), "AES"));
            decryptCipher = cipher;
        }
        byte[] decryptBytes = decryptCipher.doFinal(encryptBytes);
        return new String(decryptBytes);
    }

    public static String base64Encode(byte[] bytes) {
        return new BASE64Encoder().encode(bytes);
    }

    public static byte[] base64Decode(String base64Code) throws Exception {
        return StringUtil.isBlank(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
    }

}
