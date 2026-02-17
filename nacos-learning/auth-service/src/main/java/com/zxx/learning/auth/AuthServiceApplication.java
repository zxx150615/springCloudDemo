package com.zxx.learning.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Auth Service 启动类
 * 
 * 认证服务，负责用户登录、注册、技能管理等认证相关功能。
 * 使用 Spring MVC（非 WebFlux），通过 OpenFeign 调用 user-service。
 * 
 * @author zxx
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
