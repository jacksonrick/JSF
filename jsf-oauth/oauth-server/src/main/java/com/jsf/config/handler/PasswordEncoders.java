package com.jsf.config.handler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description: 用户密码加密器
 * User: xujunfei
 * Date: 2022-07-07
 * Time: 15:25
 */
public class PasswordEncoders {

    static Map<String, PasswordEncoder> ENCODERS = new ConcurrentHashMap<>();
    static PasswordEncoder DEFAULT_ENCODER = new DefaultPasswordEncoder();

    static {
        ENCODERS.put("md5", new Md5PasswordEncoder());
        ENCODERS.put("bc", new BCryptPasswordEncoder());
    }

    /**
     * 加密器名称
     *
     * @param name
     * @return
     */
    public static PasswordEncoder getEncoder(String name) {
        if (!StringUtils.hasText(name)) {
            return DEFAULT_ENCODER;
        }
        PasswordEncoder encoder = ENCODERS.get(name);
        if (encoder == null) {
            return DEFAULT_ENCODER;
        }
        return encoder;
    }

    // 默认加密器，直接匹配
    static class DefaultPasswordEncoder implements PasswordEncoder {

        @Override
        public String encode(CharSequence charSequence) {
            return charSequence.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    }

}
