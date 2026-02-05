package com.zxx.learning.common.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 
 * @author zxx
 */
@Data
public class Order {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 订单金额
     */
    private Double amount;
    
    /**
     * 订单状态：1-待支付，2-已支付，3-已取消
     */
    private Integer status;
    
    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    private Integer paymentMethod;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 支付时间
     */
    private LocalDateTime payTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 用户信息（通过Feign调用获取）
     */
    private User user;
}
