package com.zxx.learning.user.controller;

import com.zxx.learning.common.entity.User;
import com.zxx.learning.user.dto.UserAuthDtos;
import com.zxx.learning.user.entity.TUser;
import com.zxx.learning.user.service.TUserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务控制器
 * API 路径: /user/**（Gateway 会通过 /api/user/** 访问）
 *
 * 说明：
 *  - 对外仍然使用通用的 User DTO（com.zxx.learning.common.entity.User）
 *  - 内部通过 MyBatis-Plus 操作 t_user 表（实体：TUser）
 *
 * 同时在此类中提供 /user/internal/** 内部接口，供网关在登录/注册时调用。
 *
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private TUserService tUserService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 根据ID获取用户
     * GET /user/{id}
     *
     * 说明：该接口被 order-service 的 FeignClient 使用，保持返回 User DTO。
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("根据ID获取用户，ID: {}", id);

        TUser entity = tUserService.getById(id);
        if (entity == null) {
            log.info("未找到ID为 {} 的用户", id);
            return null;
        }

        User user = convertToDto(entity);
        log.info("返回用户: {}", user);
        return user;
    }

    /**
     * 获取用户列表
     * GET /user/list
     */
    @GetMapping("/list")
    public List<User> getUserList() {
        log.info("获取用户列表");

        List<TUser> list = tUserService.list();
        return list.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 创建用户
     * POST /user
     *
     * 说明：
     *  - 请求体中应包含至少 username、email 等基础信息；
     *  - 密码与角色的完整管理将由后续内部接口（如 /user/internal/**）和网关逻辑补充。
     */
    @PostMapping
    public User createUser(@RequestBody TUser request) {
        log.info("创建用户: {}", request);

        TUser created = tUserService.createUser(request);
        return convertToDto(created);
    }

    /**
     * 根据用户名获取用户
     * GET /user/username/{username}
     */
    @GetMapping("/username/{username}")
    public User getUserByUsername(@PathVariable String username) {
        log.info("根据用户名获取用户，username: {}", username);

        TUser entity = tUserService.getByUsername(username);
        if (entity == null) {
            log.info("未找到用户名为 {} 的用户", username);
            return null;
        }

        return convertToDto(entity);
    }

    /**
     * 内部注册接口，仅供网关调用
     * POST /user/internal/register
     */
    @PostMapping("/internal/register")
    public Map<String, Object> internalRegister(@RequestBody UserAuthDtos.RegisterRequest request) {
        String username = request != null ? request.getUsername() : null;
        String password = request != null ? request.getPassword() : null;
        String role = request != null ? request.getRole() : null;

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return error("用户名和密码不能为空");
        }

        // 用户名唯一性校验
        TUser existing = tUserService.getByUsername(username);
        if (existing != null) {
            log.info("注册失败，用户名已存在: {}", username);
            return error("用户名已存在");
        }

        // 创建新用户，密码使用 BCrypt 加密
        TUser entity = new TUser();
        entity.setUsername(username.trim());
        entity.setPassword(passwordEncoder.encode(password));
        // 角色：如果未指定则默认为 user
        if (!StringUtils.hasText(role)) {
            role = "user";
        }
        entity.setRole(role.trim());
        // 状态：1-正常
        entity.setStatus(1);

        tUserService.createUser(entity);

        log.info("内部注册成功, username={}, role={}", username, entity.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("username", entity.getUsername());
        data.put("role", entity.getRole());
        data.put("status", entity.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "注册成功");
        result.put("data", data);
        return result;
    }

    /**
     * 内部登录校验接口，仅供网关调用
     * POST /user/internal/validate-login
     */
    @PostMapping("/internal/validate-login")
    public Map<String, Object> validateLogin(@RequestBody UserAuthDtos.LoginRequest request) {
        String username = request != null ? request.getUsername() : null;
        String password = request != null ? request.getPassword() : null;

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return error("用户名和密码不能为空");
        }

        TUser entity = tUserService.getByUsername(username);
        if (entity == null) {
            log.info("登录失败，用户不存在: {}", username);
            return error("用户未注册");
        }

        if (entity.getStatus() != null && entity.getStatus() == 0) {
            log.info("登录失败，用户已被禁用: {}", username);
            return error("用户已被禁用");
        }

        if (!passwordEncoder.matches(password, entity.getPassword())) {
            log.info("登录失败，密码错误: {}", username);
            return error("用户名或密码错误");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("username", entity.getUsername());
        data.put("role", entity.getRole());
        data.put("status", entity.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "登录成功");
        result.put("data", data);
        return result;
    }

    /**
     * 初始化管理员账号，仅用于手动创建管理员
     * POST /user/internal/init-admin
     */
    @PostMapping("/internal/init-admin")
    public Map<String, Object> initAdmin(@RequestBody InitAdminRequest request) {
        String username = request != null ? request.getUsername() : null;
        String password = request != null ? request.getPassword() : null;
        String email = request != null ? request.getEmail() : null;

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return error("用户名和密码不能为空");
        }

        // 检查是否已存在同名用户
        TUser existing = tUserService.getByUsername(username);
        if (existing != null) {
            log.info("初始化管理员失败，用户名已存在: {}", username);
            return error("用户名已存在");
        }

        // 构造管理员用户，密码使用 BCrypt 加密
        TUser entity = new TUser();
        entity.setUsername(username.trim());
        entity.setPassword(passwordEncoder.encode(password));
        entity.setRole("admin");
        entity.setStatus(1);
        if (StringUtils.hasText(email)) {
            entity.setEmail(email.trim());
        }

        tUserService.createUser(entity);

        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("username", entity.getUsername());
        data.put("role", entity.getRole());
        data.put("status", entity.getStatus());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "初始化管理员成功");
        result.put("data", data);
        return result;
    }

    /**
     * 将数据库实体 TUser 转换为通用 User DTO
     */
    private User convertToDto(TUser entity) {
        if (entity == null) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setUsername(entity.getUsername());
        user.setEmail(entity.getEmail());
        user.setStatus(entity.getStatus());
        user.setCreateTime(entity.getCreateTime());
        // t_user 中暂未包含 age、remark 字段，这里保持为 null
        return user;
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", msg);
        return result;
    }

    /**
     * 初始化管理员请求参数
     */
    @Data
    public static class InitAdminRequest {
        /**
         * 管理员用户名（必填）
         */
        private String username;

        /**
         * 管理员密码明文（必填，将在服务端做 BCrypt 加密）
         */
        private String password;

        /**
         * 管理员邮箱（可选）
         */
        private String email;
    }
}
