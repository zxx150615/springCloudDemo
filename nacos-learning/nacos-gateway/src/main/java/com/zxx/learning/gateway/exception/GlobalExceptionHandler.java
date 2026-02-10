package com.zxx.learning.gateway.exception;

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
 * 
 * @author zxx
 */
@Slf4j
@Order(-1)
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        
        // 设置响应头
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        // 构建错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        if (ex instanceof ResponseStatusException) {
            ResponseStatusException statusException = (ResponseStatusException) ex;
            HttpStatus status = statusException.getStatus();
            response.setStatusCode(status);
            errorResponse.put("status", status.value());
            errorResponse.put("error", status.getReasonPhrase());
            errorResponse.put("message", statusException.getReason() != null ? statusException.getReason() : status.getReasonPhrase());
        } else {
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : "服务器内部错误");
        }
        
        log.error("Gateway异常处理", ex);
        
        // 将错误响应写入响应体
        String json = "{\"success\":false,\"status\":" + errorResponse.get("status") + 
                      ",\"error\":\"" + errorResponse.get("error") + 
                      "\",\"message\":\"" + errorResponse.get("message") + 
                      "\",\"timestamp\":" + errorResponse.get("timestamp") + "}";
        
            DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
    }
}
