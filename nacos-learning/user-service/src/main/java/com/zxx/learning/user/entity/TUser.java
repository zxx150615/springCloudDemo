package com.zxx.learning.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据库用户表实体，对应表：t_user
 *
 * 说明：用于认证与权限相关的用户信息存储。
 */
@Data
@TableName("t_user")
public class TUser {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名（唯一）
     */
    private String username;

    /**
     * 登录密码（BCrypt 加密）
     */
    private String password;

    /**
     * 角色：user / admin
     */
    private String role;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}

