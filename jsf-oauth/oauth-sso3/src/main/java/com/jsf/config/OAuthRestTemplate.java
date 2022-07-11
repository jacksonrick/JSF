package com.jsf.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA.
 * Description: OAuth RestTemplate
 * User: xujunfei
 * Date: 2022-07-05
 * Time: 15:57
 */
@Component
public class OAuthRestTemplate {

    @Autowired
    OAuth2AuthorizedClientManager authorizedClientManager;

    /**
     * OAuth2 restTemplate
     *
     * @return
     */
    @Bean(name = "oauthRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate rest = new RestTemplate();
        rest.getInterceptors().add((request, body, execution) -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId("rest") // 注册ID，授权模式必须为client_credentials
                    .principal("client")
                    .build();
            OAuth2AuthorizedClient authorize = authorizedClientManager.authorize(authorizeRequest);
            OAuth2AccessToken accessToken = authorize.getAccessToken();
            // 设置请求头认证信息
            request.getHeaders().setBearerAuth(accessToken.getTokenValue());
            return execution.execute(request, body);
        });
        return rest;
    }

}
