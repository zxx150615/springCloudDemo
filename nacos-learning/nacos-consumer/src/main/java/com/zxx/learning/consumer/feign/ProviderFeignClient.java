package com.zxx.learning.consumer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Provider服务Feign客户端
 * 
 * @author zxx
 */
@FeignClient(name = "nacos-provider")
public interface ProviderFeignClient {

    /**
     * 调用Provider的Hello接口
     */
    @GetMapping("/hello")
    Map<String, Object> hello();

    /**
     * 调用Provider的Hello接口（带参数）
     */
    @GetMapping("/hello/{name}")
    Map<String, Object> hello(@PathVariable("name") String name);

    /**
     * 获取Provider服务信息
     */
    @GetMapping("/hello/info")
    Map<String, Object> info();
}
