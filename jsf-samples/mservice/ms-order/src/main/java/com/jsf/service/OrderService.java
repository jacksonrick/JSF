package com.jsf.service;

import com.jsf.database.mapper.OrderMapper;
import com.jsf.database.model.Order;
import com.jsf.database.model.ResMsg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description: 订单主服务
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:49
 */
@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private InventoryService inventoryService;
    @Resource
    private MemberService memberService;

    /**
     * 订单服务
     * <ul>
     *     <li>新增订单</li>
     *     <li>减少库存</li>
     *     <li>减少余额</li>
     * </ul>
     *
     * @param userId
     * @param productId
     * @param money
     */
    public void order(Integer userId, Integer productId, Integer money) {
        Order order = new Order(getOrderno(), userId, productId, money);
        // 下单
        orderMapper.insert(order);

        // 减少库存
        System.out.println(new Date() + " 开始调用updateInventory...");
        ResMsg r1 = inventoryService.updateInventory(productId, 1);
        System.out.println(r1);

        // 减少用户钱包金额
        System.out.println(new Date() + " 开始调用updateMember...");
        ResMsg r2 = memberService.updateMember(userId, money);
        System.out.println(r2);
    }

    private String getOrderno() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

}
