package com.jsf.utils.encrypt;

import com.jsf.utils.exception.SysException;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 参数加密、验签
 * User: xujunfei
 * Date: 2018-07-09
 * Time: 17:40
 */
public class SignUtil {

    /**
     * 获取密文
     *
     * @param params 参数键值对，不包含sign
     * @param secret 固定密钥或随机字符串
     * @return
     */
    public static String getSign(TreeMap<String, String> params, String secret) {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> iterator = params.keySet().iterator(); iterator.hasNext(); ) {
            String key = iterator.next();
            String val = params.get(key);
            if (null != val) {
                // 过滤空值和key为sign
                if (!StringUtils.equals("sign", key)) {
                    sb.append(key).append("=").append(val);
                }
            }
        }
        String dd = EncoderByMd5(sb.toString()).toUpperCase();
        String signaOwn = EncoderByMd5(dd + secret).toUpperCase();
        return signaOwn;
    }


    /**
     * 校验
     *
     * @param params 参数键值对，包含sign
     * @param secret 固定密钥
     * @return
     */
    public static boolean checkSign(TreeMap<String, String> params, String secret) {
        String signaInterface = String.valueOf(params.get("sign"));
        String signaOwn = getSign(params, secret);
        return StringUtils.equals(signaOwn, signaInterface);
    }

    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    private static String EncoderByMd5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes("UTF-8"));
            return bytes2Hex(md.digest());
        } catch (Exception e) {
            throw new SysException(e.getMessage(), e);
        }
    }


    /**
     * 32位加密补零
     *
     * @param bts
     * @return
     */
    private static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

}
