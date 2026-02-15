# 基于 SQL DDL 的代码生成器

## 功能特性

1. **首次生成**：根据 CREATE TABLE 语句生成完整的三层代码
   - Entity（实体类）
   - Controller（控制器）
   - Service（服务接口）
   - ServiceImpl（服务实现）
   - Mapper（数据访问层）

2. **增量更新**：表新增字段时，智能合并新字段，保留已有代码和自定义方法

## 使用方式

### 1. 编译打包

```bash
cd code-generator
mvn clean package
```

打包后会生成 `target/code-generator-1.0-SNAPSHOT.jar`

### 2. 首次生成代码

```bash
java -jar target/code-generator-1.0-SNAPSHOT.jar \
  --sql-file=tables.sql \
  --module=user \
  --service-name=user-service
```

参数说明：
- `--sql-file`: SQL 文件路径（包含 CREATE TABLE 语句）
- `--module`: 模块名（如 user、order）
- `--service-name`: 服务名（如 user-service、order-service）

### 3. 增量更新（表新增字段后）

只更新 Entity 文件（推荐）：

```bash
java -jar target/code-generator-1.0-SNAPSHOT.jar \
  --sql-file=tables.sql \
  --module=user \
  --service-name=user-service \
  --update \
  --update-entity-only
```

更新所有文件：

```bash
java -jar target/code-generator-1.0-SNAPSHOT.jar \
  --sql-file=tables.sql \
  --module=user \
  --service-name=user-service \
  --update
```

### 4. 直接提供 SQL 语句

```bash
java -jar target/code-generator-1.0-SNAPSHOT.jar \
  --sql="CREATE TABLE user (id BIGINT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50))" \
  --module=user \
  --service-name=user-service
```

## SQL 示例

```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `age` INT COMMENT '年龄',
  `status` INT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) COMMENT='用户表';
```

## 增量更新示例

### 场景：user 表新增字段

**原始 SQL：**

```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100),
  PRIMARY KEY (`id`)
);
```

**更新后的 SQL（新增了 age 和 status 字段）：**

```sql
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100),
  `age` INT COMMENT '年龄',
  `status` INT DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`)
);
```

### 保留自定义代码

在 Entity 类中使用特殊标记来保留自定义代码：

```java
@Data
@TableName("user")
public class User {
    private Long id;
    private String username;
    private String email;
    
    // ========== CUSTOM CODE START ==========
    // 自定义方法
    public boolean isActive() {
        return status != null && status == 1;
    }
    // ========== CUSTOM CODE END ==========
}
```

增量更新时，标记内的代码会被保留。

## 生成的文件结构

```
nacos-common/src/main/java/com/zxx/learning/common/entity/
  └── User.java

user-service/src/main/java/com/zxx/learning/user/
  ├── controller/
  │   └── UserController.java
  ├── service/
  │   ├── UserService.java
  │   └── impl/
  │       └── UserServiceImpl.java
  └── mapper/
      └── UserMapper.java
```

## 技术栈

- **FreeMarker**: 模板引擎
- **MyBatis-Plus**: 数据访问框架
- **JavaParser**: Java 代码解析（用于增量更新）
- **Lombok**: 简化代码

## 注意事项

1. 增量更新时，建议只更新 Entity 文件（`--update-entity-only`），因为 Controller、Service、Mapper 使用 MyBatis-Plus，新字段会自动可用
2. 自定义代码必须放在标记内（`// ========== CUSTOM CODE START ==========` 和 `// ========== CUSTOM CODE END ==========`）才能被保留
3. SQL 文件可以包含多个 CREATE TABLE 语句，生成器会为每个表生成代码
