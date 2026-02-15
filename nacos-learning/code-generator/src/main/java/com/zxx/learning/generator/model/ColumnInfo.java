package com.zxx.learning.generator.model;

import lombok.Data;

/**
 * 数据库列信息
 * 
 * @author code-generator
 */
@Data
public class ColumnInfo {
    
    /**
     * 列名（数据库字段名）
     */
    private String columnName;
    
    /**
     * 属性名（Java 属性名，驼峰命名）
     */
    private String propertyName;
    
    /**
     * MySQL 数据类型
     */
    private String dataType;
    
    /**
     * Java 类型
     */
    private String javaType;
    
    /**
     * 类型长度（如 VARCHAR(50) 中的 50）
     */
    private String length;
    
    /**
     * 是否主键
     */
    private boolean primaryKey;
    
    /**
     * 是否自增
     */
    private boolean autoIncrement;
    
    /**
     * 是否允许 NULL
     */
    private boolean nullable;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 字段注释
     */
    private String comment;
    
    /**
     * 是否在 INSERT 时忽略（如 create_time 等自动填充字段）
     */
    private boolean insertIgnore;
    
    /**
     * 是否在 UPDATE 时忽略（如 create_time 等不可更新字段）
     */
    private boolean updateIgnore;
}
