package com.jsf.config;


import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录用户访问无权限异常处理
 */
public class OAuthLoginDeineHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuthLoginDeineHandler.class);

    private String unauthUrl;

    public OAuthLoginDeineHandler() {
        this.unauthUrl = "/unauth";
    }

    public OAuthLoginDeineHandler(String unauthUrl) {
        this.unauthUrl = unauthUrl;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        log.info("无权访问：" + e.getMessage());
        if (isAjaxRequest(request)) {
            Map<String, Object> map = new HashMap<>(3);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            map.put("code", HttpStatus.FORBIDDEN.value());
            map.put("success", false);
            map.put("message", "无访问权限");
            response.getWriter().write(JSON.toJSONString(map));
        } else {
            RequestDispatcher dispatcher = request.getRequestDispatcher(this.unauthUrl);
            dispatcher.forward(request, response);
        }
    }

    boolean isAjaxRequest(ServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"));
    }
}