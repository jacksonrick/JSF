package com.jsf.config.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: OAuth server异常
 * User: xujunfei
 * Date: 2022-07-06
 * Time: 14:52
 */
@JsonSerialize(using = OAuthServerExceptionSerializer.class)
public class OAuthServerException extends OAuth2Exception {

    private static final Logger log = LoggerFactory.getLogger(OAuthServerException.class);

    public static final String ERROR = "error";
    public static final String DESCRIPTION = "error_description";
    public static final String URI = "error_uri";
    public static final String INVALID_REQUEST = "invalid_request";
    public static final String INVALID_CLIENT = "invalid_client";
    public static final String INVALID_GRANT = "invalid_grant";
    public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";
    public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";
    public static final String INVALID_SCOPE = "invalid_scope";
    public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
    public static final String INVALID_TOKEN = "invalid_token";
    public static final String REDIRECT_URI_MISMATCH = "redirect_uri_mismatch";
    public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";
    public static final String ACCESS_DENIED = "access_denied";
    /**
     * 其他异常
     */
    public static final String METHOD_NOT_ALLOWED = "method_not_allowed";
    public static final String SERVER_ERROR = "server_error";
    public static final String UNAUTHORIZED = "unauthorized";

    private static ConcurrentHashMap<String, String> oAuth2ErrorMap = new ConcurrentHashMap<>();

    /**
     * 映射异常
     */
    static {
        oAuth2ErrorMap.put(INVALID_CLIENT, "无效的客户端");
        oAuth2ErrorMap.put(INVALID_GRANT, "无效的授权模式");
        oAuth2ErrorMap.put(INVALID_SCOPE, "权限不足");
        oAuth2ErrorMap.put(UNSUPPORTED_GRANT_TYPE, "不支持的授权模式类型");
        oAuth2ErrorMap.put(ACCESS_DENIED, "拒绝访问");

        oAuth2ErrorMap.put(METHOD_NOT_ALLOWED, "方法不允许访问");
        oAuth2ErrorMap.put(SERVER_ERROR, "服务器内部异常");
        oAuth2ErrorMap.put(UNAUTHORIZED, "未授权");
    }

    /**
     * oAuth2 ErrorCode的扩展信息
     */
    private String extendMessage;

    public OAuthServerException(String msg, Throwable t) {
        super(msg, t);
    }

    public OAuthServerException(String msg, String oAuth2ErrorCode, Throwable t) {
        super(msg, t);
        log.error("OAuthServer异常 -> msg={}，errorCode={}", msg, oAuth2ErrorCode);
        String oAuth2ErrorMessage = oAuth2ErrorMap.get(oAuth2ErrorCode);
        if (oAuth2ErrorMessage == null) {
            this.extendMessage = t.getCause().getMessage();
        } else {
            this.extendMessage = oAuth2ErrorMessage;
        }
    }

    public String getExtendMessage() {
        return extendMessage;
    }

    public void setExtendMessage(String extendMessage) {
        this.extendMessage = extendMessage;
    }

}
