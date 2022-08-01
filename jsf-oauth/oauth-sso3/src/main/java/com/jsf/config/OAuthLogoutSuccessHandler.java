package com.jsf.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录
 **/
public class OAuthLogoutSuccessHandler implements LogoutSuccessHandler {

    private String authServer;

    public OAuthLogoutSuccessHandler(String authServer) {
        this.authServer = authServer;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletRequest.getSession().invalidate();
        // 退出到SSO
        httpServletResponse.sendRedirect(authServer + "/logout");
    }
}
