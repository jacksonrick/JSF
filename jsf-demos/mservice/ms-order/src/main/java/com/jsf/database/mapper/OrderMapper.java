package com.jsf.database.mapper;

import com.jsf.database.model.Order;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2021-02-05
 * Time: 14:38
 */
public interface OrderMapper {

    int insert(Order order);

}
