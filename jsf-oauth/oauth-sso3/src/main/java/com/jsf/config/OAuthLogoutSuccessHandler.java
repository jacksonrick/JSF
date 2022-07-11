package com.jsf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录
 **/
@Component
public class OAuthLogoutSuccessHandler implements LogoutSuccessHandler {

    @Value("${spring.security.auth-server}")
    private String authServer;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletRequest.getSession().invalidate();
        // 退出到SSO
        httpServletResponse.sendRedirect(authServer + "/logout");
    }
}
