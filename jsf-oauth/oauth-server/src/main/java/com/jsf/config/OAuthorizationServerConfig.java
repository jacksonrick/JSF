package com.jsf.config;

import com.jsf.config.exception.OAuthServerExceptionTranslator;
import com.jsf.config.handler.OAuthUserAuthenticationConverter;
import com.jsf.service.OUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证服务器配置
 * endpoint: spring-security-oauth2-2.3.7.RELEASE.jar!/org/springframework/security/oauth2/provider/endpoint
 */
@Configuration
@EnableAuthorizationServer
public class OAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private OUserDetailService oUserDetailService;
    @Autowired
    private OAuthServerExceptionTranslator oAuthServerExceptionTranslator;
    @Autowired
    private OAuthUserAuthenticationConverter oAuthUserAuthenticationConverter;


    /*
     * 注意问题
     * 1.数据库的authorized_grant_types不能设置全部，可组合，如client_credentials,authorization_code
     * 2.client_credentials没有refresh_token，故设置无效
     * 3.同域名/IP下，每个sso客户端的Cookie名不能相同
     */

    /**
     * 客户端配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     * TokenStore
     * <p>两种存储方式</p>
     * <p>1. JDBC</p>
     * <p>2. JWT（Header、Payload、Signature）</p>
     *
     * @return
     */
    @Bean
    public TokenStore tokenStore() {
        //return new JwtTokenStore(jwtAccessTokenConverter());
        return new JdbcTokenStore(dataSource);
    }

    /**
     * 配置端点
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain enhancers = new TokenEnhancerChain();
        enhancers.setTokenEnhancers(Arrays.asList(tokenEnhancer(), jwtAccessTokenConverter()));
        endpoints
                .tokenStore(tokenStore())
                .userDetailsService(oUserDetailService)
                .authenticationManager(authenticationManager)
                .tokenEnhancer(enhancers)
                .accessTokenConverter(jwtAccessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .exceptionTranslator(oAuthServerExceptionTranslator)
        ;
    }

    /**
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .allowFormAuthenticationForClients() // 允许client使用form的方式进行authentication的授权
                //.tokenKeyAccess("isAuthenticated()") // /oauth/token_key
                .checkTokenAccess("permitAll()") // /oauth/check_token
        ;
    }

    /**
     * 生成JTW token
     *
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // converter.setSigningKey("MIEQIBADAN");
        // 更改Key后务必删除oauth_access_token记录，重启应用

        // 设置公私钥
        //converter.setVerifierKey("");
        //converter.setVerifier(new RsaVerifier(""));

        // 生成jks文件 keytool -genkey -alias jwt -keyalg RSA -keysize 1024 -keystore jwt.jks -validity 365
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray())
                .getKeyPair("jwt", "123456".toCharArray());
        converter.setKeyPair(keyPair);

        // userinfo接口自定义字段(additionalInformation同tokenEnhancer + principal自定义)
        //DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
        //tokenConverter.setUserTokenConverter(oAuthUserAuthenticationConverter);
        //converter.setAccessTokenConverter(tokenConverter);
        return converter;
    }

    /**
     * Token增强
     *
     * @return
     */
    @Bean
    public TokenEnhancer tokenEnhancer() {
        return (accessToken, authentication) -> {
            final Map<String, Object> additionalInfo = new HashMap<>(1);
            additionalInfo.put("license", "made by jackson");
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
            return accessToken;
        };
    }

    /**
     * 默认密码加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
