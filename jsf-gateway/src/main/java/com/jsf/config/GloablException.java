package com.jsf.config;

import com.jsf.model.ResMsg;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description: 自定义异常
 * User: xujunfei
 * Date: 2021-02-03
 * Time: 11:21
 */
@Configuration
@EnableConfigurationProperties({ServerProperties.class, ResourceProperties.class})
public class GloablException {

    private ServerProperties serverProperties;
    private ApplicationContext applicationContext;
    private ResourceProperties resourceProperties;
    private List<ViewResolver> viewResolvers;
    private ServerCodecConfigurer serverCodecConfigurer;

    public GloablException(ServerProperties serverProperties,
                           ResourceProperties resourceProperties,
                           ObjectProvider<List<ViewResolver>> viewResolversProvider,
                           ServerCodecConfigurer serverCodecConfigurer,
                           ApplicationContext applicationContext) {
        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.getIfAvailable(() -> Collections.emptyList());
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * 注册自定义的ErrorWebExceptionHandler
     *
     * @param errorAttributes
     * @return
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        DefaultErrorWebExceptionHandler exceptionHandler = new CustomErrorWebExceptionHandler(errorAttributes,
                this.resourceProperties, this.serverProperties.getError(), this.applicationContext);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

    /**
     * 自定义异常处理器
     * 响应JSON格式的信息，可根据不同的异常类型返回不同的状态和消息
     */
    static class CustomErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

        public CustomErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
            super(errorAttributes, resourceProperties, errorProperties, applicationContext);
        }

        @Override
        protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
            return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
        }

        @Override
        protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
            // 封装异常属性
            Map<String, Object> error = getErrorAttributes(request, isIncludeStackTrace(request, MediaType.ALL));
            // 获取request的异常类型，根据不同异常类型进行相应处理
            Throwable throwable = getError(request);
            return ServerResponse.status(getHttpStatus(error))
                    .contentType(MediaType.APPLICATION_JSON)
                    // 自定义响应内容
                    .body(BodyInserters.fromObject(ResMsg.fail(10000, "网关异常：" + throwable.getMessage())));
            //.doOnNext((resp) -> logError(request, errorStatus));
        }

    }
}
