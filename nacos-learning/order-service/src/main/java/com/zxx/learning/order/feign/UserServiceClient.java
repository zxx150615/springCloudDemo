package com.zxx.learning.order.feign;

import com.zxx.learning.common.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端
 * 用于调用 user-service 获取用户信息
 * 
 * @author zxx
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserServiceClient {
    
    /**
     * 根据用户ID获取用户信息
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    User getUserById(@PathVariable("id") Long id);
}
