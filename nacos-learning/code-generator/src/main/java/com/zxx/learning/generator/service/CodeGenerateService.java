package com.zxx.learning.generator.service;

import com.zxx.learning.generator.config.GeneratorConfig;
import com.zxx.learning.generator.model.ColumnInfo;
import com.zxx.learning.generator.model.TableInfo;
import com.zxx.learning.generator.util.StringUtil;
import com.zxx.learning.generator.util.TypeConverter;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成服务
 * 
 * @author code-generator
 */
@Slf4j
public class CodeGenerateService {
    
    private final Configuration freeMarkerConfig;
    private final GeneratorConfig config;
    private final IncrementalUpdateService incrementalUpdateService;
    
    public CodeGenerateService(GeneratorConfig config) {
        this.config = config;
        this.incrementalUpdateService = new IncrementalUpdateService();
        
        // 初始化 FreeMarker
        this.freeMarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        this.freeMarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates");
        this.freeMarkerConfig.setDefaultEncoding("UTF-8");
    }
    
    /**
     * 生成代码（首次生成或增量更新）
     * 
     * @param tableInfo 表信息
     * @param updateMode 是否为更新模式
     * @param updateEntityOnly 是否只更新 Entity
     */
    public void generateCode(TableInfo tableInfo, boolean updateMode, boolean updateEntityOnly) {
        // 设置包名和路径
        setupPackageAndPath(tableInfo);
        
        // 收集需要导入的包
        collectImportPackages(tableInfo);
        
        if (updateMode) {
            // 增量更新模式
            if (updateEntityOnly) {
                // 只更新 Entity
                updateEntity(tableInfo);
            } else {
                // 更新所有文件
                updateEntity(tableInfo);
                generateController(tableInfo);
                generateService(tableInfo);
                generateServiceImpl(tableInfo);
                generateMapper(tableInfo);
            }
        } else {
            // 首次生成模式
            generateEntity(tableInfo);
            generateController(tableInfo);
            generateService(tableInfo);
            generateServiceImpl(tableInfo);
            generateMapper(tableInfo);
        }
    }
    
    /**
     * 设置包名和路径
     */
    private void setupPackageAndPath(TableInfo tableInfo) {
        String module = tableInfo.getModule();
        String serviceName = tableInfo.getServiceName();
        
        tableInfo.setPackageBase(config.getPackageBase());
        tableInfo.setPackageEntity(config.getPackageEntity());
        tableInfo.setPackageController(config.getPackageControllerTemplate().replace("{module}", module));
        tableInfo.setPackageService(config.getPackageServiceTemplate().replace("{module}", module));
        tableInfo.setPackageServiceImpl(config.getPackageServiceImplTemplate().replace("{module}", module));
        tableInfo.setPackageMapper(config.getPackageMapperTemplate().replace("{module}", module));
    }
    
    /**
     * 收集需要导入的包
     */
    private void collectImportPackages(TableInfo tableInfo) {
        for (ColumnInfo column : tableInfo.getColumns()) {
            String importPackage = TypeConverter.getImportPackage(column.getJavaType());
            if (importPackage != null) {
                tableInfo.addImportPackage(importPackage);
            }
        }
    }
    
    /**
     * 生成 Entity
     */
    private void generateEntity(TableInfo tableInfo) {
        try {
            Template template = freeMarkerConfig.getTemplate("entity.ftl");
            String code = processTemplate(template, tableInfo);
            
            String packagePath = tableInfo.getPackageEntity().replace(".", "/");
            String filePath = config.getOutputEntityPath() + "/" + packagePath + "/" + tableInfo.getClassName() + ".java";
            
            writeFile(filePath, code);
            log.info("生成 Entity: {}", filePath);
        } catch (Exception e) {
            log.error("生成 Entity 失败", e);
        }
    }
    
    /**
     * 更新 Entity（增量更新）
     */
    private void updateEntity(TableInfo tableInfo) {
        String packagePath = tableInfo.getPackageEntity().replace(".", "/");
        String filePath = config.getOutputEntityPath() + "/" + packagePath + "/" + tableInfo.getClassName() + ".java";
        
        boolean success = incrementalUpdateService.updateEntity(tableInfo, filePath);
        if (!success) {
            // 如果更新失败，尝试首次生成
            generateEntity(tableInfo);
        }
    }
    
    /**
     * 生成 Controller
     */
    private void generateController(TableInfo tableInfo) {
        try {
            Template template = freeMarkerConfig.getTemplate("controller.ftl");
            String code = processTemplate(template, tableInfo);
            
            String packagePath = tableInfo.getPackageController().replace(".", "/");
            String outputPath = config.getOutputControllerPathTemplate()
                    .replace("{service-name}", tableInfo.getServiceName());
            String filePath = outputPath + "/" + packagePath + "/" + tableInfo.getClassName() + "Controller.java";
            
            writeFile(filePath, code);
            log.info("生成 Controller: {}", filePath);
        } catch (Exception e) {
            log.error("生成 Controller 失败", e);
        }
    }
    
    /**
     * 生成 Service
     */
    private void generateService(TableInfo tableInfo) {
        try {
            Template template = freeMarkerConfig.getTemplate("service.ftl");
            String code = processTemplate(template, tableInfo);
            
            String packagePath = tableInfo.getPackageService().replace(".", "/");
            String outputPath = config.getOutputServicePathTemplate()
                    .replace("{service-name}", tableInfo.getServiceName());
            String filePath = outputPath + "/" + packagePath + "/" + tableInfo.getClassName() + "Service.java";
            
            writeFile(filePath, code);
            log.info("生成 Service: {}", filePath);
        } catch (Exception e) {
            log.error("生成 Service 失败", e);
        }
    }
    
    /**
     * 生成 ServiceImpl
     */
    private void generateServiceImpl(TableInfo tableInfo) {
        try {
            Template template = freeMarkerConfig.getTemplate("serviceImpl.ftl");
            String code = processTemplate(template, tableInfo);
            
            String packagePath = tableInfo.getPackageServiceImpl().replace(".", "/");
            String outputPath = config.getOutputServicePathTemplate()
                    .replace("{service-name}", tableInfo.getServiceName());
            String filePath = outputPath + "/" + packagePath + "/" + tableInfo.getClassName() + "ServiceImpl.java";
            
            writeFile(filePath, code);
            log.info("生成 ServiceImpl: {}", filePath);
        } catch (Exception e) {
            log.error("生成 ServiceImpl 失败", e);
        }
    }
    
    /**
     * 生成 Mapper
     */
    private void generateMapper(TableInfo tableInfo) {
        try {
            Template template = freeMarkerConfig.getTemplate("mapper.ftl");
            String code = processTemplate(template, tableInfo);
            
            String packagePath = tableInfo.getPackageMapper().replace(".", "/");
            String outputPath = config.getOutputMapperPathTemplate()
                    .replace("{service-name}", tableInfo.getServiceName());
            String filePath = outputPath + "/" + packagePath + "/" + tableInfo.getClassName() + "Mapper.java";
            
            writeFile(filePath, code);
            log.info("生成 Mapper: {}", filePath);
        } catch (Exception e) {
            log.error("生成 Mapper 失败", e);
        }
    }
    
    /**
     * 处理模板
     */
    private String processTemplate(Template template, TableInfo tableInfo) throws IOException, TemplateException {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableInfo", tableInfo);
        dataModel.put("tableName", tableInfo.getTableName());
        dataModel.put("className", tableInfo.getClassName());
        dataModel.put("classNameLower", tableInfo.getClassNameLower());
        dataModel.put("comment", tableInfo.getComment());
        dataModel.put("columns", tableInfo.getColumns());
        dataModel.put("module", tableInfo.getModule());
        dataModel.put("packageEntity", tableInfo.getPackageEntity());
        dataModel.put("packageController", tableInfo.getPackageController());
        dataModel.put("packageService", tableInfo.getPackageService());
        dataModel.put("packageServiceImpl", tableInfo.getPackageServiceImpl());
        dataModel.put("packageMapper", tableInfo.getPackageMapper());
        dataModel.put("importPackages", tableInfo.getImportPackages());
        
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
    
    /**
     * 写入文件
     */
    private void writeFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
    }
}
