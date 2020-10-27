package com.jsf.system.aspect;

import com.jsf.system.handler.ApiLocker;
import com.jsf.utils.annotation.ApiLock;
import com.jsf.utils.annotation.RepeatSubmit;
import com.jsf.utils.encrypt.SignUtil;
import com.jsf.utils.exception.ApiException;
import com.jsf.utils.string.StringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * Description: Api安全策略
 * <p>时间戳、签名、重复校验(需Redis)</p>
 * <p>对于IP限制和限流建议在反向代理或网关设置</p>
 * User: xujunfei
 * Date: 2018-05-24
 * Time: 15:07
 */
@Aspect
@Component
@Order(100)
public class AspectApiSafe {

    private final static Logger log = LoggerFactory.getLogger(AspectApiSafe.class);
    public final static int requestExpireSecond = 120; // 此处设置请求时间戳不能与服务器时间戳相差120秒

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;


    // 注意此处拦截的是指定包下的所有 RestController 类下的方法
    @Pointcut("execution(public * com.jsf.controller..*.*(..))&&@within(org.springframework.web.bind.annotation.RestController)")
    public void app() {
    }

    /**
     * @param point
     */
    @Before("app()")
    public void app(JoinPoint point) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 时间戳
        String timestamp = request.getHeader("timestamp");
        // 随机字符串
        String nonce = request.getHeader("nonce");
        // 签名，字段固定为sign，不建议修改
        String sign = request.getHeader("sign");
        if (StringUtil.isBlank(timestamp) || StringUtil.isBlank(nonce) || StringUtil.isBlank(sign)) {
            throw new ApiException("请求参数错误");
        }

        // 请求时间间隔
        long reqeustInterval = Math.abs(System.currentTimeMillis() / 1000 - Long.valueOf(timestamp));
        if (reqeustInterval > requestExpireSecond) {
            throw new ApiException("请求超时，请重试");
        }

        // 校验签名
        TreeMap<String, String> params = new TreeMap<>();
        params.put("timestamp", timestamp);
        request.getParameterMap().forEach((key, value) -> params.put(key, value[0]));
        String signStr = SignUtil.getSign(params, nonce);
        if (!signStr.equals(sign)) {
            throw new ApiException("请求签名错误");
        }

        // 重复请求校验
        MethodSignature signature = (MethodSignature) point.getSignature();
        RepeatSubmit repeatSubmitAnno = signature.getMethod().getAnnotation(RepeatSubmit.class);
        if (repeatSubmitAnno != null) {
            Boolean hasKey = stringRedisTemplate.hasKey(signStr);
            if (hasKey) {
                throw new ApiException("请勿重复提交");
            } else {
                // 如果不存在，保存到数据库，并设置超时时间
                stringRedisTemplate.opsForValue().set(signStr, timestamp, repeatSubmitAnno.timeout(), TimeUnit.SECONDS);
            }
        }

        // API锁
        ApiLock apiLockAnno = signature.getMethod().getAnnotation(ApiLock.class);
        if (apiLockAnno != null) {
            if (!ApiLocker.lock(apiLockAnno.name())) {
                throw new ApiException(apiLockAnno.desc());
            }
        }
    }

}
