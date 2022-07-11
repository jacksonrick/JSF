package com.jsf.config;

import com.jsf.model.OAuthUserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * RABC认证(role-Based-access control)
 */
@Service("rbacService")
public class RbacService {

    //private AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        // true为放行
        boolean hasPermission = false;
        if (!(principal instanceof OAuthUserInfo)) {
            return false;
        }
        OAuthUserInfo userInfo = (OAuthUserInfo) principal;
        if (true) { //这里可以自定义判断权限逻辑
            return true;
        }
        return hasPermission;
    }

}
