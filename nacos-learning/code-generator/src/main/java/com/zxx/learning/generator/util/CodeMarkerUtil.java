package com.zxx.learning.generator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码标记工具
 * 用于识别和保留标记内的自定义代码
 * 
 * @author code-generator
 */
public class CodeMarkerUtil {
    
    private static final String MARKER_START = "// ========== CUSTOM CODE START ==========";
    private static final String MARKER_END = "// ========== CUSTOM CODE END ==========";
    
    private static final Pattern MARKER_PATTERN = Pattern.compile(
        Pattern.quote(MARKER_START) + ".*?" + Pattern.quote(MARKER_END),
        Pattern.DOTALL
    );
    
    /**
     * 提取标记内的自定义代码
     * 
     * @param code 源代码
     * @return 自定义代码列表
     */
    public static List<String> extractCustomCode(String code) {
        List<String> customCodes = new ArrayList<>();
        if (code == null || code.isEmpty()) {
            return customCodes;
        }
        
        Matcher matcher = MARKER_PATTERN.matcher(code);
        while (matcher.find()) {
            String customCode = matcher.group();
            // 移除标记行，只保留代码内容
            customCode = customCode.replace(MARKER_START, "").replace(MARKER_END, "").trim();
            if (!customCode.isEmpty()) {
                customCodes.add(customCode);
            }
        }
        
        return customCodes;
    }
    
    /**
     * 移除标记内的代码（用于重新生成时）
     * 
     * @param code 源代码
     * @return 移除标记代码后的代码
     */
    public static String removeCustomCode(String code) {
        if (code == null || code.isEmpty()) {
            return code;
        }
        
        return MARKER_PATTERN.matcher(code).replaceAll("");
    }
    
    /**
     * 在代码末尾添加自定义代码标记
     * 
     * @param code 源代码
     * @param customCode 自定义代码
     * @return 添加标记后的代码
     */
    public static String appendCustomCode(String code, String customCode) {
        if (customCode == null || customCode.trim().isEmpty()) {
            return code;
        }
        
        StringBuilder sb = new StringBuilder(code);
        if (!code.trim().endsWith("}")) {
            sb.append("\n");
        }
        sb.append("\n    ").append(MARKER_START).append("\n");
        sb.append("    ").append(customCode).append("\n");
        sb.append("    ").append(MARKER_END);
        
        return sb.toString();
    }
    
    /**
     * 检查代码中是否包含自定义代码标记
     */
    public static boolean hasCustomCode(String code) {
        if (code == null || code.isEmpty()) {
            return false;
        }
        return code.contains(MARKER_START) && code.contains(MARKER_END);
    }
    
    /**
     * 获取标记开始字符串
     */
    public static String getMarkerStart() {
        return MARKER_START;
    }
    
    /**
     * 获取标记结束字符串
     */
    public static String getMarkerEnd() {
        return MARKER_END;
    }
}
