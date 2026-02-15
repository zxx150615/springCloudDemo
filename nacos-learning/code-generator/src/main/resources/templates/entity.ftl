package ${packageEntity};

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
<#list importPackages as importPackage>
import ${importPackage};
</#list>

/**
 * ${comment!className}
 * 
 * @author code-generator
 */
@Data
@TableName("${tableName}")
public class ${className} {
<#list columns as column>
    
    /**
     * ${column.comment!column.propertyName}
     */
    <#if column.primaryKey>
    @TableId(type = IdType.AUTO)
    </#if>
    private ${column.javaType} ${column.propertyName};
</#list>
}
