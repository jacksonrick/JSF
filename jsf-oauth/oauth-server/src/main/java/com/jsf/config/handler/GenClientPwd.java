package com.jsf.config.handler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class GenClientPwd {

    public static void main(String[] args) {
        String clientId = getTokenId();
        System.out.println("clientId为：" + clientId);
        String clientSecret = getTokenId();
        System.out.println("clientSecret为：" + clientSecret);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encrypted = encoder.encode(clientSecret);
        System.out.println("clientSecret加密后为：" + encrypted);
    }

    static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String getTokenId() {
        byte[] bytes = new byte[16];
        // 产生16字节的byte
        SECURE_RANDOM.nextBytes(bytes);
        // 取摘要,默认是"MD5"算法
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        StringBuffer result = new StringBuffer();
        // 转化为16进制字符串
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10)
                result.append((char) ('0' + b1));
            else
                result.append((char) ('A' + (b1 - 10)));
            if (b2 < 10)
                result.append((char) ('0' + b2));
            else
                result.append((char) ('A' + (b2 - 10)));
        }
        return (result.toString());
    }
}
