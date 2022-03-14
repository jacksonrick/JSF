package com.jsf.service;

import com.jsf.util.MyException;
import com.jsf.util.MyUtil;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * Description: 重试服务【支付推送DEMO】
 * User: xujunfei
 * Date: 2020-12-15
 * Time: 11:5327
 */
@Service
public class RetryService {

    @Retryable(value = MyException.class, maxAttempts = 3,
            backoff = @Backoff(value = 1000, maxDelay = 10000, multiplier = 1.2),
            recover = "pushCover")
    public void push(String orderno) {
        System.out.println(MyUtil.getTime() + " " + Thread.currentThread().getName() + " 订单：" + orderno);
        if (MyUtil.push(orderno)) {
            TaskService.successList.add(orderno);
            System.out.println(MyUtil.getTime() + " " + Thread.currentThread().getName() + " 推送成功：" + orderno);
            return;
        }
        // 这里测试抛出异常
        throw new MyException(orderno);
    }

    @Recover
    public void pushCover(MyException e, String orderno) {
        System.out.println(MyUtil.getTime() + " " + Thread.currentThread().getName() + " 重试多次失败：" + orderno);
        TaskService.failList.add(orderno);
    }

}
