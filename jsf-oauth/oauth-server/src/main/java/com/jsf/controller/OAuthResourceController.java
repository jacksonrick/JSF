package com.jsf.controller;

import com.jsf.config.OAuthResourceServerConfig;
import com.jsf.model.OAuthUser;
import com.jsf.model.ResMsg;
import com.jsf.service.OUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * 资源服务接口
 *
 * {@link OAuthResourceServerConfig}
 *
 * @author jackson-rick
 */
@RestController
@RequestMapping("/api")
public class OAuthResourceController {

    @Autowired
    private OUserService oUserService;
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 同步用户
     *
     * @param username 用户名
     * @param password 密码（加密）
     * @param roles    角色ID（多个使用英文逗号隔开）
     * @param disabled 账号是否禁用
     * @return
     */
    @PostMapping("/userSync")
    public ResMsg userSync(OAuth2Authentication authentication, String username, String password, String roles, Boolean disabled) {
        String clientId = authentication.getOAuth2Request().getClientId();
        if (!StringUtils.hasText(username)) {
            return ResMsg.fail("名称不能为空");
        }
        try {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        } catch (Exception e) {
            return ResMsg.fail("客户端未接入");
        }
        OAuthUser user = oUserService.findByUsername(username);
        if (user == null) {
            if (!StringUtils.hasText(password)) {
                return ResMsg.fail("密码不能为空");
            }
            oUserService.addUser(username, password, roles, clientId);
        } else {
            oUserService.updateUser(username, password, roles, disabled);
        }
        return ResMsg.successdata("同步成功", username);
    }

    /**
     * 用户信息
     *
     * @param user
     * @return
     */
    @RequestMapping("/user")
    public Principal user(Principal principal) {
        //OAuth2Authentication
        //if (principal instanceof OAuth2Authentication) {
        //    OAuth2Authentication authentication = (OAuth2Authentication) principal;
        //    if (authentication.getUserAuthentication() == null) {
        //        return principal;
        //    }
        //    return authentication.getUserAuthentication();
        //}
        //return principal;
        return principal;
    }

    @Autowired
    private JwtAccessTokenConverter converter;

    /**
     * 获取token_key(JWT)
     *
     * @param principal
     * @return
     */
    @RequestMapping("/tokenKey")
    public Map<String, String> tokenKey() {
        Map<String, String> result = converter.getKey();
        return result;
    }

    // form action "/oauth/authorize"
    // org.springframework.security.oauth2.provider.endpoint.WhitelabelApprovalEndpoint
    /*@RequestMapping("/oauth/confirm_access")
    public String getAccessConfirmation(HttpServletRequest request) throws Exception {

        return "/user/oauth_approval";
    }*/

}
