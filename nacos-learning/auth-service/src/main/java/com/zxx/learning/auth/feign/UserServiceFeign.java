package com.zxx.learning.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * User Service Feign 客户端
 * 
 * 调用 user-service 的内部接口进行用户认证相关操作。
 * 
 * @author zxx
 */
@FeignClient(name = "user-service", path = "/user")
public interface UserServiceFeign {

    /**
     * 用户登录校验
     * 
     * @param request 登录请求参数 {username, password}
     * @return 响应结果 {success: boolean, msg: String, data: {id, username, role, status}}
     */
    @PostMapping("/internal/validate-login")
    Map<String, Object> validateLogin(@RequestBody Map<String, Object> request);

    /**
     * 用户注册
     * 
     * @param request 注册请求参数 {username, password, role}
     * @return 响应结果 {success: boolean, msg: String, data: {id, username, role, status}}
     */
    @PostMapping("/internal/register")
    Map<String, Object> register(@RequestBody Map<String, Object> request);
}
