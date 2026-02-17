package com.zxx.learning.gateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一使用 {success, msg, status, timestamp} 格式
 * 
 * @author zxx
 */
@Slf4j
@Order(-1)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 构建错误响应（统一格式：{success, msg, status, timestamp}）
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        String errorMsg;
        HttpStatus httpStatus;
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException statusException = (ResponseStatusException) ex;
            httpStatus = statusException.getStatus();
            errorMsg = statusException.getReason() != null ? statusException.getReason() : httpStatus.getReasonPhrase();
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorMsg = ex.getMessage() != null ? ex.getMessage() : "服务器内部错误";
        }
        
        response.setStatusCode(httpStatus);
        errorResponse.put("status", httpStatus.value());
        errorResponse.put("msg", errorMsg);
        
        log.error("Gateway异常处理: {}", errorMsg, ex);
        
        // 使用 ObjectMapper 序列化 JSON，确保安全转义
        try {
            String json = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("序列化错误响应失败", e);
            // 降级处理：使用简单的错误响应
            String fallbackJson = "{\"success\":false,\"msg\":\"服务器内部错误\",\"status\":500,\"timestamp\":" + System.currentTimeMillis() + "}";
            DataBuffer buffer = response.bufferFactory().wrap(fallbackJson.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        }
    }
}
