package com.jsf.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义用户
 * User: xujunfei
 * Date: 2022-08-09
 * Time: 11:01
 */
public class UserInfo implements UserDetails {

    private String name;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserInfo() {
    }

    public UserInfo(String name, String password) {
        this.name = name;
        this.password = password;
        this.authorities = Collections.EMPTY_LIST;
    }

    public UserInfo(String name, String password, List<GrantedAuthority> authorities) {
        this.name = name;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", authorities=" + authorities +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UserInfo ? this.name.equals(((UserInfo) obj).getUsername()) : false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
