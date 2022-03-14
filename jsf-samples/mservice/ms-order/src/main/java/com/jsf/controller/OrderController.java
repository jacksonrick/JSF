package com.jsf.controller;

import com.jsf.database.model.ResMsg;
import com.jsf.service.Order2Service;
import com.jsf.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:50
 */
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private Order2Service orderService2;

    @PostMapping("/order")
    public ResMsg order(Integer userId, Integer productId, Integer money) throws InterruptedException {
        System.out.println(new Date() + "开始下单，userId=" + userId + ",productId=" + productId + ",money" + money);
        //TimeUnit.SECONDS.sleep(1);
        orderService.order(userId, productId, money);
        return ResMsg.success();
    }

    @PostMapping("/order2")
    public ResMsg order2() {
        System.out.println(new Date() + "开始下单");
        orderService2.order();
        return ResMsg.success();
    }

}
