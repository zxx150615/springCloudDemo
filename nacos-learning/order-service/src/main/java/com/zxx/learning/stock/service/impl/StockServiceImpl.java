package com.zxx.learning.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zxx.learning.common.entity.Stock;
import com.zxx.learning.stock.mapper.StockMapper;
import com.zxx.learning.stock.service.StockService;
import org.springframework.stereotype.Service;

/**
 * 库存表服务实现类
 * 
 * @author code-generator
 */
@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
}
