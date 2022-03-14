package com.jsf.service;

import com.jsf.database.mapper.OrderMapper;
import com.jsf.database.model.Order;
import com.jsf.database.model.ResMsg;
import com.jsf.exception.ServiceException;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
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
     * <p>@GlobalTransactional 开启全局事务</p>
     *
     * @param userId
     * @param productId
     * @param money
     */
    @GlobalTransactional(rollbackFor = Exception.class)
    public void order(Integer userId, Integer productId, Integer money) {
        Order order = new Order(getOrderno(), userId, productId, money);
        // 下单
        orderMapper.insert(order);
        // 减少库存
        System.out.println(new Date() + " 开始调用updateInventory...");
        ResMsg r1 = inventoryService.updateInventory(productId, 1);
        System.out.println(r1);
        // 1、微服务开启全局异常处理
        // 2、熔断降级开启
        // 3、手动抛出异常让分布式事务生效
        if (r1.getCode() != 0) {
            throw new ServiceException(r1.getMsg());
        }
        // 减少用户钱包金额
        System.out.println(new Date() + " 开始调用updateMember...");
        ResMsg r2 = memberService.updateMember(userId, money);
        System.out.println(r2);
        if (r2.getCode() != 0) {
            throw new ServiceException(r2.getMsg());
        }

        //throw new RuntimeException("抛出异常");
    }

    private String getOrderno() {
        LocalDateTime dt = LocalDateTime.now();
        return dt.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    /**
     * 注意：如果有其他包含事务的方法，不需要seata代理，请另外新建一个类，
     * 即使使用@Transactional也会被seata代理
     */

}
