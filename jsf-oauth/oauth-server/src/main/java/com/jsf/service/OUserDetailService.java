package com.jsf.service;

import com.jsf.database.model.User;
import com.jsf.database.model.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * User Service
 */
@Component
public class OUserDetailService implements UserDetailsService {

    @Autowired
    private OUserService oUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = oUserService.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("00"); // 用户名不存在
        }
        return new UserDetail(user);
    }

    public void updateLocks(String username) {
        oUserService.updateLocks(username);
    }

    public void updateLock0(String username) {
        oUserService.updateLock0(username);
    }
}
