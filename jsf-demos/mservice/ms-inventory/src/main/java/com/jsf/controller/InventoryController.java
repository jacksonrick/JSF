package com.jsf.controller;

import com.jsf.database.model.ResMsg;
import com.jsf.service.InventoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:29
 */
@RestController
public class InventoryController {

    @Resource
    private InventoryService inventoryService;

    @PostMapping("/updateInventory")
    public ResMsg udpateInventory(Long productId, Integer amount) {
        System.out.println(new Date() + " 收到更新库存请求，productId=" + productId + ",amount=" + amount);
        inventoryService.updateInventory(productId, amount);
        return ResMsg.success();
    }

}
