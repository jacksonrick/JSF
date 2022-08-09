package com.jsf.service;

import com.jsf.database.mapper.OrderMapper;
import com.jsf.database.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: 订单服务 - 单点事务
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:49
 */
@Service
public class OrderSingleService {

    @Resource
    private OrderMapper orderMapper;

    @Transactional
    public void order() {
        Order order1 = new Order(getOrderno(), 1, 1, 1);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
        }
        Order order2 = new Order(getOrderno(), 2, 2, 2);

        orderMapper.insert(order1);
        System.out.println(1 / 0);
        orderMapper.insert(order2);
    }

    private String getOrderno() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
