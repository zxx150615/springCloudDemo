package com.zxx.learning.generator.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库表信息
 * 
 * @author code-generator
 */
@Data
public class TableInfo {
    
    /**
     * 表名（数据库表名）
     */
    private String tableName;
    
    /**
     * 类名（Java 类名，首字母大写，驼峰命名）
     */
    private String className;
    
    /**
     * 类名首字母小写（用于变量名）
     */
    private String classNameLower;
    
    /**
     * 表注释
     */
    private String comment;
    
    /**
     * 列信息列表
     */
    private List<ColumnInfo> columns = new ArrayList<>();
    
    /**
     * 主键列
     */
    private ColumnInfo primaryKey;
    
    /**
     * 包名配置
     */
    private String packageBase;
    private String packageEntity;
    private String packageController;
    private String packageService;
    private String packageServiceImpl;
    private String packageMapper;
    
    /**
     * 模块名（如 user、order）
     */
    private String module;
    
    /**
     * 服务名（如 user-service）
     */
    private String serviceName;
    
    /**
     * 需要导入的包列表（用于 Entity）
     */
    private List<String> importPackages = new ArrayList<>();
    
    /**
     * 添加需要导入的包（去重）
     */
    public void addImportPackage(String packageName) {
        if (packageName != null && !packageName.isEmpty() && !importPackages.contains(packageName)) {
            importPackages.add(packageName);
        }
    }
}
