package com.zxx.learning.generator.service;

import com.zxx.learning.generator.model.ColumnInfo;
import com.zxx.learning.generator.model.TableInfo;
import com.zxx.learning.generator.parser.JavaCodeParser;
import com.zxx.learning.generator.util.CodeMarkerUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 增量更新服务
 * 对比 SQL 和现有代码，智能合并新字段
 * 
 * @author code-generator
 */
@Slf4j
public class IncrementalUpdateService {
    
    private final JavaCodeParser javaCodeParser = new JavaCodeParser();
    
    /**
     * 增量更新 Entity 文件
     * 
     * @param tableInfo 表信息（从 SQL 解析）
     * @param entityFilePath Entity 文件路径
     * @return 是否更新成功
     */
    public boolean updateEntity(TableInfo tableInfo, String entityFilePath) {
        File entityFile = new File(entityFilePath);
        
        // 如果文件不存在，返回 false（应该使用首次生成）
        if (!entityFile.exists()) {
            log.warn("Entity 文件不存在，应该使用首次生成模式: {}", entityFilePath);
            return false;
        }
        
        try {
            // 读取现有文件内容
            String existingContent = FileUtils.readFileToString(entityFile, StandardCharsets.UTF_8);
            
            // 提取自定义代码（标记内的）
            List<String> customCodes = CodeMarkerUtil.extractCustomCode(existingContent);
            
            // 解析现有字段
            List<ColumnInfo> existingFields = javaCodeParser.parseEntityFields(entityFile);
            Map<String, ColumnInfo> existingFieldMap = existingFields.stream()
                    .collect(Collectors.toMap(ColumnInfo::getPropertyName, f -> f));
            
            // 找出新增字段
            List<ColumnInfo> newFields = new ArrayList<>();
            for (ColumnInfo column : tableInfo.getColumns()) {
                if (!existingFieldMap.containsKey(column.getPropertyName())) {
                    newFields.add(column);
                    log.info("发现新字段: {}.{}", tableInfo.getClassName(), column.getPropertyName());
                }
            }
            
            if (newFields.isEmpty()) {
                log.info("没有新字段需要添加: {}", tableInfo.getClassName());
                return true;
            }
            
            // 合并新字段到现有代码
            String updatedContent = mergeNewFields(existingContent, newFields, customCodes);
            
            // 写回文件
            FileUtils.writeStringToFile(entityFile, updatedContent, StandardCharsets.UTF_8);
            log.info("成功更新 Entity 文件，添加了 {} 个新字段: {}", newFields.size(), entityFilePath);
            
            return true;
            
        } catch (IOException e) {
            log.error("更新 Entity 文件失败: {}", entityFilePath, e);
            return false;
        }
    }
    
    /**
     * 合并新字段到现有代码
     * 
     * @param existingContent 现有代码
     * @param newFields 新字段列表
     * @param customCodes 自定义代码列表
     * @return 合并后的代码
     */
    private String mergeNewFields(String existingContent, List<ColumnInfo> newFields, List<String> customCodes) {
        // 移除标记内的代码（稍后会重新添加）
        String content = CodeMarkerUtil.removeCustomCode(existingContent);
        
        // 找到最后一个字段的位置（在类结束前）
        int lastFieldIndex = findLastFieldIndex(content);
        
        if (lastFieldIndex < 0) {
            // 如果找不到字段位置，在类开始后添加
            lastFieldIndex = content.indexOf('{') + 1;
        }
        
        // 生成新字段代码
        StringBuilder newFieldsCode = new StringBuilder();
        for (ColumnInfo field : newFields) {
            newFieldsCode.append("\n    /**\n");
            if (field.getComment() != null && !field.getComment().isEmpty()) {
                newFieldsCode.append("     * ").append(field.getComment()).append("\n");
            } else {
                newFieldsCode.append("     * ").append(field.getPropertyName()).append("\n");
            }
            newFieldsCode.append("     */\n");
            newFieldsCode.append("    private ").append(field.getJavaType()).append(" ")
                    .append(field.getPropertyName()).append(";\n");
        }
        
        // 插入新字段
        String before = content.substring(0, lastFieldIndex);
        String after = content.substring(lastFieldIndex);
        
        StringBuilder result = new StringBuilder(before);
        result.append(newFieldsCode);
        result.append(after);
        
        // 重新添加自定义代码
        if (!customCodes.isEmpty()) {
            String customCode = String.join("\n    ", customCodes);
            result = new StringBuilder(CodeMarkerUtil.appendCustomCode(result.toString(), customCode));
        }
        
        return result.toString();
    }
    
    /**
     * 找到最后一个字段的位置
     */
    private int findLastFieldIndex(String content) {
        // 查找最后一个 private 字段定义
        int lastIndex = -1;
        int currentIndex = 0;
        
        while (true) {
            int privateIndex = content.indexOf("private ", currentIndex);
            if (privateIndex < 0) {
                break;
            }
            
            // 检查是否是字段定义（不是方法）
            int nextSemicolon = content.indexOf(';', privateIndex);
            int nextBrace = content.indexOf('{', privateIndex);
            
            if (nextSemicolon > 0 && (nextBrace < 0 || nextSemicolon < nextBrace)) {
                lastIndex = nextSemicolon + 1;
                currentIndex = nextSemicolon + 1;
            } else {
                break;
            }
        }
        
        return lastIndex;
    }
}
