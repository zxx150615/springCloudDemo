package com.zxx.learning.user.dto;

import lombok.Data;

/**
 * 用户认证相关 DTO 定义（仅供 user-service 内部与网关调用使用）。
 */
public class UserAuthDtos {

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
        /**
         * 明文密码，由服务端使用 BCrypt 进行加密存储
         */
        private String password;
        /**
         * 角色，可选：user / admin
         * 自注册场景由网关强制为 user
         */
        private String role;
    }

    /**
     * 认证结果中返回的用户关键信息
     */
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String role;
        private Integer status;
    }
}

