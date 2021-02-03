package com.jsf.config;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 全局日志捕获
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 14:18
 */
@Configuration
public class GlobalLogFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(GlobalLogFilter.class);
    private static final String REQUEST_LINE = "\n-----------------------------------------------------------------------------";

    /**
     * 打印请求日志
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        StringBuilder req = new StringBuilder();

        ServerHttpRequest request = exchange.getRequest();
        InetSocketAddress address = request.getRemoteAddress();
        String method = request.getMethodValue();
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();

        // 获取请求参数
        Map queryMap = request.getQueryParams();
        String query = JSON.toJSONString(queryMap);
        // body暂不记录

        // 拼接请求日志
        req.append(REQUEST_LINE);
        req.append("\n uri=").append(uri.getPath());
        req.append("\n method=").append(method);
        req.append("\n header=").append(headers);
        req.append("\n query=").append(query);
        req.append("\n address=").append(address.getAddress().getHostAddress());
        req.append(REQUEST_LINE);
        log.info(req.toString());

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // 值越小，越优先
        return 2;
    }
}
