package com.jsf.config;

import com.jsf.config.exception.OAuthResourceAccessDeniedHandler;
import com.jsf.config.exception.OAuthResourceAuthenticationEntryPointHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

/**
 * Created with IntelliJ IDEA.
 * Description: 资源服务
 * User: xujunfei
 * Date: 2018-06-07
 * Time: 09:59
 */
@Configuration
@EnableResourceServer
public class OAuthResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private OAuthResourceAccessDeniedHandler oAuthResourceAccessDeniedHandler;
    @Autowired
    private OAuthResourceAuthenticationEntryPointHandler oAuthResourceAuthenticationEntryPointHandler;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        // 自定义异常返回信息
        resources.accessDeniedHandler(oAuthResourceAccessDeniedHandler);
        resources.authenticationEntryPoint(oAuthResourceAuthenticationEntryPointHandler);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 若认证服务器和资源服务器在一起，则需要单独定义接口资源前缀，不要以/oauth、/authentication开头
                .antMatcher("/api/**")
                .authorizeRequests()
                // 需要保护的资源
                .antMatchers("/api/user", "/api/tokenKey").authenticated()
                // 1、此资源是否拥有某角色（hasRole不能以ROLE_开头，oauth_client_details表字段authorities角色名以ROLE_开头）
                //.antMatchers("/api/userSync").hasRole("SYS") // 或hasAuthority("ROLE_SYS")
                // 2、此资源是否拥有某范围（此值与oauth_client_details表scope字段）
                //.antMatchers("/api/userSync").access("#oauth2.hasScope('all')")
                .and().csrf().disable()
        ;
    }

}
