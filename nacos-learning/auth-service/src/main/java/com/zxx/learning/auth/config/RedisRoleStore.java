package com.zxx.learning.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 使用 Redis 存储用户角色的实现（基于 StringRedisTemplate，同步版本）。
 *
 * <p><b>技术栈声明</b>：本项目统一使用 StringRedisTemplate（同步版本），不使用 ReactiveStringRedisTemplate。</p>
 *
 * <p>说明：
 * <ul>
 *     <li>key 结构：sa:roles:{loginId}</li>
 *     <li>value：Set<String>，例如 user、admin</li>
 *     <li>使用同步的 StringRedisTemplate，因为 StpInterface.getRoleList() 是同步方法</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRoleStore {

    private static final String KEY_PREFIX = "sa:roles:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 为指定用户添加一个角色（如果已存在则忽略）
     *
     * @param loginId 登录ID/用户名
     * @param role    角色编码，例如 user、admin
     */
    public void addRole(String loginId, String role) {
        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(role)) {
            return;
        }
        String key = buildKey(loginId);
        String value = role.trim();

        try {
            // 使用同步方法写入 Redis Set
            Long result = redisTemplate.opsForSet().add(key, value);
            if (result != null && result > 0) {
                log.debug("成功向 Redis 写入角色, loginId={}, role={}", loginId, value);
            }
        } catch (Exception e) {
            log.error("向 Redis 写入角色异常, loginId={}, role={}", loginId, value, e);
        }
    }

    /**
     * 获取指定用户的角色列表
     *
     * @param loginId 登录ID/用户名
     * @return 角色列表（可能为空列表，绝不为 null）
     */
    public List<String> getRoles(String loginId) {
        if (!StringUtils.hasText(loginId)) {
            return Collections.emptyList();
        }
        String key = buildKey(loginId);
        try {
            Set<String> roles = redisTemplate.opsForSet().members(key);
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            return new java.util.ArrayList<>(roles);
        } catch (Exception e) {
            log.error("从 Redis 读取角色异常, loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }

    private String buildKey(String loginId) {
        return KEY_PREFIX + loginId.trim();
    }
}
