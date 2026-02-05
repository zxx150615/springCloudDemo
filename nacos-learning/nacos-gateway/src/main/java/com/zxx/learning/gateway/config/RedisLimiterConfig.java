package com.zxx.learning.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * Redis限流配置
 * 
 * @author zxx
 */
@Slf4j
@Configuration
public class RedisLimiterConfig {

    /**
     * 基于IP的限流键解析器
     * 根据请求的IP地址进行限流
     * 支持代理场景：优先从 X-Forwarded-For 或 X-Real-IP 获取真实IP
     */
    @Bean
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            ServerHttpRequest request = exchange.getRequest();
            String ip = getClientIp(request);
            log.debug("Rate limit key (IP): {}", ip);
            return Mono.just(ip);
        };
    }

    /**
     * 基于用户ID的限流键解析器（可选）
     * 从请求头或参数中获取用户ID进行限流
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 优先从请求头获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (!StringUtils.hasText(userId)) {
                // 如果请求头没有，从查询参数获取
                userId = exchange.getRequest().getQueryParams().getFirst("userId");
            }
            if (!StringUtils.hasText(userId)) {
                userId = "anonymous";
            }
            log.debug("Rate limit key (User): {}", userId);
            return Mono.just(userId);
        };
    }

    /**
     * 获取客户端真实IP地址
     * 支持代理场景，按优先级获取：
     * 1. X-Forwarded-For 请求头（可能包含多个IP，取第一个）
     * 2. X-Real-IP 请求头
     * 3. RemoteAddress
     * 
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(ServerHttpRequest request) {
        String ip = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For 可能包含多个IP，取第一个
            int index = ip.indexOf(',');
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            return ip.trim();
        }
        
        ip = request.getHeaders().getFirst("X-Real-IP");
        if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }
}
