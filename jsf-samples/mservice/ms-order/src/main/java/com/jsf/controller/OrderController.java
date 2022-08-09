package com.jsf.controller;

import com.jsf.database.model.ResMsg;
import com.jsf.service.OrderSingleService;
import com.jsf.service.OrderSeataService;
import com.jsf.service.OrderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: 下单入口
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:50
 */
@RestController
public class OrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private OrderSeataService orderSeataService;
    @Resource
    private OrderSingleService orderSingleService;

    /**
     * OrderService 分布式事务
     *
     * @param userId
     * @param productId
     * @param money
     * @return
     * @throws Exception
     */
    @PostMapping("/order")
    public ResMsg order(Integer userId, Integer productId, Integer money) throws Exception {
        System.out.println(new Date() + "开始下单，userId=" + userId + ",productId=" + productId + ",money" + money);
        //TimeUnit.SECONDS.sleep(1);
        orderService.order(userId, productId, money);
        return ResMsg.success();
    }

    /**
     * OrderSeataService seata分布式事务
     *
     * @param userId
     * @param productId
     * @param money
     * @return
     */
    @PostMapping("/order/seata")
    public ResMsg orderSeata(Integer userId, Integer productId, Integer money) {
        System.out.println(new Date() + "开始下单，userId=" + userId + ",productId=" + productId + ",money" + money);
        orderSeataService.order(userId, productId, money);
        return ResMsg.success();
    }

    /**
     * OrderSingleService 单点事务
     *
     * @return
     */
    @PostMapping("/order/single")
    public ResMsg orderSingle() {
        System.out.println(new Date() + "开始下单");
        orderSingleService.order();
        return ResMsg.success();
    }

}
