package com.jsf.service;

import com.jsf.database.model.ResMsg;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 16:05
 */
@FeignClient(value = "ms-inventory", fallback = InventoryFallback.class)
public interface InventoryService {

    @PostMapping("/updateInventory")
    ResMsg updateInventory(@RequestParam("productId") Integer productId, @RequestParam("amount") Integer amount);

}
