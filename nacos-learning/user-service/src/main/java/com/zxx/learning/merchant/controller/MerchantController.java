package com.zxx.learning.merchant.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxx.learning.common.entity.Merchant;
import com.zxx.learning.merchant.service.MerchantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家表控制器
 * API 路径: /merchant/**（Gateway 会通过 /api/merchant/** 访问）
 * 
 * @author code-generator
 */
@Slf4j
@RestController
@RequestMapping("/merchant")
public class MerchantController {
    
    @Autowired
    private MerchantService merchantService;
    
    /**
     * 根据ID获取商家表
     * GET /merchant/{id}
     */
    @GetMapping("/{id}")
    public Merchant getById(@PathVariable Long id) {
        log.info("根据ID获取商家表，ID: {}", id);
        return merchantService.getById(id);
    }
    
    /**
     * 获取商家表列表
     * GET /merchant/list
     */
    @GetMapping("/list")
    public List<Merchant> list() {
        log.info("获取商家表列表");
        return merchantService.list();
    }
    
    /**
     * 分页查询商家表
     * GET /merchant/page?current=1&size=10
     */
    @GetMapping("/page")
    public Page<Merchant> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("分页查询商家表，current: {}, size: {}", current, size);
        return merchantService.page(new Page<>(current, size));
    }
    
    /**
     * 创建商家表
     * POST /merchant
     */
    @PostMapping
    public boolean save(@RequestBody Merchant merchant) {
        log.info("创建商家表: {}", merchant);
        return merchantService.save(merchant);
    }
    
    /**
     * 更新商家表
     * PUT /merchant
     */
    @PutMapping
    public boolean updateById(@RequestBody Merchant merchant) {
        log.info("更新商家表: {}", merchant);
        return merchantService.updateById(merchant);
    }
    
    /**
     * 删除商家表
     * DELETE /merchant/{id}
     */
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        log.info("删除商家表，ID: {}", id);
        return merchantService.removeById(id);
    }
}
