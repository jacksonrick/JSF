package com.jsf.controller;

import com.jsf.database.model.User;
import com.jsf.service.UserService;
import com.jsf.system.handler.ApiLocker;
import com.jsf.utils.annotation.ApiLock;
import com.jsf.utils.annotation.RepeatSubmit;
import com.jsf.utils.annotation.Token;
import com.jsf.utils.entity.ResMsg;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-10-27
 * Time: 16:17
 */
@RestController
@RequestMapping("/api")
public class TestController {

    @Resource
    private UserService userService;

    /**
     * 用户信息
     * TODO 防重复提交TEST
     *
     * @param userId
     * @return
     */
    @PostMapping("/userinfo")
    @Token
    @RepeatSubmit(timeout = 5)
    public ResMsg userinfo(Long userId) {
        User user = userService.findUserById(userId);
        return ResMsg.successdata(user);
    }

    /**
     * TODO 接口锁
     *
     * @return
     */
    @GetMapping("/report")
    @ApiLock(name = "KEY01")
    public ResMsg report() {
        this.reportTask();
        return ResMsg.success();
    }

    // 耗时/资源任务
    void reportTask() {
        System.out.println("任务开始");
        Runnable runnable = () -> {
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("任务结束");
                ApiLocker.unlock("KEY01");
            }
        };
        new Thread(runnable).start();
    }


}
