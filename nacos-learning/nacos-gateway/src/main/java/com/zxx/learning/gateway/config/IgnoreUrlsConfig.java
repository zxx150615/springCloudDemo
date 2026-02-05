package com.zxx.learning.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 忽略URL配置
 * 配置不需要认证的URL路径
 * 
 * @author zxx
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway.auth")
public class IgnoreUrlsConfig {
    
    /**
     * 白名单URL列表
     */
    private List<String> ignoreUrls = new ArrayList<>();
    
    /**
     * 获取忽略的URL列表
     */
    public List<String> getUrls() {
        return ignoreUrls;
    }
}
