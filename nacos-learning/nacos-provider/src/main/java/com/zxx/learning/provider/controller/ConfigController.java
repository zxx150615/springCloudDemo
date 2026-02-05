package com.zxx.learning.provider.controller;

import com.zxx.learning.provider.config.AppConfig;
import com.zxx.learning.provider.config.CustomConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置控制器
 * 用于测试从Nacos配置中心读取配置
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private CustomConfig customConfig;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 获取应用配置
     */
    @GetMapping("/app")
    public Map<String, Object> getAppConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("applicationName", applicationName);
        if (appConfig != null) {
            result.put("appName", appConfig.getName());
            result.put("appVersion", appConfig.getVersion());
            result.put("appDescription", appConfig.getDescription());
        }
        return result;
    }

    /**
     * 获取自定义配置
     */
    @GetMapping("/custom")
    public Map<String, Object> getCustomConfig() {
        Map<String, Object> result = new HashMap<>();
        if (customConfig != null) {
            result.put("message", customConfig.getMessage());
            result.put("timeout", customConfig.getTimeout());
            result.put("enabled", customConfig.getEnabled());
        }
        return result;
    }

    /**
     * 获取所有配置
     */
    @GetMapping("/all")
    public Map<String, Object> getAllConfig() {
        Map<String, Object> result = new HashMap<>();
        result.put("applicationName", applicationName);
        
        Map<String, Object> app = new HashMap<>();
        if (appConfig != null) {
            app.put("name", appConfig.getName());
            app.put("version", appConfig.getVersion());
            app.put("description", appConfig.getDescription());
        }
        result.put("app", app);
        
        Map<String, Object> custom = new HashMap<>();
        if (customConfig != null) {
            custom.put("message", customConfig.getMessage());
            custom.put("timeout", customConfig.getTimeout());
            custom.put("enabled", customConfig.getEnabled());
        }
        result.put("custom", custom);
        
        return result;
    }
}
