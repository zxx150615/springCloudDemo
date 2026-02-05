package com.zxx.learning.order.controller;

import com.zxx.learning.common.entity.Order;
import com.zxx.learning.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单服务控制器
 * API 路径: /order/**（Gateway 会通过 /api/order/** 访问）
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 根据ID获取订单
     * GET /order/{id}
     */
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        log.info("接收获取订单请求，ID: {}", id);
        return orderService.getOrderById(id);
    }
    
    /**
     * 获取订单列表
     * GET /order/list
     */
    @GetMapping("/list")
    public List<Order> getOrderList() {
        log.info("接收获取订单列表请求");
        return orderService.getOrderList();
    }
    
    /**
     * 创建订单
     * POST /order
     */
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        log.info("接收创建订单请求: {}", order);
        return orderService.createOrder(order);
    }
    
    /**
     * 获取用户的订单列表
     * GET /order/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Long userId) {
        log.info("接收获取用户订单列表请求，userId: {}", userId);
        return orderService.getOrdersByUserId(userId);
    }
    
    /**
     * 支付订单
     * POST /order/{orderId}/pay
     * 
     * @param orderId 订单ID
     * @param paymentMethod 支付方式：1-支付宝，2-微信，3-银行卡（从请求参数获取）
     */
    @PostMapping("/{orderId}/pay")
    public Order payOrder(@PathVariable Long orderId, @RequestParam(required = false, defaultValue = "1") Integer paymentMethod) {
        log.info("接收支付订单请求，orderId: {}, paymentMethod: {}", orderId, paymentMethod);
        return orderService.payOrder(orderId, paymentMethod);
    }
}
