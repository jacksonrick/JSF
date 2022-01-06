package com.jsf.mapper;

import com.jsf.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2019-11-14
 * Time: 10:18
 */
public interface UserMapper {

    int insert(User user);

    int update(User user);

    User findById(Integer id);

    List<User> findList(User condition);

    List<User> findInfoList(User condition);

    List<User> findByPage(@Param("condition") User condition, @Param("lastId") Integer lastId, @Param("limit") int limit);

    Integer findSum(User condition);
}
