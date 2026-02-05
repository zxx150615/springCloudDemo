package com.zxx.learning.provider.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 从Nacos配置中心读取 spring.datasource.* 配置项
 * 
 * @author zxx
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig {
    
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    
    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
