package com.zxx.learning.gateway.config;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Sa-Token 权限与角色获取实现类。
 *
 * <p>这里仅实现角色查询逻辑，权限列表返回空集合即可。</p>
 *
 * Sa-Token 会自动从 Spring 容器中查找 {@link StpInterface} 实现，
 * 并通过 {@link #getRoleList(Object, String)} 获取当前登录用户的角色列表。
 *
 * 角色数据来源：Redis（与 auth-service 共享，key: sa:roles:{loginId}）
 *
 * @author zxx
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SaTokenStpInterfaceImpl implements StpInterface {

    private static final String KEY_PREFIX = "sa:roles:";

    private final StringRedisTemplate redisTemplate;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本示例不做权限点控制，返回空集合即可
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        String loginIdStr = loginId.toString();
        if (!StringUtils.hasText(loginIdStr)) {
            return Collections.emptyList();
        }
        
        String key = KEY_PREFIX + loginIdStr.trim();
        try {
            Set<String> roles = redisTemplate.opsForSet().members(key);
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            return new java.util.ArrayList<>(roles);
        } catch (Exception e) {
            log.error("从 Redis 读取角色异常, loginId={}", loginIdStr, e);
            return Collections.emptyList();
        }
    }
}

