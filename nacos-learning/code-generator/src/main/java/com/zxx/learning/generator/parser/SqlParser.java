package com.zxx.learning.generator.parser;

import com.zxx.learning.generator.model.ColumnInfo;
import com.zxx.learning.generator.model.TableInfo;
import com.zxx.learning.generator.util.StringUtil;
import com.zxx.learning.generator.util.TypeConverter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL DDL 解析器
 * 解析 CREATE TABLE 语句，提取表结构信息
 * 
 * @author code-generator
 */
@Slf4j
public class SqlParser {
    
    // 匹配 CREATE TABLE 语句
    private static final Pattern CREATE_TABLE_PATTERN = Pattern.compile(
        "(?i)CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?[`']?([\\w]+)[`']?",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    // 匹配表注释
    private static final Pattern TABLE_COMMENT_PATTERN = Pattern.compile(
        "(?i)COMMENT\\s*=\\s*['\"]([^'\"]+)['\"]",
        Pattern.CASE_INSENSITIVE
    );
    
    // 匹配字段定义
    private static final Pattern COLUMN_PATTERN = Pattern.compile(
        "[`']?([\\w]+)[`']?\\s+([\\w()\\s,]+?)(?:\\s+NOT\\s+NULL)?(?:\\s+DEFAULT\\s+([^,\\s)]+))?(?:\\s+AUTO_INCREMENT)?(?:\\s+COMMENT\\s+['\"]([^'\"]+)['\"])?",
        Pattern.CASE_INSENSITIVE
    );
    
    // 匹配主键定义
    private static final Pattern PRIMARY_KEY_PATTERN = Pattern.compile(
        "(?i)PRIMARY\\s+KEY\\s*\\([`']?([\\w]+)[`']?\\)",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 解析 SQL 文件，提取所有表信息
     * 
     * @param sqlContent SQL 内容
     * @return 表信息列表
     */
    public List<TableInfo> parseTables(String sqlContent) {
        List<TableInfo> tables = new ArrayList<>();
        if (sqlContent == null || sqlContent.trim().isEmpty()) {
            return tables;
        }
        
        // 分割多个 CREATE TABLE 语句
        String[] createStatements = sqlContent.split("(?i)(?=CREATE\\s+TABLE)");
        
        for (String statement : createStatements) {
            statement = statement.trim();
            if (statement.isEmpty() || !statement.toUpperCase().startsWith("CREATE")) {
                continue;
            }
            
            TableInfo tableInfo = parseTable(statement);
            if (tableInfo != null) {
                tables.add(tableInfo);
            }
        }
        
        return tables;
    }
    
    /**
     * 解析单个 CREATE TABLE 语句
     * 
     * @param sql CREATE TABLE 语句
     * @return 表信息
     */
    public TableInfo parseTable(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return null;
        }
        
        TableInfo tableInfo = new TableInfo();
        
        // 提取表名
        Matcher tableMatcher = CREATE_TABLE_PATTERN.matcher(sql);
        if (!tableMatcher.find()) {
            log.warn("无法解析表名: {}", sql.substring(0, Math.min(100, sql.length())));
            return null;
        }
        
        String tableName = StringUtil.removeBackticks(tableMatcher.group(1));
        tableInfo.setTableName(tableName);
        tableInfo.setClassName(StringUtil.underlineToCamelUpper(tableName));
        tableInfo.setClassNameLower(StringUtil.uncapitalize(tableInfo.getClassName()));
        
        // 提取表注释
        Matcher commentMatcher = TABLE_COMMENT_PATTERN.matcher(sql);
        if (commentMatcher.find()) {
            tableInfo.setComment(commentMatcher.group(1));
        }
        
        // 提取主键
        String primaryKeyColumn = null;
        Matcher pkMatcher = PRIMARY_KEY_PATTERN.matcher(sql);
        if (pkMatcher.find()) {
            primaryKeyColumn = StringUtil.removeBackticks(pkMatcher.group(1));
        }
        
        // 提取字段信息
        List<ColumnInfo> columns = parseColumns(sql, primaryKeyColumn);
        tableInfo.setColumns(columns);
        
        // 设置主键列
        if (primaryKeyColumn != null) {
            for (ColumnInfo column : columns) {
                if (column.getColumnName().equals(primaryKeyColumn)) {
                    tableInfo.setPrimaryKey(column);
                    break;
                }
            }
        }
        
        return tableInfo;
    }
    
    /**
     * 解析字段信息
     * 
     * @param sql CREATE TABLE 语句
     * @param primaryKeyColumn 主键列名
     * @return 字段列表
     */
    private List<ColumnInfo> parseColumns(String sql, String primaryKeyColumn) {
        List<ColumnInfo> columns = new ArrayList<>();
        
        // 提取字段定义部分（在括号内）
        int startIndex = sql.indexOf('(');
        int endIndex = sql.lastIndexOf(')');
        if (startIndex < 0 || endIndex < 0 || startIndex >= endIndex) {
            return columns;
        }
        
        String columnSection = sql.substring(startIndex + 1, endIndex);
        
        // 按逗号分割字段定义（需要处理括号内的逗号）
        List<String> columnDefinitions = splitColumnDefinitions(columnSection);
        
        for (String columnDef : columnDefinitions) {
            columnDef = columnDef.trim();
            if (columnDef.isEmpty() || columnDef.toUpperCase().startsWith("PRIMARY KEY") 
                || columnDef.toUpperCase().startsWith("KEY") 
                || columnDef.toUpperCase().startsWith("INDEX")
                || columnDef.toUpperCase().startsWith("UNIQUE")
                || columnDef.toUpperCase().startsWith("FOREIGN KEY")
                || columnDef.toUpperCase().startsWith("CONSTRAINT")) {
                continue;
            }
            
            ColumnInfo column = parseColumn(columnDef, primaryKeyColumn);
            if (column != null) {
                columns.add(column);
            }
        }
        
        return columns;
    }
    
    /**
     * 分割字段定义（处理括号内的逗号）
     */
    private List<String> splitColumnDefinitions(String columnSection) {
        List<String> definitions = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int parenDepth = 0;
        
        for (char c : columnSection.toCharArray()) {
            if (c == '(') {
                parenDepth++;
                current.append(c);
            } else if (c == ')') {
                parenDepth--;
                current.append(c);
            } else if (c == ',' && parenDepth == 0) {
                definitions.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        if (current.length() > 0) {
            definitions.add(current.toString().trim());
        }
        
        return definitions;
    }
    
    /**
     * 解析单个字段定义
     * 
     * @param columnDef 字段定义字符串
     * @param primaryKeyColumn 主键列名
     * @return 字段信息
     */
    private ColumnInfo parseColumn(String columnDef, String primaryKeyColumn) {
        if (columnDef == null || columnDef.trim().isEmpty()) {
            return null;
        }
        
        ColumnInfo column = new ColumnInfo();
        
        // 移除反引号
        columnDef = columnDef.replace("`", "");
        
        // 提取字段名（第一个单词）
        String[] parts = columnDef.trim().split("\\s+");
        if (parts.length < 2) {
            return null;
        }
        
        String columnName = parts[0].trim();
        column.setColumnName(columnName);
        column.setPropertyName(StringUtil.underlineToCamel(columnName));
        
        // 判断是否主键
        boolean isPrimaryKey = columnName.equals(primaryKeyColumn);
        column.setPrimaryKey(isPrimaryKey);
        
        // 提取数据类型
        String dataTypePart = parts[1].trim();
        String dataType = dataTypePart;
        String length = null;
        
        // 提取长度信息，如 VARCHAR(50)
        int parenIndex = dataTypePart.indexOf('(');
        if (parenIndex > 0) {
            dataType = dataTypePart.substring(0, parenIndex).trim();
            int closeParen = dataTypePart.indexOf(')', parenIndex);
            if (closeParen > parenIndex) {
                length = dataTypePart.substring(parenIndex + 1, closeParen).trim();
            }
        }
        
        column.setDataType(dataType.toUpperCase());
        column.setLength(length);
        column.setJavaType(TypeConverter.mysqlToJava(dataType));
        
        // 检查 NOT NULL
        boolean notNull = columnDef.toUpperCase().contains("NOT NULL");
        column.setNullable(!notNull);
        
        // 检查 AUTO_INCREMENT
        boolean autoIncrement = columnDef.toUpperCase().contains("AUTO_INCREMENT");
        column.setAutoIncrement(autoIncrement);
        if (autoIncrement && isPrimaryKey) {
            // 自增主键通常使用 IdType.AUTO
        }
        
        // 提取默认值
        Pattern defaultPattern = Pattern.compile("(?i)DEFAULT\\s+([^,\\s)]+)");
        Matcher defaultMatcher = defaultPattern.matcher(columnDef);
        if (defaultMatcher.find()) {
            String defaultValue = defaultMatcher.group(1).trim();
            defaultValue = StringUtil.removeQuotes(defaultValue);
            column.setDefaultValue(defaultValue);
        }
        
        // 提取注释
        Pattern commentPattern = Pattern.compile("(?i)COMMENT\\s+['\"]([^'\"]+)['\"]");
        Matcher commentMatcher = commentPattern.matcher(columnDef);
        if (commentMatcher.find()) {
            column.setComment(commentMatcher.group(1));
        }
        
        // 判断是否在 INSERT/UPDATE 时忽略（如 create_time、update_time 等）
        String lowerColumnName = columnName.toLowerCase();
        if (lowerColumnName.contains("create_time") || lowerColumnName.contains("createTime")) {
            column.setUpdateIgnore(true);
        }
        if (lowerColumnName.contains("update_time") || lowerColumnName.contains("updateTime")) {
            column.setInsertIgnore(true);
        }
        
        return column;
    }
}
