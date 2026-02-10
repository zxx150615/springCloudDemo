package com.zxx.learning.provider.controller;

import com.zxx.learning.provider.config.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库配置控制器
 * 用于测试从Nacos配置中心读取数据库配置
 * 
 * @author zxx
 */
@Slf4j
@RestController
@RequestMapping("/database")
public class DatabaseConfigController {
    
    @Autowired
    private DataSourceConfig dataSourceConfig;
    
    @Autowired
    private DataSource dataSource;
    
    /**
     * 获取数据库配置信息（不包含密码）
     */
    @GetMapping("/config")
    public Map<String, Object> getDatabaseConfig() {
        Map<String, Object> result = new HashMap<>();
        if (dataSourceConfig != null) {
            result.put("driverClassName", dataSourceConfig.getDriverClassName());
            result.put("url", dataSourceConfig.getUrl());
            result.put("username", dataSourceConfig.getUsername());
            result.put("password", "******");
        }
        return result;
    }
    
    /**
     * 测试数据库连接
     */
    @GetMapping("/test")
    public Map<String, Object> testDatabaseConnection() {
        Map<String, Object> result = new HashMap<>();
        try {
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            
            Map<String, Object> dbInfo = new HashMap<>();
            dbInfo.put("databaseProductName", metaData.getDatabaseProductName());
            dbInfo.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            dbInfo.put("driverName", metaData.getDriverName());
            dbInfo.put("driverVersion", metaData.getDriverVersion());
            dbInfo.put("url", metaData.getURL());
            dbInfo.put("username", metaData.getUserName());
            
            result.put("success", true);
            result.put("message", "数据库连接成功");
            result.put("databaseInfo", dbInfo);
            
            connection.close();
        } catch (Exception e) {
            log.error("数据库连接测试失败", e);
            result.put("success", false);
            result.put("message", "数据库连接失败: " + e.getMessage());
        }
        return result;
    }
}
