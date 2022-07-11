package org.springframework.security.web.authentication;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 重写Spring Security的退出逻辑，在跳转页加上自定义参数，如不需要请删除此类
 */
public abstract class AbstractAuthenticationTargetUrlRequestHandler {

    protected final Log logger = LogFactory.getLog(this.getClass());
    private String targetUrlParameter = null;
    private String defaultTargetUrl = "/";
    private boolean alwaysUseDefaultTargetUrl = false;
    private boolean useReferer = false;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    protected AbstractAuthenticationTargetUrlRequestHandler() {
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response,
                          Authentication authentication) throws IOException {
        // 此处改写
        String param = "";
        if (authentication != null) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            if (!authorities.isEmpty()) {
                for (GrantedAuthority authority : authorities) {
                    param = authority.getAuthority();
                    break;
                }
            }
        }

        String targetUrl = determineTargetUrl(request, response, authentication) + "?sys=" + param;
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication) {
        return determineTargetUrl(request, response);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response) {
        if (isAlwaysUseDefaultTargetUrl()) {
            return defaultTargetUrl;
        }

        // Check for the parameter and use that if available
        String targetUrl = null;

        if (targetUrlParameter != null) {
            targetUrl = request.getParameter(targetUrlParameter);

            if (StringUtils.hasText(targetUrl)) {
                logger.debug("Found targetUrlParameter in request: " + targetUrl);

                return targetUrl;
            }
        }

        if (useReferer && !StringUtils.hasLength(targetUrl)) {
            targetUrl = request.getHeader("Referer");
            logger.debug("Using Referer header: " + targetUrl);
        }

        if (!StringUtils.hasText(targetUrl)) {
            targetUrl = defaultTargetUrl;
            logger.debug("Using default Url: " + targetUrl);
        }

        return targetUrl;
    }

    protected final String getDefaultTargetUrl() {
        return defaultTargetUrl;
    }

    public void setDefaultTargetUrl(String defaultTargetUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultTargetUrl),
                "defaultTarget must start with '/' or with 'http(s)'");
        this.defaultTargetUrl = defaultTargetUrl;
    }

    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
        this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
    }

    protected boolean isAlwaysUseDefaultTargetUrl() {
        return alwaysUseDefaultTargetUrl;
    }

    public void setTargetUrlParameter(String targetUrlParameter) {
        if (targetUrlParameter != null) {
            Assert.hasText(targetUrlParameter, "targetUrlParameter cannot be empty");
        }
        this.targetUrlParameter = targetUrlParameter;
    }

    protected String getTargetUrlParameter() {
        return targetUrlParameter;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public void setUseReferer(boolean useReferer) {
        this.useReferer = useReferer;
    }

}