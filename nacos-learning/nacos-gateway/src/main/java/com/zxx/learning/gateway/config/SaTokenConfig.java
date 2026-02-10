package com.zxx.learning.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.context.SaHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sa-Token相关配置
 * 配置全局认证过滤器和权限验证逻辑
 * 
 * @author zxx
 */
@Slf4j
@Configuration
public class SaTokenConfig {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    /**
     * 注册Sa-Token全局过滤器
     */
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")
                // 配置白名单路径
                .setExcludeList(ignoreUrlsConfig.getUrls())
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 对于OPTIONS预检请求直接放行
                    SaRouter.match(SaHttpMethod.OPTIONS).stop();
                    
                    // 登录认证：验证用户是否已登录
                    SaRouter.match("/**", r -> StpUtil.checkLogin());
                    
                    // 角色权限认证：根据路径匹配所需角色
                    String requestPath = SaHolder.getRequest().getRequestPath();
                    log.debug("请求路径: {}", requestPath);
                    
                    // 从配置或Redis获取路径-角色映射
                    Map<String, String> pathRoleMap = getPathRoleMap();
                    
                    // 查找当前路径所需的角色
                    org.springframework.util.AntPathMatcher pathMatcher = new org.springframework.util.AntPathMatcher();
                    List<String> requiredRoles = new ArrayList<>();
                    
                    for (Map.Entry<String, String> entry : pathRoleMap.entrySet()) {
                        if (pathMatcher.match(entry.getKey(), requestPath)) {
                            String roles = entry.getValue();
                            if (roles != null && !roles.isEmpty()) {
                                String[] roleArray = roles.split(",");
                                for (String role : roleArray) {
                                    requiredRoles.add(role.trim());
                                }
                            }
                        }
                    }
                    
                    // 如果路径需要角色权限，验证用户是否拥有相应角色
                    if (!requiredRoles.isEmpty()) {
                        boolean hasRole = false;
                        for (String role : requiredRoles) {
                            if (StpUtil.hasRole(role)) {
                                hasRole = true;
                                break;
                            }
                        }
                        if (!hasRole) {
                            throw new RuntimeException("无权限访问，需要角色: " + requiredRoles);
                        }
                    }
                })
                // 异常处理函数
                .setError(e -> {
                    log.error("Sa-Token认证失败", e);
                    return "认证失败: " + e.getMessage();
                });
    }

    /**
     * 获取路径-角色映射
     * 这里可以从配置文件或Redis中读取
     */
    private Map<String, String> getPathRoleMap() {
        // 这里简化处理，实际应该从配置文件中读取
        Map<String, String> pathRoleMap = new HashMap<>();
        pathRoleMap.put("/api/admin/**", "admin");
        pathRoleMap.put("/api/user/delete", "admin");
        pathRoleMap.put("/api/user/**", "user,admin");
        pathRoleMap.put("/api/order/**", "user,admin");
        return pathRoleMap;
    }
}
