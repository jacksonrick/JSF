package com.jsf.config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsf.model.ResMsg;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * Description: OAuth resource异常捕获
 * User: xujunfei
 * Date: 2022-07-06
 * Time: 15:08
 */
@Component
public class OAuthResourceAuthenticationEntryPointHandler extends OAuth2AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        Throwable cause = authException.getCause();

        // 自定义返回格式内容
        ResMsg resMsg = ResMsg.unauth(authException.getMessage(), authException.getMessage());
        if (cause instanceof OAuth2AccessDeniedException) {
            resMsg.setMsg("资源ID不在范围内");
        } else if (cause instanceof InvalidTokenException) {
            resMsg.setMsg("Token解析失败");
        } else if (authException instanceof InsufficientAuthenticationException) {
            resMsg.setMsg("未携带token");
        } else {
            resMsg.setMsg("未知异常信息");
        }
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.append(new ObjectMapper().writeValueAsString(resMsg));

    }

}
