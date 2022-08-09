package com.jsf.config;

import com.jsf.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2022-08-09
 * Time: 10:41
 */
@Configuration
public class LoginAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CustomUserDetailService userDetailService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserInfo userInfo = (UserInfo) userDetailService.loadUserByUsername(authentication.getName());
        if (userInfo == null) {
            throw new UsernameNotFoundException("用户名" + authentication.getName() + "不存在");
        }
        if (!userInfo.getUsername().equals(authentication.getName()) ||
                !passwordEncoder.matches(String.valueOf(authentication.getCredentials()), userInfo.getPassword())) {
            throw new BadCredentialsException("密码错误");
        }
        return new UsernamePasswordAuthenticationToken(userInfo, userInfo.getPassword(), userInfo.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
