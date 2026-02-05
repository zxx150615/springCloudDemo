package com.zxx.learning.gateway.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * 请求日志过滤器工厂
 * 用于记录所有经过Gateway的请求日志
 * 
 * @author zxx
 */
@Slf4j
@Component
public class RequestLoggingGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestLoggingGatewayFilterFactory.Config> {

    public RequestLoggingGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            String level = config.getLevel() != null ? config.getLevel().toUpperCase() : "INFO";
            
            if ("DEBUG".equals(level)) {
                log.debug("Gateway Request - Method: {}, URI: {}, Headers: {}, Time: {}", 
                    request.getMethod(), request.getURI(), request.getHeaders(), LocalDateTime.now());
            } else if ("INFO".equals(level)) {
                log.info("Gateway Request - Method: {}, URI: {}, Time: {}", 
                    request.getMethod(), request.getURI(), LocalDateTime.now());
            } else if ("WARN".equals(level)) {
                log.warn("Gateway Request - Method: {}, URI: {}, Time: {}", 
                    request.getMethod(), request.getURI(), LocalDateTime.now());
            }
            
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if ("DEBUG".equals(level)) {
                    log.debug("Gateway Response - Status: {}, Time: {}", 
                        exchange.getResponse().getStatusCode(), LocalDateTime.now());
                }
            }));
        };
    }

    @Data
    public static class Config {
        private String level = "INFO";
    }
}
