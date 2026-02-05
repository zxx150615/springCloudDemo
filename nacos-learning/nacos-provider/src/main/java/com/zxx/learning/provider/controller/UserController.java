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
 * 用户控制器
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        log.info("根据ID获取用户，ID: {}", id);
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("username", "user" + id);
        user.put("email", "user" + id + "@example.com");
        user.put("age", 20 + (int)(id % 20));
        user.put("createTime", LocalDateTime.now());
        user.put("status", 1);
        user.put("remark", "Provider服务返回的用户数据");
        user.put("serverPort", serverPort);
        user.put("applicationName", applicationName);
        return user;
    }

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Map<String, Object> getUserList() {
        log.info("获取用户列表");
        Map<String, Object> result = new HashMap<>();
        result.put("total", 10);
        result.put("serverPort", serverPort);
        result.put("applicationName", applicationName);
        return result;
    }

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Map<String, Object> getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户，username: {}", username);
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("username", username);
        user.put("email", username + "@example.com");
        user.put("age", 25);
        user.put("createTime", LocalDateTime.now());
        user.put("status", 1);
        user.put("remark", "Provider服务返回的用户数据");
        user.put("serverPort", serverPort);
        user.put("applicationName", applicationName);
        return user;
    }
}
