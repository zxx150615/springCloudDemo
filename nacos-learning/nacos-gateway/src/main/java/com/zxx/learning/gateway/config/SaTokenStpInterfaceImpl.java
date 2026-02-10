package com.zxx.learning.gateway.config;

import cn.dev33.satoken.stp.StpInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限与角色获取实现类。
 *
 * <p>这里仅实现角色查询逻辑，权限列表返回空集合即可。</p>
 *
 * Sa-Token 会自动从 Spring 容器中查找 {@link StpInterface} 实现，
 * 并通过 {@link #getRoleList(Object, String)} 获取当前登录用户的角色列表。
 *
 * 角色数据来源：{@link RedisRoleStore}
 *
 * @author z
 */
@Component
@RequiredArgsConstructor
public class SaTokenStpInterfaceImpl implements StpInterface {

    private final RedisRoleStore redisRoleStore;

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
        // 从 Redis 中获取该登录ID对应的角色列表
        return redisRoleStore.getRoles(loginId.toString());
    }
}

