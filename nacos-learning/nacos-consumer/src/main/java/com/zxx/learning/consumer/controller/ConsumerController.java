package com.zxx.learning.consumer.controller;

import com.zxx.learning.consumer.feign.ProviderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费者控制器
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired
    private ProviderFeignClient providerFeignClient;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 通过Feign调用Provider服务
     */
    @GetMapping("/hello")
    public Map<String, Object> callProvider() {
        log.info("Consumer调用Provider服务");
        Map<String, Object> providerResponse = providerFeignClient.hello();
        
        Map<String, Object> result = new HashMap<>();
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("providerResponse", providerResponse);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * 带参数调用Provider服务
     */
    @GetMapping("/hello/{name}")
    public Map<String, Object> callProvider(@PathVariable String name) {
        log.info("Consumer调用Provider服务，参数：{}", name);
        Map<String, Object> providerResponse = providerFeignClient.hello(name);
        
        Map<String, Object> result = new HashMap<>();
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("providerResponse", providerResponse);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * 获取Provider服务信息
     */
    @GetMapping("/info")
    public Map<String, Object> getProviderInfo() {
        log.info("Consumer获取Provider服务信息");
        Map<String, Object> providerInfo = providerFeignClient.info();
        
        Map<String, Object> result = new HashMap<>();
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("providerInfo", providerInfo);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * Consumer自身信息
     */
    @GetMapping("/self")
    public Map<String, Object> self() {
        Map<String, Object> result = new HashMap<>();
        result.put("application", applicationName);
        result.put("port", serverPort);
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        return result;
    }
}
