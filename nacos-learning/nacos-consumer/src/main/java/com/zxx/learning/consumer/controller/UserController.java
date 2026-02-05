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
 * 用户控制器
 * 通过Feign调用Provider服务获取用户信息
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/consumer/user")
public class UserController {

    @Autowired
    private ProviderFeignClient providerFeignClient;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 根据ID获取用户（通过Feign调用Provider）
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        log.info("Consumer根据ID获取用户，ID: {}", id);
        
        // 这里应该调用Provider的/user/{id}接口，但为了简化，我们调用hello接口演示
        Map<String, Object> providerResponse = providerFeignClient.hello();
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "通过Consumer服务获取用户信息");
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("userId", id);
        result.put("providerResponse", providerResponse);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Map<String, Object> getUserList() {
        log.info("Consumer获取用户列表");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "通过Consumer服务获取用户列表");
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        log.info("Consumer根据用户名获取用户，username: {}", username);
        
        Map<String, Object> providerResponse = providerFeignClient.hello(username);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "通过Consumer服务获取用户信息");
        result.put("consumer", applicationName);
        result.put("consumerPort", serverPort);
        result.put("username", username);
        result.put("providerResponse", providerResponse);
        result.put("timestamp", LocalDateTime.now());
        
        return result;
    }
}
