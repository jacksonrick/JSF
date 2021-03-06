package com.jsf.system.aspect;

import com.jsf.system.handler.TokenHandler;
import com.jsf.database.enums.ResCode;
import com.jsf.system.conf.IConstant;
import com.jsf.utils.annotation.Token;
import com.jsf.utils.exception.ApiTokenException;
import com.jsf.utils.string.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * Description: 前置处理器，用户Token，仅拦截接口方法上带有Token注解
 * User: xujunfei
 * Date: 2018-05-24
 * Time: 15:07
 */
@Aspect
@Component
@Order(101)
public class AspectToken {

    private final static Logger log = LoggerFactory.getLogger(AspectToken.class);

    @Autowired(required = false)
    private RedisTemplate redisTemplate;
    @Autowired
    private TokenHandler tokenHandler;

    @Pointcut("@annotation(com.jsf.utils.annotation.Token)")
    public void token() {
    }

    /**
     * 将token转换为userId
     * 注意：注解方法中，第一个参数必须要是Long param_name
     *
     * @param pjp
     * @param tk
     */
    @Around("token()&&@annotation(tk)")
    public Object token(ProceedingJoinPoint pjp, Token tk) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object[] args = pjp.getArgs();
        if (args.length < 1) {
            throw new ApiTokenException("必须指定一个参数");
        }

        String name = tk.name(); // 键名
        String type = tk.type(); // 类型 header param cookie
        boolean need = tk.need(); // 是否必须 默认：true
        boolean cache = tk.useCache(); // 是否使用缓存 默认：true

        String token = "";
        if (IConstant.TOKEN_HEADER.equals(type)) {
            token = request.getHeader(name);
        } else if (IConstant.TOKEN_PARAM.equals(type)) {
            token = request.getParameter(name);
        } else if (IConstant.TOKEN_COOKIE.equals(type)) {
            Cookie cookieValue = WebUtils.getCookie(request, name);
            if (cookieValue != null) {
                token = cookieValue.getValue();
            }
        }
        log.info("api token: " + token);

        if (!need) { //非必须
            if (StringUtil.isBlank(token)) {
                return pjp.proceed();
            } else {
                if (cache) { //从缓存取
                    Long uid = (Long) redisTemplate.opsForValue().get(token);
                    if (uid != null) {
                        args[0] = tokenHandler.getIdByTokenFromRedis(token);
                        return pjp.proceed(args);
                    } else {
                        return pjp.proceed();
                    }
                } else {
                    args[0] = tokenHandler.getIdByTokenFromDb(token);
                    return pjp.proceed(args);
                }
            }
        } else { //必须
            if (StringUtil.isBlank(token)) {
                throw new ApiTokenException(ResCode.TOKEN_EXP.msg());
            } else {
                if (cache) {
                    args[0] = tokenHandler.getIdByTokenFromRedis(token);
                } else {
                    args[0] = tokenHandler.getIdByTokenFromDb(token);
                }
                return pjp.proceed(args);
            }
        }
    }

}
