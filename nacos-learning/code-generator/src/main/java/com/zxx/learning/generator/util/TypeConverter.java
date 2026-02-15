package com.zxx.learning.generator.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * MySQL 类型转 Java 类型转换器
 * 
 * @author code-generator
 */
public class TypeConverter {
    
    private static final Map<String, String> TYPE_MAP = new HashMap<>();
    
    static {
        // 整数类型
        TYPE_MAP.put("TINYINT", "Integer");
        TYPE_MAP.put("SMALLINT", "Integer");
        TYPE_MAP.put("MEDIUMINT", "Integer");
        TYPE_MAP.put("INT", "Integer");
        TYPE_MAP.put("INTEGER", "Integer");
        TYPE_MAP.put("BIGINT", "Long");
        
        // 浮点类型
        TYPE_MAP.put("FLOAT", "Float");
        TYPE_MAP.put("DOUBLE", "Double");
        TYPE_MAP.put("DECIMAL", "BigDecimal");
        TYPE_MAP.put("NUMERIC", "BigDecimal");
        
        // 字符串类型
        TYPE_MAP.put("CHAR", "String");
        TYPE_MAP.put("VARCHAR", "String");
        TYPE_MAP.put("TEXT", "String");
        TYPE_MAP.put("TINYTEXT", "String");
        TYPE_MAP.put("MEDIUMTEXT", "String");
        TYPE_MAP.put("LONGTEXT", "String");
        
        // 日期时间类型
        TYPE_MAP.put("DATE", "LocalDate");
        TYPE_MAP.put("TIME", "LocalTime");
        TYPE_MAP.put("DATETIME", "LocalDateTime");
        TYPE_MAP.put("TIMESTAMP", "LocalDateTime");
        TYPE_MAP.put("YEAR", "Integer");
        
        // 二进制类型
        TYPE_MAP.put("BINARY", "byte[]");
        TYPE_MAP.put("VARBINARY", "byte[]");
        TYPE_MAP.put("BLOB", "byte[]");
        TYPE_MAP.put("TINYBLOB", "byte[]");
        TYPE_MAP.put("MEDIUMBLOB", "byte[]");
        TYPE_MAP.put("LONGBLOB", "byte[]");
        
        // 布尔类型
        TYPE_MAP.put("BIT", "Boolean");
        TYPE_MAP.put("BOOLEAN", "Boolean");
        TYPE_MAP.put("BOOL", "Boolean");
        
        // JSON 类型
        TYPE_MAP.put("JSON", "String");
    }
    
    /**
     * MySQL 类型转 Java 类型
     * 
     * @param mysqlType MySQL 类型（如 VARCHAR、INT、BIGINT）
     * @return Java 类型（如 String、Integer、Long）
     */
    public static String mysqlToJava(String mysqlType) {
        if (mysqlType == null || mysqlType.isEmpty()) {
            return "String";
        }
        
        // 移除长度信息，如 VARCHAR(50) -> VARCHAR
        String type = mysqlType.toUpperCase().trim();
        int parenIndex = type.indexOf('(');
        if (parenIndex > 0) {
            type = type.substring(0, parenIndex).trim();
        }
        
        // 查找映射
        String javaType = TYPE_MAP.get(type);
        if (javaType != null) {
            return javaType;
        }
        
        // 默认返回 String
        return "String";
    }
    
    /**
     * 获取需要导入的包
     * 
     * @param javaType Java 类型
     * @return 需要导入的包名，如果不需要导入则返回 null
     */
    public static String getImportPackage(String javaType) {
        if (javaType == null) {
            return null;
        }
        
        switch (javaType) {
            case "BigDecimal":
                return "java.math.BigDecimal";
            case "LocalDate":
                return "java.time.LocalDate";
            case "LocalTime":
                return "java.time.LocalTime";
            case "LocalDateTime":
                return "java.time.LocalDateTime";
            default:
                return null;
        }
    }
    
    /**
     * 判断是否需要导入包
     */
    public static boolean needImport(String javaType) {
        return getImportPackage(javaType) != null;
    }
}
