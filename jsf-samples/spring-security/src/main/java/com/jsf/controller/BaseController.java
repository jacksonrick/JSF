package com.jsf.controller;

import com.jsf.model.UserInfo;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-08-09
 * Time: 11:42
 */
public class BaseController {

    protected UserInfo getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (null != authentication) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                // 匿名用户
                return null;
            }
            UserInfo info = (UserInfo) authentication.getPrincipal();
            return info;
        }
        return null;
    }

}
