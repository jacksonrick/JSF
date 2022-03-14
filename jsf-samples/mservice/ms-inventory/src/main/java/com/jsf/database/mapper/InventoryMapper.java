package com.jsf.database.mapper;

import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:38
 */
public interface InventoryMapper {

    int update(@Param("productId") Long productId, @Param("amount") Integer amount);

}
