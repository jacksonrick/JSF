package com.jsf.model;

import java.security.Principal;

/**
 * Created with IntelliJ IDEA.
 * Description: userinfo-principal端点自定义返回实体
 * User: xujunfei
 * Date: 2022-07-07
 * Time: 10:20
 */
public class OUserInfo implements Principal {

    private Integer id;
    private String username;
    private Boolean disabled;


    public OUserInfo(Integer id, String username, Boolean disabled) {
        this.id = id;
        this.username = username;
        this.disabled = disabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "OUserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", disabled=" + disabled +
                '}';
    }

    @Override
    public String getName() {
        return this.username;
    }
}
