package com.zxx.learning.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商家表
 * 
 * @author code-generator
 */
@Data
@TableName("merchant")
public class Merchant {
    
    /**
     * 商家ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商家名称
     */
    private String name;
    
    /**
     * 联系人姓名
     */
    private String contactPerson;
    
    /**
     * 联系电话
     */
    private String phone;
    
    /**
     * 邮箱地址
     */
    private String email;
    
    /**
     * 商家地址
     */
    private String address;
    
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
