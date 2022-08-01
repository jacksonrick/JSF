package com.jsf.config;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录失败处理
 */
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuthLoginFailureHandler.class);

    private String failureUrl;

    public OAuthLoginFailureHandler() {
        this.failureUrl = "/failure";
    }

    public OAuthLoginFailureHandler(String failureUrl) {
        this.failureUrl = failureUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.info("登陆失败：" + e.getMessage());
        if (isAjaxRequest(request)) {
            Map<String, Object> map = new HashMap<>(3);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            map.put("success", false);
            if (e instanceof OAuth2AuthenticationException) {
                OAuth2AuthenticationException ex = (OAuth2AuthenticationException) e;
                map.put("message", "登录失败：" + ex.getError().getDescription());
            } else {
                map.put("message", e.getMessage());
            }
            response.getWriter().write(JSON.toJSONString(map));
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher(this.failureUrl);
            dispatcher.forward(request, response);
        }
    }

    boolean isAjaxRequest(ServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"));
    }

}
