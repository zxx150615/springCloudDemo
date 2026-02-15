package com.zxx.learning.generator.util;

import java.util.regex.Pattern;

/**
 * 字符串工具类
 * 
 * @author code-generator
 */
public class StringUtil {
    
    /**
     * 下划线转驼峰命名（首字母小写）
     * 例如：user_name -> userName
     */
    public static String underlineToCamel(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        str = str.toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean needUpperCase = false;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                needUpperCase = true;
            } else {
                if (needUpperCase) {
                    result.append(Character.toUpperCase(c));
                    needUpperCase = false;
                } else {
                    result.append(c);
                }
            }
        }
        
        return result.toString();
    }
    
    /**
     * 下划线转驼峰命名（首字母大写）
     * 例如：user_name -> UserName
     */
    public static String underlineToCamelUpper(String str) {
        String camel = underlineToCamel(str);
        if (camel == null || camel.isEmpty()) {
            return camel;
        }
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }
    
    /**
     * 驼峰转下划线
     * 例如：userName -> user_name
     */
    public static String camelToUnderline(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
    
    /**
     * 首字母大写
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 首字母小写
     */
    public static String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * 移除反引号（MySQL 表名和字段名可能带反引号）
     */
    public static String removeBackticks(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("`", "");
    }
    
    /**
     * 移除单引号和双引号
     */
    public static String removeQuotes(String str) {
        if (str == null) {
            return null;
        }
        return str.replace("'", "").replace("\"", "");
    }
}
