package com.zxx.learning.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zxx.learning.common.entity.Stock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存表数据访问层
 * 
 * @author code-generator
 */
@Mapper
public interface StockMapper extends BaseMapper<Stock> {
}
