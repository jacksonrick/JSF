package com.jsf.utils.string;

import com.jsf.utils.date.DateUtil;
import com.jsf.utils.exception.SysException;
import com.jsf.utils.math.RandomUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符处理工具类
 *
 * @author rick
 * @version v3.0
 */
public class StringUtil {

    public static final String EMPTY_STRING = "";

    /**
     * 获取订单编号（时间戳+8位随机数）
     *
     * @return
     */
    public static String getOrderCode() {
        return DateUtil.dateToStr(new Date(), DateUtil.YYYYMMDDHHMMSSSS) + (int) (Math.random() * 90000 + 10000);
    }

    /**
     * 获取手机验证码
     *
     * @param size 位数
     * @return
     */
    public static String getSmsCode(int size) {
        String base = "0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int number = RandomUtil.SECURE_RANDOM.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 将字符串转换为一个新的字符数组-空格分隔符
     *
     * @param str
     * @return
     */
    public static String[] strToArray(String str) {
        return strToArray(str, "");
    }

    /**
     * 字符串转数组
     *
     * @param str
     * @param separator
     * @return
     */
    public static String[] strToArray(String str, String separator) {
        if (isNotBlank(str)) {
            return str.split(separator);
        }
        return new String[]{};
    }

    /**
     * 字符串转List
     *
     * @param str
     * @return
     */
    public static List<String> strToList(String str) {
        String[] arr = strToArray(str, ",");
        return Arrays.asList(arr);
    }

    /**
     * 字符串转Int数组
     *
     * @param str
     * @param separator
     * @return
     */
    public static Integer[] strToIntArray(String str, String separator) {
        if (isBlank(str)) {
            return new Integer[]{};
        }
        String[] strs = str.split(separator);
        if (strs.length == 0) {
            return new Integer[]{};
        }
        Integer[] ints = new Integer[strs.length];
        for (int i = 0; i < strs.length; i++) {
            ints[i] = Integer.valueOf(strs[i]);
        }
        return ints;
    }

    /**
     * 字符串转Long数组
     *
     * @param str
     * @param separator
     * @return
     */
    public static Long[] strToLongArray(String str, String separator) {
        if (isBlank(str)) {
            return new Long[]{};
        }
        String[] strs = str.split(separator);
        if (strs.length == 0) {
            return new Long[]{};
        }
        Long[] longs = new Long[strs.length];
        for (int i = 0; i < strs.length; i++) {
            longs[i] = Long.valueOf(strs[i]);
        }
        return longs;
    }

    // 默认逗号
    public static Integer[] strToIntArray(String str) {
        return strToIntArray(str, ",");
    }

    public static Long[] strToLongArray(String str) {
        return strToLongArray(str, ",");
    }


    static Pattern PATTERN_HTML = Pattern.compile("<[^>]+>");

    /**
     * 去除HTML标签
     *
     * @param str
     * @return
     */
    public static String delHTMLTag(String str) {
        if (StringUtil.isBlank(str)) {
            return "";
        }
        Matcher matcher = PATTERN_HTML.matcher(str);
        if (matcher.find()) {
            return matcher.replaceAll("");
        }
        return str;
    }

    /**
     * 获取随机代码
     *
     * @param size 长度
     * @return
     */
    public static String getRandomCode(int size) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            int number = RandomUtil.SECURE_RANDOM.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 文件随机命名
     * <p>规则：当前时间(Long)+5位随机数+原文件后缀</p>
     *
     * @param file 源文件
     * @return
     */
    public static String randomFilename(String file) {
        String temp = file.substring(file.lastIndexOf("."));
        return System.currentTimeMillis() + "" + (int) (Math.random() * 90000 + 10000) + temp;
    }

    /**
     * 文件随机命名
     *
     * @return
     */
    public static String randomFilename() {
        return System.currentTimeMillis() + "" + (int) (Math.random() * 90000 + 10000);
    }

    /**
     * 获取文件类型【后缀】
     *
     * @param file
     * @return
     */
    public static String getFileType(String file) {
        return file.substring(file.lastIndexOf(".") + 1);
    }

    public static final String[] number = new String[]{"十", "一", "二", "三", "四", "五", "六", "七", "八", "九"};

    /**
     * 阿拉伯数字转中文
     *
     * @return
     */
    public static String getColumnNum(String num) {
        char[] cr = num.toCharArray();
        int length = cr.length;
        int[] arr = new int[length];
        for (int i = 0; i < cr.length; i++) {
            arr[i] = Integer.parseInt(cr[i] + "");
        }
        StringBuffer strBuffer = new StringBuffer();
        switch (length) {
            case 1:
                strBuffer.append(number[arr[0]]);
                break;
            case 2:
                if ("10".equals(num)) {
                    strBuffer.append(number[arr[1]]);
                    break;
                } else if (arr[0] == 1) {
                    strBuffer.append(number[0]).append(number[arr[1]]);
                    break;
                } else if (arr[1] == 0) {
                    strBuffer.append(number[arr[0]]).append(number[arr[1]]);
                } else {
                    strBuffer.append(number[arr[0]]).append(number[0]).append(number[arr[1]]);
                }
                break;
            default:
                break;
        }
        return strBuffer.toString();
    }

    /**
     * 判断字符串是否为空
     *
     * @param cs
     * @return
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    private static final int SESSION_ID_BYTES = 16;

    /**
     * 构造SESSIONID
     *
     * @return
     */
    public static synchronized String getTokenId() {
        byte[] bytes = new byte[SESSION_ID_BYTES];
        // 产生16字节的byte
        RandomUtil.SECURE_RANDOM.nextBytes(bytes);
        // 取摘要,默认是"MD5"算法
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new SysException(e.getMessage(), e);
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

    static Pattern PATTERN_LETTER = Pattern.compile("[A-Z]");

    /**
     * 将驼峰命名改为下划线
     * 如：bBindAdsd -> b_bind_adsd
     *
     * @param str
     * @return
     */
    public static StringBuffer underline(String str) {
        Matcher matcher = PATTERN_LETTER.matcher(str);
        StringBuffer sb = new StringBuffer(str);
        if (matcher.find()) {
            sb = new StringBuffer();
            //将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            //正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            //把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb;
        }
        return underline(sb.toString());
    }
}
