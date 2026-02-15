package com.zxx.learning.generator;

import com.zxx.learning.generator.config.GeneratorConfig;
import com.zxx.learning.generator.model.TableInfo;
import com.zxx.learning.generator.parser.SqlParser;
import com.zxx.learning.generator.service.CodeGenerateService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 代码生成器主入口
 * 
 * 使用方式：
 * 1. 首次生成：
 *    java -jar code-generator.jar --sql-file=tables.sql --module=user --service-name=user-service
 * 
 * 2. 增量更新（只更新 Entity）：
 *    java -jar code-generator.jar --sql-file=tables.sql --module=user --service-name=user-service --update --update-entity-only
 * 
 * @author code-generator
 */
@Slf4j
public class CodeGenerator {
    
    public static void main(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            
            // 显示帮助信息
            if (cmd.hasOption("help") || cmd.hasOption("h")) {
                printHelp(options);
                return;
            }
            
            // 读取 SQL 文件
            String sqlFile = cmd.getOptionValue("sql-file");
            String sqlContent = cmd.getOptionValue("sql");
            
            if (sqlFile == null && sqlContent == null) {
                System.err.println("错误：必须提供 --sql-file 或 --sql 参数");
                printHelp(options);
                System.exit(1);
            }
            
            String sql = sqlContent;
            if (sqlFile != null) {
                File file = new File(sqlFile);
                if (!file.exists()) {
                    System.err.println("错误：SQL 文件不存在: " + sqlFile);
                    System.exit(1);
                }
                sql = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            }
            
            // 解析参数
            String module = cmd.getOptionValue("module");
            String serviceName = cmd.getOptionValue("service-name");
            boolean updateMode = cmd.hasOption("update");
            boolean updateEntityOnly = cmd.hasOption("update-entity-only");
            
            if (module == null || serviceName == null) {
                System.err.println("错误：必须提供 --module 和 --service-name 参数");
                printHelp(options);
                System.exit(1);
            }
            
            // 创建配置
            GeneratorConfig config = new GeneratorConfig();
            
            // 解析 SQL
            SqlParser sqlParser = new SqlParser();
            List<TableInfo> tables = sqlParser.parseTables(sql);
            
            if (tables.isEmpty()) {
                System.err.println("错误：未能从 SQL 中解析出任何表");
                System.exit(1);
            }
            
            // 创建代码生成服务
            CodeGenerateService codeGenerateService = new CodeGenerateService(config);
            
            // 生成代码
            for (TableInfo tableInfo : tables) {
                tableInfo.setModule(module);
                tableInfo.setServiceName(serviceName);
                
                System.out.println("正在生成代码: " + tableInfo.getClassName());
                codeGenerateService.generateCode(tableInfo, updateMode, updateEntityOnly);
            }
            
            System.out.println("代码生成完成！");
            
        } catch (ParseException e) {
            System.err.println("参数解析失败: " + e.getMessage());
            printHelp(options);
            System.exit(1);
        } catch (Exception e) {
            log.error("代码生成失败", e);
            System.err.println("代码生成失败: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * 创建命令行选项
     */
    private static Options createOptions() {
        Options options = new Options();
        
        options.addOption("h", "help", false, "显示帮助信息");
        options.addOption(Option.builder("f")
                .longOpt("sql-file")
                .hasArg()
                .argName("FILE")
                .desc("SQL 文件路径（包含 CREATE TABLE 语句）")
                .build());
        options.addOption(Option.builder("s")
                .longOpt("sql")
                .hasArg()
                .argName("SQL")
                .desc("直接提供 SQL 语句（CREATE TABLE）")
                .build());
        options.addOption(Option.builder("m")
                .longOpt("module")
                .hasArg()
                .argName("MODULE")
                .desc("模块名（如 user、order）")
                .required()
                .build());
        options.addOption(Option.builder("n")
                .longOpt("service-name")
                .hasArg()
                .argName("SERVICE")
                .desc("服务名（如 user-service、order-service）")
                .required()
                .build());
        options.addOption("u", "update", false, "增量更新模式（默认：首次生成）");
        options.addOption("e", "update-entity-only", false, "只更新 Entity 文件（增量更新时使用）");
        
        return options;
    }
    
    /**
     * 打印帮助信息
     */
    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("code-generator", "MySQL 表结构代码生成器", options, 
                "\n示例：\n" +
                "  首次生成：\n" +
                "    java -jar code-generator.jar --sql-file=tables.sql --module=user --service-name=user-service\n" +
                "  增量更新（只更新 Entity）：\n" +
                "    java -jar code-generator.jar --sql-file=tables.sql --module=user --service-name=user-service --update --update-entity-only\n",
                true);
    }
}
