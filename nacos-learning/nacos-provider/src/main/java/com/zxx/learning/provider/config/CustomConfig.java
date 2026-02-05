package com.zxx.learning.provider.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 自定义配置类
 * 从Nacos配置中心读取 custom.* 配置项
 * 
 * @author zxx
 */
@Data
@Component
@RefreshScope
@ConfigurationProperties(prefix = "custom")
public class CustomConfig {
    
    /**
     * 自定义消息
     */
    private String message;
    
    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
}
