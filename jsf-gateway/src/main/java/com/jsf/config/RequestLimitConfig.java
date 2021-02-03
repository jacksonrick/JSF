package com.jsf.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Created with IntelliJ IDEA.
 * Description: 请求限流，定义不同的KeyResolver
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 10:37
 */
@Component
public class RequestLimitConfig {

    /**
     * 根据固定属性限流
     *
     * @return
     */
    @Bean(name = "myKeyResolver")
    public KeyResolver myKeyResolver() {
        // 1.这里根据商品ID限流，请求参数中必须包含 productId
        // return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("productId"));
        // 2.根据IP地址
        // return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        // 3.根据请求路径
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }

}
