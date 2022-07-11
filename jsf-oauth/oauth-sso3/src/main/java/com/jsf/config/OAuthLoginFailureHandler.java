package com.jsf.config;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录失败处理
 */
@Component
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        Map<String, Object> map = new HashMap<>(3);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
        map.put("success", false);
        if (e instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException ex = (OAuth2AuthenticationException) e;
            if ("user_not_found".equals(ex.getError().getErrorCode())) {
                response.sendRedirect("/unauth?error=user_not_found"); // 此异常跳转页面
                return;
            }
            map.put("message", "登录失败：" + ex.getError().getDescription());
        } else {
            map.put("message", "登录失败：" + e.getMessage());
        }
        response.getWriter().write(JSON.toJSONString(map));
    }

}
