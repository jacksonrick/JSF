package com.jsf.service;

import com.jsf.util.MyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-12-15
 * Time: 14:0631
 */
@Service
public class TaskService {

    public static List<String> successList = new ArrayList<>();
    public static List<String> failList = new ArrayList<>();

    @Resource
    private RetryService retryService;

    static final ExecutorService EXECUTOR = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(100));

    public void start() throws Exception {
        List<Future> results = new ArrayList<>();
        Callable task = (() -> {
            retryService.push(MyUtil.getOrderCode());
            return true;
        });
        for (int i = 0; i < 20; i++) {
            results.add(EXECUTOR.submit(task));
            // 每隔n秒创建一个推送任务
            TimeUnit.SECONDS.sleep(2);
        }

        for (Future result : results) {
            result.get();
        }
        System.out.println("推送成功的订单：" + successList);
        System.out.println("推送失败的订单：" + failList);
    }

}
