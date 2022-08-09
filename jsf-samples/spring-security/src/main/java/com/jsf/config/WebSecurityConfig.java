package com.jsf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-06-11
 * Time: 11:55
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private CustomUserDetailService userDetailService;
    @Resource
    private LoginAuthenticationProvider loginAuthenticationProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 1.自定义的安全认证
        auth.authenticationProvider(loginAuthenticationProvider);

        // 2.内置数据库认证，存储用户（需要创建authorities,users表）
        // [@Resource private DataSource dataSource;]
        // auth.jdbcAuthentication().dataSource(dataSource);

        // 3.内存用户
        //auth.inMemoryAuthentication().withUser("admin").password("xxxxxxxxxxx").roles("ADMIN");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        // 1. remember-me使用jdbc存储token，需要创建persistent_logins表
        //JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        //jdbcTokenRepository.setDataSource(dataSource);
        //return jdbcTokenRepository;

        // 2. 使用内存存储，服务重启失效
        return new InMemoryTokenRepositoryImpl();
    }

    /**
     * @param http the {@link HttpSecurity} to modify
     *             过滤器列表：<a href="https://docs.spring.io/spring-security/site/docs/5.0.0.M1/reference/htmlsingle/#ns-custom-filters">...</a>
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         1 antMatchers匹配URL，直接过滤
         2 anyRequest任何URL需要授权
         */
        http.csrf().disable() //禁用跨站请求伪造
                .headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable() //允许iframe嵌套
                .and()
                .authorizeRequests().antMatchers("/", "/forgot/**").permitAll() //允许请求
                .and()
                .authorizeRequests().antMatchers("/sys/**")
                .access("@accessPermission.hasPermission(request,authentication)") //自定义鉴权
                .anyRequest().authenticated() //其它需要登陆
                .and()
                .formLogin().loginPage("/login").loginProcessingUrl("/dologin").permitAll() //自定义登陆页和登陆请求
                .failureHandler(new LoginFailureHandler()) //登陆失败处理器
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/bye").permitAll() //自定义退出请求和退出成功页
                .and()
                .rememberMe().rememberMeCookieName("rememberMe")
                .tokenRepository(persistentTokenRepository()).tokenValiditySeconds(600)
                .userDetailsService(userDetailService) //记住我，生成Cookie(rememberMe)，表单需要传参remember-me=true
                .and()
                .exceptionHandling().authenticationEntryPoint(new LoginEntryPoint())
                .accessDeniedHandler(new LoginAccessDeniedHandler()) //无权限处理器
                .and()
                .sessionManagement().maximumSessions(1).expiredUrl("/login").maxSessionsPreventsLogin(true) //只允许单机登陆；一旦登陆不允许其他人登陆（principle实体务必重写equals和hashCode）
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        // 过滤静态资源
        web.ignoring().antMatchers("/static/**");
    }


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    /**
     * 注入自定义按钮权限控制
     */
    @Bean
    public DefaultWebSecurityExpressionHandler userSecurityExpressionHandler() {
        // 设置freemarker标签路径
        freeMarkerConfigurer.getTaglibFactory().setClasspathTlds(Arrays.asList("security.tld"));

        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(new AccessPermissionEvaluator());
        return handler;
    }


    /**
     * 表DDL
     * create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);
     * create table authorities (username varchar(50) not null,authority varchar(50) not null,constraint fk_authorities_users foreign key(username) references users(username));
     * create unique index ix_auth_username on authorities (username,authority);
     * create table persistent_logins (username varchar(64) not null, series varchar(64) primary key,token varchar(64) not null, last_used timestamp not null)
     */
}
