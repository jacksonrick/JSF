package com.jsf.config;

import com.jsf.config.handler.AuthFailureHandler;
import com.jsf.config.provider.OAuthAuthenticationProvider;
import com.jsf.service.OUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description: Spring Security配置
 * User: xujunfei
 * Date: 2018-06-07
 * Time: 09:59
 */
@Configuration
public class OAuthSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private OUserDetailService oUserDetailService;
    @Autowired
    private OAuthAuthenticationProvider oAuthAuthenticationProvider;
    @Autowired
    private AuthFailureHandler authFailureHandler;

    @Autowired
    @Qualifier("oauthAuthenticationDetailsSource")
    private AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource;

    /**
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 自定义的验证方式
        auth.authenticationProvider(oAuthAuthenticationProvider);
        // Dao方式
        //auth.userDetailsService(oUserDetailService).passwordEncoder(passwordEncoder());
    }

    /**
     * @param web
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/static/**");
    }

    /**
     * 登录表单验证配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().anyRequest().permitAll()
                .and()
                .formLogin().loginPage("/authentication/require").loginProcessingUrl("/authentication/form") //表单验证，自定义登陆页和登陆请求url
                .failureHandler(authFailureHandler) //登陆失败处理
                .authenticationDetailsSource(authenticationDetailsSource) //自定义DetailsSource
                .and()
                .logout().logoutSuccessUrl("/logoutPage") //退出，若配置logoutSuccessHandler，则logoutSuccessUrl无效
                .and()
                .csrf().disable()
        ;
    }
    //.antMatcher("/**").authorizeRequests().antMatchers("/oauth/**").permitAll()

    /* 记住我
        http
            .rememberMe().rememberMeServices(rememberMeServices());

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }
    */

}
