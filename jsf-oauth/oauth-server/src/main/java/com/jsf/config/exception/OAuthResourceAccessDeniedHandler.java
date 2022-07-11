package com.jsf.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsf.model.ResMsg;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description: OAuth resource 无权访问异常捕获
 * User: xujunfei
 * Date: 2022-07-06
 * Time: 15:10
 */
@Component
public class OAuthResourceAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 自定义返回格式内容
        ResMsg resMsg = ResMsg.unauth("用户访问无权限资源", accessDeniedException.getMessage());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value()); // 权限不足403
        response.getWriter().write(new ObjectMapper().writeValueAsString(resMsg));
    }

}
