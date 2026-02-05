package com.zxx.learning.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 请求验证过滤器工厂
 * 用于验证请求是否包含必要的请求头或参数
 * 
 * @author zxx
 */
@Slf4j
@Component
public class RequestValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestValidationGatewayFilterFactory.Config> {

    public RequestValidationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 如果启用了验证
            if (config.isEnabled()) {
                // 验证必要的请求头
                if (StringUtils.hasText(config.getRequiredHeader())) {
                    String headerValue = exchange.getRequest().getHeaders().getFirst(config.getRequiredHeader());
                    if (!StringUtils.hasText(headerValue)) {
                        log.warn("Missing required header: {}", config.getRequiredHeader());
                        return handleError(exchange, HttpStatus.BAD_REQUEST, 
                            "Missing required header: " + config.getRequiredHeader());
                    }
                }
                
                // 验证必要的查询参数
                if (StringUtils.hasText(config.getRequiredParam())) {
                    String paramValue = exchange.getRequest().getQueryParams().getFirst(config.getRequiredParam());
                    if (!StringUtils.hasText(paramValue)) {
                        log.warn("Missing required parameter: {}", config.getRequiredParam());
                        return handleError(exchange, HttpStatus.BAD_REQUEST, 
                            "Missing required parameter: " + config.getRequiredParam());
                    }
                }
            }
            
            return chain.filter(exchange);
        };
    }

    private Mono<Void> handleError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        
        String body = "{\"error\":\"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }

    @Data
    public static class Config {
        private boolean enabled = true;
        private String requiredHeader;
        private String requiredParam;
    }
}
