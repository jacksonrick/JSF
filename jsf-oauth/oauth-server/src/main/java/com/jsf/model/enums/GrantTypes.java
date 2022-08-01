package com.jsf.model.enums;

/**
 * 授权模式
 */
public enum GrantTypes {

    AUTHORIZATION_CODE("authorization_code", "授权码模式"),
    CLIENT_CREDENTIALS("client_credentials", "客户端模式"),
    PASSWORD("password", "密码模式"),
    REFRESH_TOKEN("refresh_token", "刷新TOKEN");

    final String code;
    final String desc;

    GrantTypes(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String code() {
        return code;
    }

    public String desc() {
        return desc;
    }
}
