package com.zxx.learning.user.controller;

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
import java.util.Map;

/**
 * 管理员控制器
 * API 路径: /admin/**（Gateway 会通过 /api/admin/** 访问，去掉 /api 前缀后变成 /admin/**）
 *
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private TUserService tUserService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 管理员新增用户接口
     * POST /admin/user
     *
     * 说明：
     *  - 需要 admin 角色权限（由 Gateway 验证）
     *  - 可以设置用户密码、角色、状态等信息
     *  - 密码会自动使用 BCrypt 加密
     *  - 统一响应格式：{success, msg, data}
     */
    @PostMapping("/user")
    public Map<String, Object> createUser(@RequestBody AdminCreateUserRequest request) {
        log.info("管理员创建用户: {}", request);

        // 参数校验
        if (request == null || !StringUtils.hasText(request.getUsername())) {
            return error("用户名不能为空");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            return error("密码不能为空");
        }

        // 用户名唯一性校验
        TUser existing = tUserService.getByUsername(request.getUsername().trim());
        if (existing != null) {
            log.info("创建用户失败，用户名已存在: {}", request.getUsername());
            return error("用户名已存在");
        }

        // 创建新用户
        TUser entity = new TUser();
        entity.setUsername(request.getUsername().trim());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置邮箱（可选）
        if (StringUtils.hasText(request.getEmail())) {
            entity.setEmail(request.getEmail().trim());
        }
        
        // 设置角色（可选，默认为 user）
        if (StringUtils.hasText(request.getRole())) {
            entity.setRole(request.getRole().trim());
        } else {
            entity.setRole("user");
        }
        
        // 设置状态（可选，默认为 1-正常）
        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        } else {
            entity.setStatus(1);
        }

        // 保存用户
        tUserService.createUser(entity);

        log.info("管理员创建用户成功, username={}, role={}, status={}", 
                entity.getUsername(), entity.getRole(), entity.getStatus());

        // 构建返回数据（不包含密码）
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.getId());
        data.put("username", entity.getUsername());
        data.put("email", entity.getEmail());
        data.put("role", entity.getRole());
        data.put("status", entity.getStatus());
        data.put("createTime", entity.getCreateTime());

        return success("创建成功", data);
    }

    /**
     * 返回成功响应
     */
    private Map<String, Object> success(String msg, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", msg);
        result.put("data", data);
        return result;
    }

    /**
     * 返回错误响应
     */
    private Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", msg);
        return result;
    }

    /**
     * 管理员创建用户请求参数
     */
    @Data
    public static class AdminCreateUserRequest {
        /**
         * 用户名（必填）
         */
        private String username;

        /**
         * 密码明文（必填，将在服务端做 BCrypt 加密）
         */
        private String password;

        /**
         * 邮箱（可选）
         */
        private String email;

        /**
         * 角色：user / admin（可选，默认为 user）
         */
        private String role;

        /**
         * 状态：1-正常，0-禁用（可选，默认为 1）
         */
        private Integer status;
    }
}
