package com.jsf.service;

import com.jsf.model.UserInfo;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-07-08
 * Time: 17:01
 */
@Service
public class UserService {

    public UserInfo findUserByName(String name) {
        UserInfo info = new UserInfo();
        info.setId(1);
        info.setUsername(name);
        return info;
    }

}
