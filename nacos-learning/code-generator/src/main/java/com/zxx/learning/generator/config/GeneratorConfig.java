package com.zxx.learning.generator.config;

import lombok.Data;

/**
 * 代码生成器配置
 * 
 * @author code-generator
 */
@Data
public class GeneratorConfig {
    
    /**
     * 基础包名
     */
    private String packageBase = "com.zxx.learning";
    
    /**
     * Entity 包名
     */
    private String packageEntity = "com.zxx.learning.common.entity";
    
    /**
     * Controller 包名模板（{module} 会被替换）
     */
    private String packageControllerTemplate = "com.zxx.learning.{module}.controller";
    
    /**
     * Service 包名模板
     */
    private String packageServiceTemplate = "com.zxx.learning.{module}.service";
    
    /**
     * ServiceImpl 包名模板
     */
    private String packageServiceImplTemplate = "com.zxx.learning.{module}.service.impl";
    
    /**
     * Mapper 包名模板
     */
    private String packageMapperTemplate = "com.zxx.learning.{module}.mapper";
    
    /**
     * Entity 输出路径（相对于项目根目录）
     */
    private String outputEntityPath = "../nacos-common/src/main/java";
    
    /**
     * Controller 输出路径模板
     */
    private String outputControllerPathTemplate = "../{service-name}/src/main/java";
    
    /**
     * Service 输出路径模板
     */
    private String outputServicePathTemplate = "../{service-name}/src/main/java";
    
    /**
     * Mapper 输出路径模板
     */
    private String outputMapperPathTemplate = "../{service-name}/src/main/java";
    
    /**
     * 作者名称
     */
    private String author = "code-generator";
}
