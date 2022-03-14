package com.jsf.service;

import com.jsf.database.model.ResMsg;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 16:06
 */
@Component
public class InventoryFallback implements InventoryService {
    @Override
    public ResMsg updateInventory(Integer productId, Integer amount) {
        return ResMsg.fail(-100, "库存服务不可用");
    }
}
