package com.jsf.config;

import com.jsf.model.UserInfo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 11:55
 */
@Service("accessPermission")
public class AccessPermission {

    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) principal;
            String curURI = request.getRequestURI(); //请求地址

            // 修改此变量来测试效果
            String[] thisUserPermissions = {"/sys/list", "/sys/view"};
            for (String permission : thisUserPermissions) {
                if (permission.equals(curURI)) {
                    return true;
                }
            }

            // 这里自定义鉴权逻辑
            // 可以从redis、数据库读取
            // return true;
        }
        return false;
    }

}
