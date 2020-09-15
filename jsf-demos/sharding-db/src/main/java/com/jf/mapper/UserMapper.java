package com.jsf.mapper;

import com.jsf.model.User;

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

}
