package com.zxx.learning.generator.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.zxx.learning.generator.model.ColumnInfo;
import com.zxx.learning.generator.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 代码解析器
 * 用于解析现有 Entity 文件，提取已有字段和方法（用于增量更新）
 * 
 * @author code-generator
 */
@Slf4j
public class JavaCodeParser {
    
    /**
     * 解析 Entity 文件，提取已有字段
     * 
     * @param entityFile Entity 文件路径
     * @return 已有字段列表
     */
    public List<ColumnInfo> parseEntityFields(File entityFile) {
        List<ColumnInfo> fields = new ArrayList<>();
        
        if (entityFile == null || !entityFile.exists()) {
            return fields;
        }
        
        try {
            CompilationUnit cu = JavaParser.parse(entityFile);
            
            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(FieldDeclaration field, Void arg) {
                    ColumnInfo columnInfo = new ColumnInfo();
                    
                    // 获取字段名
                    String fieldName = field.getVariables().get(0).getNameAsString();
                    columnInfo.setPropertyName(fieldName);
                    columnInfo.setColumnName(StringUtil.camelToUnderline(fieldName));
                    
                    // 获取字段类型
                    String typeName = field.getCommonType().asString();
                    columnInfo.setJavaType(typeName);
                    
                    // 获取注释
                    field.getJavadocComment().ifPresent(javadoc -> {
                        String comment = javadoc.getDescription().toText();
                        columnInfo.setComment(comment.trim());
                    });
                    
                    fields.add(columnInfo);
                }
            }, null);
            
        } catch (FileNotFoundException e) {
            log.error("无法找到 Entity 文件: {}", entityFile.getPath(), e);
        } catch (Exception e) {
            log.error("解析 Entity 文件失败: {}", entityFile.getPath(), e);
        }
        
        return fields;
    }
    
    /**
     * 提取自定义方法（标记外的）
     * 
     * @param entityFile Entity 文件路径
     * @return 自定义方法列表
     */
    public List<String> parseCustomMethods(File entityFile) {
        List<String> methods = new ArrayList<>();
        
        if (entityFile == null || !entityFile.exists()) {
            return methods;
        }
        
        try {
            CompilationUnit cu = JavaParser.parse(entityFile);
            
            cu.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(MethodDeclaration method, Void arg) {
                    // 排除 getter/setter 方法（Lombok 生成）
                    String methodName = method.getNameAsString();
                    if (!methodName.startsWith("get") && !methodName.startsWith("set") 
                        && !methodName.startsWith("is") && !methodName.equals("equals") 
                        && !methodName.equals("hashCode") && !methodName.equals("toString")) {
                        methods.add(method.toString());
                    }
                }
            }, null);
            
        } catch (Exception e) {
            log.error("解析自定义方法失败: {}", entityFile.getPath(), e);
        }
        
        return methods;
    }
    
    /**
     * 检查文件是否存在
     */
    public boolean entityFileExists(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }
}
