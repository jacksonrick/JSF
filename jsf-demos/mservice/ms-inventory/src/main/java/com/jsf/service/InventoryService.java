package com.jsf.service;

import com.jsf.database.mapper.InventoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:39
 */
@Service
public class InventoryService {

    @Resource
    private InventoryMapper inventoryMapper;

    @Transactional
    public void updateInventory(Long productId, Integer amount) {
        try {
            // 模拟超时；此处时间必须小于hystrix..timeoutInMilliseconds配置的值
            TimeUnit.MILLISECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        inventoryMapper.update(productId, amount);
        //throw new RuntimeException("抛出异常");
    }

}
