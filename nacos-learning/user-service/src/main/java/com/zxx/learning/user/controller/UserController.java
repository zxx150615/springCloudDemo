package com.zxx.learning.user.controller;

import com.zxx.learning.common.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务控制器
 * API 路径: /user/**（Gateway 会通过 /api/user/** 访问）
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    // 模拟数据库，使用内存存储
    private final List<User> userDatabase = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 根据ID获取用户
     * GET /user/{id}
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("根据ID获取用户，ID: {}", id);
        
        // 从模拟数据库查询
        User user = userDatabase.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (user == null) {
            // 如果不存在，返回一个模拟用户
            user = new User();
            user.setId(id);
            user.setUsername("user" + id);
            user.setEmail("user" + id + "@example.com");
            user.setAge(20 + (int)(id % 20));
            user.setCreateTime(LocalDateTime.now());
            user.setStatus(1);
            user.setRemark("模拟用户数据");
        }
        
        log.info("返回用户: {}", user);
        return user;
    }

    /**
     * 获取用户列表
     * GET /user/list
     */
    @GetMapping("/list")
    public List<User> getUserList() {
        log.info("获取用户列表");
        
        // 如果数据库为空，初始化一些测试数据
        if (userDatabase.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                User user = new User();
                user.setId((long) i);
                user.setUsername("user" + i);
                user.setEmail("user" + i + "@example.com");
                user.setAge(20 + i);
                user.setCreateTime(LocalDateTime.now());
                user.setStatus(1);
                user.setRemark("测试用户" + i);
                userDatabase.add(user);
            }
        }
        
        return userDatabase;
    }

    /**
     * 创建用户
     * POST /user
     */
    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("创建用户: {}", user);
        user.setId(idGenerator.getAndIncrement());
        user.setCreateTime(LocalDateTime.now());
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        userDatabase.add(user);
        return user;
    }

    /**
     * 根据用户名获取用户
     * GET /user/username/{username}
     */
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户，username: {}", username);
        
        User user = userDatabase.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (user == null) {
            // 如果不存在，返回一个模拟用户
            user = new User();
            user.setId(1L);
            user.setUsername(username);
            user.setEmail(username + "@example.com");
            user.setAge(25);
            user.setCreateTime(LocalDateTime.now());
            user.setStatus(1);
            user.setRemark("模拟用户数据");
        }
        
        return user;
    }
}
