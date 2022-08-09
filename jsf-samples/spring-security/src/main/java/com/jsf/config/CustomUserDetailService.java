package com.jsf.config;

import com.jsf.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义用户服务
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 14:46
 */
@Component
public class CustomUserDetailService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailService.class);

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("login user: " + username);
        // 数据库查询略
        // 这里模拟一个数据库用户
        return new UserInfo(username, passwordEncoder.encode("123456"),
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_ADMIN,auth:admin"));
        // 使用@PreAuthorize注解，
        // hasRole() 角色必须以ROLE_开头，
        // hasAuthority() 任意完全匹配的字符串
    }
}
