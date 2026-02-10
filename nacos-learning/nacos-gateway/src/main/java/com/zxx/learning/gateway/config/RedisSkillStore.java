package com.zxx.learning.gateway.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 使用 Redis 存储用户技能的实现（基于 StringRedisTemplate，同步版本）。
 *
 * <p><b>技术栈声明</b>：本项目统一使用 StringRedisTemplate（同步版本），不使用 ReactiveStringRedisTemplate。
 * 详见项目根目录下的 REDIS_TECH_STACK.md 文档。</p>
 *
 * <p>说明：
 * <ul>
 *     <li>key 结构：sa:skills:{loginId}</li>
 *     <li>value：Set<String>，例如 java、python、spring</li>
 *     <li>使用同步的 StringRedisTemplate</li>
 * </ul>
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSkillStore {

    private static final String KEY_PREFIX = "sa:skills:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 为指定用户添加一个技能（如果已存在则忽略）
     *
     * @param loginId 登录ID/用户名
     * @param skill   技能名称，例如 java、python、spring
     */
    public void addSkill(String loginId, String skill) {
        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(skill)) {
            return;
        }
        String key = buildKey(loginId);
        String value = skill.trim();

        try {
            // 使用同步方法写入 Redis Set
            Long result = redisTemplate.opsForSet().add(key, value);
            if (result != null && result > 0) {
                log.debug("成功向 Redis 写入技能, loginId={}, skill={}", loginId, value);
            }
        } catch (Exception e) {
            log.error("向 Redis 写入技能异常, loginId={}, skill={}", loginId, value, e);
        }
    }

    /**
     * 为指定用户批量添加技能
     *
     * @param loginId 登录ID/用户名
     * @param skills  技能列表
     */
    public void addSkills(String loginId, List<String> skills) {
        if (!StringUtils.hasText(loginId) || skills == null || skills.isEmpty()) {
            return;
        }
        for (String skill : skills) {
            if (StringUtils.hasText(skill)) {
                addSkill(loginId, skill.trim());
            }
        }
    }

    /**
     * 获取指定用户的技能列表
     *
     * @param loginId 登录ID/用户名
     * @return 技能列表（可能为空列表，绝不为 null）
     */
    public List<String> getSkills(String loginId) {
        if (!StringUtils.hasText(loginId)) {
            return Collections.emptyList();
        }
        String key = buildKey(loginId);
        try {
            Set<String> skills = redisTemplate.opsForSet().members(key);
            if (skills == null || skills.isEmpty()) {
                return Collections.emptyList();
            }
            return new java.util.ArrayList<>(skills);
        } catch (Exception e) {
            log.error("从 Redis 读取技能异常, loginId={}", loginId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 移除指定用户的某个技能
     *
     * @param loginId 登录ID/用户名
     * @param skill   技能名称
     */
    public void removeSkill(String loginId, String skill) {
        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(skill)) {
            return;
        }
        String key = buildKey(loginId);
        String value = skill.trim();

        try {
            Long result = redisTemplate.opsForSet().remove(key, value);
            if (result != null && result > 0) {
                log.debug("成功从 Redis 移除技能, loginId={}, skill={}", loginId, value);
            }
        } catch (Exception e) {
            log.error("从 Redis 移除技能异常, loginId={}, skill={}", loginId, value, e);
        }
    }

    /**
     * 检查用户是否拥有指定技能
     *
     * @param loginId 登录ID/用户名
     * @param skill   技能名称
     * @return 是否拥有该技能
     */
    public boolean hasSkill(String loginId, String skill) {
        if (!StringUtils.hasText(loginId) || !StringUtils.hasText(skill)) {
            return false;
        }
        String key = buildKey(loginId);
        try {
            Boolean result = redisTemplate.opsForSet().isMember(key, skill.trim());
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.error("检查用户技能异常, loginId={}, skill={}", loginId, skill, e);
            return false;
        }
    }

    private String buildKey(String loginId) {
        return KEY_PREFIX + loginId.trim();
    }
}
