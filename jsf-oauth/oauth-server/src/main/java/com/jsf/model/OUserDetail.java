package com.jsf.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2018-10-31
 * Time: 15:37
 */
public class OUserDetail extends OAuthUser implements UserDetails {

    public OUserDetail(OAuthUser user) {
        super(user);
    }

    public OUserInfo getUserInfo() {
        return new OUserInfo(super.getId(), super.getUsername(), super.getDisabled());
    }

    @Override
    public String getPassword() {
        return super.getPassword();
    }

    @Override
    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(super.getRoles());
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

    // 限制用户重复登录
    // 重写equals()和hashCode()，使用username属性作为唯一凭据
    /*@Override
    public boolean equals(Object obj) {
        if (obj instanceof UserDetail) {
            return getUsername().equals(((UserDetail) obj).getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }*/
}
