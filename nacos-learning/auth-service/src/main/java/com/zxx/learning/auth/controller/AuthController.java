package com.zxx.learning.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.zxx.learning.auth.config.RedisRoleStore;
import com.zxx.learning.auth.config.RedisSkillStore;
import com.zxx.learning.auth.feign.UserServiceFeign;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 * 提供登录与注册接口，创建 Sa-Token 登录会话
 *
 * 访问路径示例：
 *  - POST /api/auth/login
 *  - POST /api/auth/register
 *
 * 后续访问受保护接口时，在请求头中携带：
 *  Authorization: {token}
 *
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private RedisRoleStore redisRoleStore;

    @Resource
    private RedisSkillStore redisSkillStore;

    @Resource
    private UserServiceFeign userServiceFeign;

    /**
     * 登录接口
     * 调用 user-service 校验用户名和密码，成功后创建 Sa-Token 会话并将角色写入 Redis。
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String username = request != null ? request.getUsername() : null;
        String password = request != null ? request.getPassword() : null;

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error("用户名和密码不能为空"));
        }

        Map<String, Object> userInfoMap;
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            
            Map<String, Object> response = userServiceFeign.validateLogin(requestBody);
            
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error("用户服务无响应"));
            }

            Object successObj = response.get("success");
            boolean success = successObj instanceof Boolean && (Boolean) successObj;
            String msg = (String) response.getOrDefault("msg", "用户服务调用失败");

            if (!success) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(error(msg));
            }

            Object dataObj = response.get("data");
            if (!(dataObj instanceof Map)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error("用户服务返回数据格式不正确"));
            }
            userInfoMap = (Map<String, Object>) dataObj;
        } catch (Exception e) {
            log.error("调用 user-service 校验登录失败, username={}", username, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("用户服务异常，请稍后重试"));
        }

        String usernameFromResponse = (String) userInfoMap.get("username");
        if (!StringUtils.hasText(usernameFromResponse)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error("用户未注册"));
        }
        
        Object statusObj = userInfoMap.get("status");
        if (statusObj instanceof Number && ((Number) statusObj).intValue() == 0) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(error("用户已被禁用"));
        }

        // 创建登录会话（在同步请求线程里，Sa-Token 能拿到上下文）
        StpUtil.login(username);

        // 将角色写入 Redis，供 Sa-Token 鉴权使用
        String role = userInfoMap.get("role") != null ? String.valueOf(userInfoMap.get("role")) : "user";
        if (!StringUtils.hasText(role)) {
            role = "user";
        }
        redisRoleStore.addRole(username, role);

        Map<String, Object> data = new HashMap<>();
        data.put("loginId", StpUtil.getLoginIdAsString());
        data.put("token", StpUtil.getTokenValue());
        data.put("tokenName", StpUtil.getTokenName());
        data.put("expire", StpUtil.getTokenTimeout());
        data.put("role", role);

        log.info("用户登录成功, username={}, role={}, token={}", username, role, data.get("token"));

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "登录成功");
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

    /**
     * 注册接口
     * 说明：
     *  - 自注册场景：忽略前端传入的角色，统一使用 user；
     *  - 调用 user-service 完成真正的用户创建与密码加密；
     *  - 注册成功后自动登录，并将角色写入 Redis。
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String username = request != null ? request.getUsername() : null;
        String password = request != null ? request.getPassword() : null;
        String role = request != null ? request.getRole() : null;
        List<String> skills = request != null ? request.getSkills() : null;

        if (!StringUtils.hasText(username)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error("用户名不能为空"));
        }
        if (!StringUtils.hasText(password)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error("密码不能为空"));
        }

        // 自注册场景：强制设置为 user 角色，避免普通用户创建 admin
        String registerRole = StringUtils.hasText(role) ? role.trim() : "user";
        if (!"admin".equals(registerRole)) {
            registerRole = "user";
        }

        Map<String, Object> userInfoMap;
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("role", registerRole);
            
            Map<String, Object> response = userServiceFeign.register(requestBody);
            
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error("用户服务无响应"));
            }

            Object successObj = response.get("success");
            boolean success = successObj instanceof Boolean && (Boolean) successObj;
            String msg = (String) response.getOrDefault("msg", "用户服务调用失败");

            if (!success) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error(msg));
            }

            Object dataObj = response.get("data");
            if (!(dataObj instanceof Map)) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(error("用户服务返回数据格式不正确"));
            }
            userInfoMap = (Map<String, Object>) dataObj;
        } catch (Exception e) {
            log.error("调用 user-service 注册用户失败, username={}", username, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("用户服务异常，请稍后重试"));
        }

        // 注册成功后直接自动登录（在同步请求线程里，Sa-Token 能拿到上下文）
        StpUtil.login(username);

        // 分配角色：以 user-service 返回为准
        String assignedRole = userInfoMap.get("role") != null ? String.valueOf(userInfoMap.get("role")) : "user";
        if (!StringUtils.hasText(assignedRole)) {
            assignedRole = "user";
        }
        redisRoleStore.addRole(username, assignedRole);

        // 分配技能：如果指定了技能则添加到 Redis
        if (skills != null && !skills.isEmpty()) {
            redisSkillStore.addSkills(username, skills);
            log.info("为用户分配技能, username={}, skills={}", username, skills);
        }

        log.info("用户注册成功, username={}, role={}, skills={}", username, assignedRole, skills);

        Map<String, Object> data = new HashMap<>();
        data.put("loginId", StpUtil.getLoginIdAsString());
        data.put("token", StpUtil.getTokenValue());
        data.put("tokenName", StpUtil.getTokenName());
        data.put("expire", StpUtil.getTokenTimeout());
        data.put("role", assignedRole);
        data.put("skills", skills != null ? skills : Collections.emptyList());

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "注册并登录成功，已分配角色: " + assignedRole);
        result.put("data", data);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * 添加技能接口（需要登录）
     * POST /api/auth/skill/add
     */
    @PostMapping("/skill/add")
    public ResponseEntity<?> addSkill(@RequestBody SkillRequest request) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("请先登录"));
        }
        
        String loginId = StpUtil.getLoginIdAsString();
        String skill = request != null ? request.getSkill() : null;
        List<String> skills = request != null ? request.getSkills() : null;

        if (StringUtils.hasText(skill)) {
            // 添加单个技能
            redisSkillStore.addSkill(loginId, skill);
            log.info("用户添加技能, loginId={}, skill={}", loginId, skill);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("msg", "技能添加成功");
            result.put("data", Collections.singletonMap("skill", skill));
            return ResponseEntity.ok(result);
        } else if (skills != null && !skills.isEmpty()) {
            // 批量添加技能
            redisSkillStore.addSkills(loginId, skills);
            log.info("用户批量添加技能, loginId={}, skills={}", loginId, skills);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("msg", "技能批量添加成功");
            result.put("data", Collections.singletonMap("skills", skills));
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("请提供要添加的技能（skill 或 skills）"));
        }
    }

    /**
     * 查询当前用户的技能列表（需要登录）
     * GET /api/auth/skill/list
     */
    @GetMapping("/skill/list")
    public ResponseEntity<?> getSkills() {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("请先登录"));
        }
        
        String loginId = StpUtil.getLoginIdAsString();
        List<String> skills = redisSkillStore.getSkills(loginId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "查询成功");
        result.put("data", Collections.singletonMap("skills", skills));
        return ResponseEntity.ok(result);
    }

    /**
     * 移除技能接口（需要登录）
     * DELETE /api/auth/skill/remove
     */
    @DeleteMapping("/skill/remove")
    public ResponseEntity<?> removeSkill(@RequestBody SkillRequest request) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("请先登录"));
        }
        
        String loginId = StpUtil.getLoginIdAsString();
        String skill = request != null ? request.getSkill() : null;

        if (!StringUtils.hasText(skill)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(error("请提供要移除的技能名称"));
        }

        redisSkillStore.removeSkill(loginId, skill);
        log.info("用户移除技能, loginId={}, skill={}", loginId, skill);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "技能移除成功");
        result.put("data", Collections.singletonMap("skill", skill));
        return ResponseEntity.ok(result);
    }

    /**
     * 检查用户是否拥有指定技能（需要登录）
     * GET /api/auth/skill/check?skill=java
     */
    @GetMapping("/skill/check")
    public ResponseEntity<?> checkSkill(@RequestParam String skill) {
        // 获取当前登录用户
        if (!StpUtil.isLogin()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(error("请先登录"));
        }
        
        String loginId = StpUtil.getLoginIdAsString();
        boolean hasSkill = redisSkillStore.hasSkill(loginId, skill);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("msg", "查询成功");
        Map<String, Object> data = new HashMap<>();
        data.put("skill", skill);
        data.put("hasSkill", hasSkill);
        result.put("data", data);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> error(String msg) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("msg", msg);
        return result;
    }

    /**
     * 登录请求参数
     */
    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }

    /**
     * 注册请求参数
     */
    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
        /**
         * 角色（可选），如果不指定则默认分配 "user" 角色
         * 可选值：user, admin
         */
        private String role;
        /**
         * 技能列表（可选），例如 ["java", "python", "spring"]
         */
        private List<String> skills;
    }

    /**
     * 技能请求参数
     */
    @Data
    public static class SkillRequest {
        /**
         * 单个技能名称（与 skills 二选一）
         */
        private String skill;
        /**
         * 技能列表（与 skill 二选一）
         */
        private List<String> skills;
    }
}
