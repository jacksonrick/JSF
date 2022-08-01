package com.jsf.model.enums;

/**
 * 授权范围
 */
public enum Scopes {

    ALL("all"),
    USERNAME("username"),
    FULLUSER("fulluser");

    final String code;

    Scopes(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
