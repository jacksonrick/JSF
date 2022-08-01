package com.jsf.config.handler;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 授权码模式code自定义生成
 * User: xujunfei
 * Date: 2022-07-11
 * Time: 11:40
 */
@Component
public class AuthCodeService implements AuthorizationCodeServices {

    protected final ConcurrentHashMap<String, OAuth2Authentication> authorizationCodeStore = new ConcurrentHashMap<String, OAuth2Authentication>();

    // 生成随机字符的类
    private RandomValueStringGenerator generator = new RandomValueStringGenerator(16);

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = this.generator.generate();
        this.authorizationCodeStore.put(code, authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication authentication = this.authorizationCodeStore.remove(code);
        return authentication;
    }
}
