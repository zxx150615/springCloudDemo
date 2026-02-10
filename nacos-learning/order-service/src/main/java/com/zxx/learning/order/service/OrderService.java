package com.zxx.learning.order.service;

import com.zxx.learning.common.entity.Order;
import com.zxx.learning.order.feign.UserServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 订单服务
 * 
 * @author zxx
 */
@Slf4j
@Service
public class OrderService {

    @Autowired
    private UserServiceClient userServiceClient;
    
    // 模拟数据库，使用内存存储
    private final List<Order> orderDatabase = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * 根据ID获取订单
     */
    public Order getOrderById(Long id) {
        log.info("根据ID获取订单，ID: {}", id);
        
        Order order = orderDatabase.stream()
                .filter(o -> o.getId().equals(id))
                .findFirst()
                .orElse(null);
        
        if (order == null) {
            // 如果不存在，返回一个模拟订单
            order = new Order();
            order.setId(id);
            order.setUserId(1L);
            order.setOrderNo("ORD" + String.format("%08d", id));
            order.setAmount(100.0 + id * 10);
            order.setStatus(1);
            order.setCreateTime(LocalDateTime.now());
            order.setRemark("模拟订单数据");
        } else {
            // 如果订单存在，通过Feign调用用户服务获取用户信息
            try {
                order.setUser(userServiceClient.getUserById(order.getUserId()));
            } catch (Exception e) {
                log.warn("获取用户信息失败: {}", e.getMessage());
            }
        }
        
        return order;
    }
    
    /**
     * 获取订单列表
     */
    public List<Order> getOrderList() {
        log.info("获取订单列表");
        
        // 如果数据库为空，初始化一些测试数据
        if (orderDatabase.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                Order order = new Order();
                order.setId((long) i);
                order.setUserId((long) i);
                order.setOrderNo("ORD" + String.format("%08d", i));
                order.setAmount(100.0 + i * 10);
                order.setStatus(1);
                order.setCreateTime(LocalDateTime.now());
                order.setRemark("测试订单" + i);
                orderDatabase.add(order);
            }
        }
        
        return orderDatabase;
    }
    
    /**
     * 创建订单
     */
    public Order createOrder(Order order) {
        log.info("创建订单: {}", order);
        
        // 验证用户是否存在
        try {
            userServiceClient.getUserById(order.getUserId());
        } catch (Exception e) {
            log.warn("用户不存在: {}", e.getMessage());
            throw new RuntimeException("用户不存在: " + order.getUserId());
        }
        
        order.setId(idGenerator.getAndIncrement());
        order.setOrderNo("ORD" + String.format("%08d", order.getId()));
            order.setCreateTime(LocalDateTime.now());
        if (order.getStatus() == null) {
            order.setStatus(1);
        }
        orderDatabase.add(order);
        
        return order;
    }
    
    /**
     * 根据用户ID获取订单列表
     */
    public List<Order> getOrdersByUserId(Long userId) {
        log.info("根据用户ID获取订单列表，userId: {}", userId);
        
        List<Order> orders = orderDatabase.stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
        
        // 为每个订单填充用户信息
        orders.forEach(order -> {
            try {
                order.setUser(userServiceClient.getUserById(userId));
            } catch (Exception e) {
                log.warn("获取用户信息失败: {}", e.getMessage());
            }
        });
        
        return orders;
    }
    
    /**
     * 支付订单
     */
    public Order payOrder(Long orderId, Integer paymentMethod) {
        log.info("支付订单，orderId: {}, paymentMethod: {}", orderId, paymentMethod);
        
        Order order = orderDatabase.stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("订单不存在: " + orderId));
        
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        
        order.setStatus(2); // 已支付
        order.setPaymentMethod(paymentMethod);
        order.setPayTime(LocalDateTime.now());
        
        return order;
    }
}
