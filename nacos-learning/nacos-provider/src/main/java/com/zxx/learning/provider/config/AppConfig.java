package com.zxx.learning.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 应用配置类
 * 从Nacos配置中心读取 app.* 配置项
 * 
 * @author zxx
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    /**
     * 应用名称
     */
    private String name;
    
    /**
     * 应用版本
     */
    private String version;
    
    /**
     * 应用描述
     */
    private String description;
}
