package com.jsf.config;

import com.jsf.model.UserInfo;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description: 按钮权限的控制
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 11:55
 */
@Component
public class AccessPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) principal;
            String key = null != permission ? permission.toString() : "";

            // 修改此变量来测试效果
            String[] thisUserPermissions = {"sys:list", "sys:view"};
            for (String p : thisUserPermissions) {
                if (p.equals(key)) {
                    return true;
                }
            }

            // 这里自定义鉴权逻辑
            // 可以从redis、数据库读取
            // return true;
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String targetType, Object permission) {
        return false;
    }
}
