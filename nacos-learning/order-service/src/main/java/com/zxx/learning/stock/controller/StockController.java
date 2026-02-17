package com.zxx.learning.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zxx.learning.common.entity.Stock;
import com.zxx.learning.stock.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 库存表控制器
 * API 路径: /stock/**（Gateway 会通过 /api/stock/** 访问）
 * 
 * @author code-generator
 */
@Slf4j
@RestController
@RequestMapping("/stock")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    /**
     * 根据ID获取库存表
     * GET /stock/{id}
     */
    @GetMapping("/{id}")
    public Stock getById(@PathVariable Long id) {
        log.info("根据ID获取库存表，ID: {}", id);
        return stockService.getById(id);
    }
    
    /**
     * 获取库存表列表
     * GET /stock/list
     */
    @GetMapping("/list")
    public List<Stock> list() {
        log.info("获取库存表列表");
        return stockService.list();
    }
    
    /**
     * 分页查询库存表
     * GET /stock/page?current=1&size=10
     */
    @GetMapping("/page")
    public Page<Stock> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("分页查询库存表，current: {}, size: {}", current, size);
        return stockService.page(new Page<>(current, size));
    }
    
    /**
     * 创建库存表
     * POST /stock
     */
    @PostMapping
    public boolean save(@RequestBody Stock stock) {
        log.info("创建库存表: {}", stock);
        return stockService.save(stock);
    }
    
    /**
     * 更新库存表
     * PUT /stock
     */
    @PutMapping
    public boolean updateById(@RequestBody Stock stock) {
        log.info("更新库存表: {}", stock);
        return stockService.updateById(stock);
    }
    
    /**
     * 删除库存表
     * DELETE /stock/{id}
     */
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        log.info("删除库存表，ID: {}", id);
        return stockService.removeById(id);
    }
}
