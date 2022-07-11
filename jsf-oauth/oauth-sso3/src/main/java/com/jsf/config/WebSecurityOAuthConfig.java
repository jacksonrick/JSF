package com.jsf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * spring security oauth配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityOAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OAuthUserService oAuthUserService;

    @Autowired
    private OAuthLoginSuccessHandler oAuthLoginSuccessHandler;

    @Autowired
    private OAuthLoginFailureHandler oAuthLoginFailureHandler;

    @Autowired
    private OAuthLogoutSuccessHandler oAuthLogoutSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().antMatchers("/login").permitAll()
                .and().authorizeRequests()
                // 1、自定义RBAC认证
                //.antMatchers("/api/**").access("@rbacService.hasPermission(request, authentication)")
                // 2、此资源是否拥有某角色（hasRole不能以ROLE_开头，oauth_client_details表字段authorities角色名以ROLE_开头）
                //.antMatchers("/api/**").hasRole("ADMIN") // 或hasAuthority("ROLE_ADMIN")
                // /**需要登录
                .antMatchers("/**").authenticated().and()
                // 退出系统
                .logout().logoutUrl("/logout").logoutSuccessHandler(oAuthLogoutSuccessHandler).permitAll();
        http.headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable();
        // 无权限处理
        http.exceptionHandling().accessDeniedHandler(new OAuthLoginDeineHandler());
        // OAuth2认证配置
        http.oauth2Login().loginProcessingUrl("/sso/login")
                //.successHandler(oAuthLoginSuccessHandler) // 自定义登陆成功处理
                .failureHandler(oAuthLoginFailureHandler) // 自定义登陆失败处理
                .userInfoEndpoint().userService(oAuthUserService); // 自定义用户服务
        // OAuth2 rest client
        http.oauth2Client();
        // 重复登录(重新登陆生效)
        // http.sessionManagement().maximumSessions(1).expiredUrl("/web/logout?error");
    }

    @Override
    public void configure(WebSecurity web) {
        //忽略url的登录访问权限
        web.ignoring().antMatchers("/static/**");
    }

}