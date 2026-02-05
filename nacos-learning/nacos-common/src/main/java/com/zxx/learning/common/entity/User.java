package com.zxx.learning.common.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author zxx
 */
@Data
public class User {
    
    /**
     * 用户ID
     */
    private Long id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 年龄
     */
    private Integer age;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 状态：1-正常，0-禁用
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
