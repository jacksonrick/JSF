package org.springframework.security;

import com.jsf.config.ICONSTANT;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description: 添加自定义校验字段
 * <p>springsecurity反序列化时校验了包名：org.springframework.security，所以在这里单独建立个包</p>
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 16:30
 */
public class OUserExtDetail extends WebAuthenticationDetails {

    // 用户填写的验证码值
    private final String verify;
    // SESSION获取的验证码值
    private final String verifySession;
    // 客户端ID
    private String clientId;

    public OUserExtDetail(HttpServletRequest request) {
        super(request);
        verify = request.getParameter(ICONSTANT.SE_VERIFY);
        verifySession = (String) request.getSession().getAttribute(ICONSTANT.SE_VERIFY);
    }

    public String getVerify() {
        return verify;
    }

    public String getVerifySession() {
        return verifySession;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
