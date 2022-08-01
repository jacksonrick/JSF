package com.jsf.config;

import com.jsf.model.OAuthUserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录成功处理
 */
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuthLoginSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuthUserInfo oAuthUserInfo = (OAuthUserInfo) authentication.getPrincipal();
        log.info("登陆成功：" + oAuthUserInfo);
        // 初始化数据
        //

        // 跳转到主页面
        response.sendRedirect("/");

        // JSON形式
        /*response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> map = new HashMap<>(3);
        map.put("code", HttpStatus.OK.value());
        map.put("success", true);
        map.put("message", "登录成功");
        response.getWriter().write(JSONUtil.toJsonStr(map));*/
    }

}
