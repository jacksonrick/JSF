package com.jsf.config.handler;

import com.jsf.model.OUserDetail;
import com.jsf.service.OUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义用户信息转换器
 * User: xujunfei
 * Date: 2022-07-06
 * Time: 16:57
 */
@Component
public class OAuthUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

    @Autowired
    private OUserDetailService oUserDetailService;

    /*public OAuthUserAuthenticationConverter(OUserDetailService oUserDetailService) {
        setUserDetailsService(oUserDetailService);
    }*/

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(USERNAME, authentication.getName());
        Object principal = authentication.getPrincipal();
        if (principal instanceof OUserDetail) {
            OUserDetail user = (OUserDetail) principal;
            response.put("user_id", user.getId());
        }
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            OUserDetail user = (OUserDetail) oUserDetailService.loadUserByUsername((String) map.get(USERNAME));
            Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
            authorities = user.getAuthorities();
            return new UsernamePasswordAuthenticationToken(user.getUserInfo(), "N/A", authorities);
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        if (!map.containsKey(AUTHORITIES)) {
            return AuthorityUtils.NO_AUTHORITIES;
        }
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }
}
