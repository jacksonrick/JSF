package com.jsf.config.provider;

import com.jsf.config.handler.PasswordEncoders;
import com.jsf.model.OAuthUser;
import com.jsf.service.OUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.OUserExtDetail;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义的权限校验
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 16:14
 */
@Component
public class OAuthAuthenticationProvider implements AuthenticationProvider {

    private static Logger log = LoggerFactory.getLogger(OAuthAuthenticationProvider.class);

    @Autowired
    private OUserDetailService oUserDetailService;

    /**
     * 自定义验证方式
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取认证信息
        Object details = authentication.getDetails();
        if (details instanceof OUserExtDetail) { // sso default[WebAuthenticationDetails]
            log.info("sso login user: " + authentication.getName());
            // 验证码等校验
            OUserExtDetail detail = (OUserExtDetail) authentication.getDetails();
            if (!detail.getVerify().equalsIgnoreCase(detail.getVerifySession())) {
                throw new BadCredentialsException("04"); //验证码错误
            }
            // 从SavedRequest中获取clientId
            //HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //HttpSession session = currentRequest.getSession(false);
            //SavedRequest savedRequest = (SavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            //if (savedRequest != null) {
            //    String[] clientIds = savedRequest.getParameterMap().get("client_id");
            //    if (clientIds != null && clientIds.length > 0) {
            //        // 设置clientId
            //        detail.setClientId(clientIds[0]);
            //    }
            //}
        } else if (details instanceof LinkedHashMap) { // password
            //LinkedHashMap detailMap = (LinkedHashMap) authentication.getDetails();
            //String clientId = (String) detailMap.get("client_id");
            log.info("password login detail: " + authentication.getDetails());
        } else {
            throw new RuntimeException("未做处理的认证方式");
        }

        // 用户名密码校验
        OAuthUser userDetail = (OAuthUser) oUserDetailService.loadUserByUsername(authentication.getName());
        if (userDetail.getDisabled()) {
            throw new BadCredentialsException("01"); //账户禁用
        }
        if (userDetail.getLocks() >= 5) {
            throw new BadCredentialsException("02"); //账户已锁定
        }
        PasswordEncoder passwordEncoder = PasswordEncoders.getEncoder("bc");
        if (!userDetail.getUsername().equals(authentication.getName()) ||
                !passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userDetail.getPassword())) {
            oUserDetailService.updateLocks(userDetail.getUsername());
            throw new BadCredentialsException("03"); //密码错误
        }
        if (userDetail.getLocks() > 0) { //密码正确，如果有锁，则置0
            oUserDetailService.updateLock0(userDetail.getUsername());
        }

        Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();
        // 这里的pricipal也可以是用户id、或对象userDetail 等
        return new UsernamePasswordAuthenticationToken(userDetail.getUsername(), userDetail.getPassword(), authorities);
    }

    @Override
    public boolean supports(Class<?> cls) {
        return true;
    }
}
