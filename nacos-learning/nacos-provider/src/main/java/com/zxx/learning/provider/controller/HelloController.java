package com.zxx.learning.provider.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello服务控制器
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/hello")
public class HelloController {

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 简单的Hello接口
     */
    @GetMapping
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello from Nacos Provider!");
        result.put("application", applicationName);
        result.put("port", serverPort);
        result.put("timestamp", LocalDateTime.now());
        
        log.info("Hello接口被调用，端口：{}", serverPort);
        return result;
    }

    /**
     * 带参数的Hello接口
     */
    @GetMapping("/{name}")
    public Map<String, Object> hello(@PathVariable String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello, " + name + "!");
        result.put("application", applicationName);
        result.put("port", serverPort);
        result.put("timestamp", LocalDateTime.now());
        
        log.info("Hello接口被调用，参数：{}，端口：{}", name, serverPort);
        return result;
    }

    /**
     * 获取服务信息
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("application", applicationName);
        result.put("port", serverPort);
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
