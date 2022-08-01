package com.jsf.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * Description: 数据库用户
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 15:36
 */
public class OAuthUser implements UserDetails {

    /**
     * 用户id
     * 这里使用整型自增
     */
    private Integer id;
    /**
     * 用户名，具有唯一性
     */
    private String username;
    /**
     * 密码（已加密）
     */
    private String password;
    /**
     * 自定义的角色名
     */
    private String roles;
    /**
     * 是否禁用
     */
    private Boolean disabled;
    /**
     * 锁定次数（超过5次无法登陆）
     */
    private Integer locks;

    public OAuthUser() {
    }

    public OUserInfo getUserInfo() {
        return new OUserInfo(this.id, this.username, this.disabled);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getLocks() {
        return locks;
    }

    public void setLocks(Integer locks) {
        this.locks = locks;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(this.roles);
    }
}
