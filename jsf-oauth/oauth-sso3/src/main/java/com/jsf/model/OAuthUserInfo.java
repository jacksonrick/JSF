package com.jsf.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.*;

/**
 * 单点登录用户信息
 */
public class OAuthUserInfo implements Serializable, UserDetails, OAuth2User {

    private static final long serialVersionUID = -5697270478132259883L;

    // 角色/权限集合
    private Set<GrantedAuthority> authorities;
    // oauth用户属性
    private Map<String, Object> attributes;

    // 用户信息
    private UserInfo userInfo;

    public OAuthUserInfo() {
    }

    /**
     * @param attributes
     * @param userInfo
     */
    public OAuthUserInfo(Map<String, Object> attributes, UserInfo userInfo) {
        // authorities是sso系统中存储的用户权限，这里也可以使用自定义角色管理【见getAuthorities()，从SysRole获取权限】
        // SCOPE_开头是系统授权范围，ROLE_开头是用户拥有的权限
        this.authorities = new LinkedHashSet<>();
        Object obj = attributes.get("authorities");
        if (obj != null) {
            ArrayList auths = (ArrayList) obj;
            for (Object auth : auths) {
                String authority = String.valueOf(((Map) auth).get("authority"));
                this.authorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.userInfo = userInfo;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String getName() {
        return this.userInfo.getUsername();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return this.userInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        // 账户是否过期，默认true
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 账户是否锁定，默认true
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 密码是否没有过期，默认true
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 账户是否可用，默认true
        return true;
    }

    // Spring Security 是使用org.springframework.security.core.userdetails.User类作为用户登录凭据( Principal )
    // 该类中重写了equals()和hashCode()，使用username属性作为唯一凭据。

    @Override
    public boolean equals(Object rhs) {
        return rhs instanceof OAuthUserInfo ? this.getUserInfo().getUsername().equals(((OAuthUserInfo) rhs).getUsername()) : false;
    }

    @Override
    public int hashCode() {
        return this.getUserInfo().getUsername().hashCode();
    }

    @Override
    public String toString() {
        return "OAuthUserInfo{" +
                "authorities=" + authorities +
                ", userInfo=" + userInfo +
                '}';
    }
}
