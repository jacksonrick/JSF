package com.jsf.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.util.function.Supplier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description: 限流熔断异常配置
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 10:37
 */
@Configuration
public class GatewayBlockException {

    private final List<ViewResolver> viewResolvers;
    private final ServerCodecConfigurer serverCodecConfigurer;

    public GatewayBlockException(ObjectProvider<List<ViewResolver>> viewResolversProvider, ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList);
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // 自定义异常
        return new CustomBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    static class CustomBlockExceptionHandler extends SentinelGatewayBlockExceptionHandler {
        private List<ViewResolver> viewResolvers;
        private List<HttpMessageWriter<?>> messageWriters;

        public CustomBlockExceptionHandler(List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
            super(viewResolvers, serverCodecConfigurer);
            this.viewResolvers = viewResolvers;
            this.messageWriters = serverCodecConfigurer.getWriters();
        }

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
            if (exchange.getResponse().isCommitted()) {
                return Mono.error(throwable);
            }
            if (!BlockException.isBlockException(throwable)) {
                return Mono.error(throwable);
            }
            // System.out.println("throwable===" + throwable.getClass().getSimpleName());
            return handleBlockedRequest(exchange, throwable)
                    .flatMap(response -> writeResponse(response, exchange, throwable));
        }

        private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
            return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
        }

        private final Supplier<ServerResponse.Context> contextSupplier = () -> new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return CustomBlockExceptionHandler.this.messageWriters;
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return CustomBlockExceptionHandler.this.viewResolvers;
            }
        };

        private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange, Throwable throwable) {
            ServerHttpResponse resp = exchange.getResponse();
            resp.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            resp.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            String json = "{\"code\": 9999, \"msg\": \"Blocked\"}";
            if (throwable instanceof FlowException) {
                json = "{\"code\": 9999, \"msg\": \"触发限流\"}";
            } else if (throwable instanceof DegradeException) {
                json = "{\"code\": 9999, \"msg\": \"触发熔断\"}";
            }
            DataBuffer buffer = resp.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return resp.writeWith(Mono.just(buffer));
        }

    }

}
