package ${packageController};

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${packageEntity}.${className};
import ${packageService}.${className}Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ${comment!className}控制器
 * API 路径: /${module}/**（Gateway 会通过 /api/${module}/** 访问）
 * 
 * @author code-generator
 */
@Slf4j
@RestController
@RequestMapping("/${module}")
public class ${className}Controller {
    
    @Autowired
    private ${className}Service ${classNameLower}Service;
    
    /**
     * 根据ID获取${comment!className}
     * GET /${module}/{id}
     */
    @GetMapping("/{id}")
    public ${className} getById(@PathVariable Long id) {
        log.info("根据ID获取${comment!className}，ID: {}", id);
        return ${classNameLower}Service.getById(id);
    }
    
    /**
     * 获取${comment!className}列表
     * GET /${module}/list
     */
    @GetMapping("/list")
    public List<${className}> list() {
        log.info("获取${comment!className}列表");
        return ${classNameLower}Service.list();
    }
    
    /**
     * 分页查询${comment!className}
     * GET /${module}/page?current=1&size=10
     */
    @GetMapping("/page")
    public Page<${className}> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size) {
        log.info("分页查询${comment!className}，current: {}, size: {}", current, size);
        return ${classNameLower}Service.page(new Page<>(current, size));
    }
    
    /**
     * 创建${comment!className}
     * POST /${module}
     */
    @PostMapping
    public boolean save(@RequestBody ${className} ${classNameLower}) {
        log.info("创建${comment!className}: {}", ${classNameLower});
        return ${classNameLower}Service.save(${classNameLower});
    }
    
    /**
     * 更新${comment!className}
     * PUT /${module}
     */
    @PutMapping
    public boolean updateById(@RequestBody ${className} ${classNameLower}) {
        log.info("更新${comment!className}: {}", ${classNameLower});
        return ${classNameLower}Service.updateById(${classNameLower});
    }
    
    /**
     * 删除${comment!className}
     * DELETE /${module}/{id}
     */
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        log.info("删除${comment!className}，ID: {}", id);
        return ${classNameLower}Service.removeById(id);
    }
}
