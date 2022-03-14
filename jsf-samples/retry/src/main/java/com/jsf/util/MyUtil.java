package com.jsf.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-12-15
 * Time: 14:1204
 */
public class MyUtil {

    static Random random = new Random();

    static AtomicInteger atm = new AtomicInteger(1);

    /**
     * 模拟推送成功与失败
     * 假设为0是成功
     *
     * @return
     */
    public static boolean push(String orderno) {
        int i = random.nextInt(10);
        System.out.println("推送订单：" + orderno + "，结果：" + i);
        return i == 0;
    }

    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    static DateTimeFormatter dtf2 = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static String getOrderCode() {
        LocalDateTime dt = LocalDateTime.now();
        String date = dt.format(dtf);
        return date + "-" + atm.getAndIncrement();
    }

    public static String getTime() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.format(dtf2);
    }

}
