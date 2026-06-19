# 数据库迁移指南

## 添加password字段到用户表

### 适用场景

如果您已经使用旧版本的SQL脚本创建了数据库，需要添加`password`字段到`t_user`表中。

### 迁移步骤

#### 方式一：使用迁移脚本（推荐）

```bash
# 连接到MySQL数据库
mysql -u root -p

# 执行迁移脚本
source src/main/resources/sql/migration_add_password.sql
```

#### 方式二：手动执行SQL

```sql
USE `bar`;

-- 添加password字段
ALTER TABLE `t_user` 
ADD COLUMN `password` VARCHAR(100) DEFAULT NULL COMMENT '密码（MD5加密）' AFTER `phone`;

-- 添加phone索引
ALTER TABLE `t_user` 
ADD INDEX `idx_phone` (`phone`);
```

#### 方式三：使用MySQL客户端工具

1. 打开Navicat、DBeaver或其他MySQL客户端工具
2. 连接到`bar`数据库
3. 找到`t_user`表
4. 添加新字段：
   - 字段名：`password`
   - 类型：`VARCHAR(100)`
   - 默认值：`NULL`
   - 注释：密码（MD5加密）
   - 位置：在`phone`字段之后

### 验证迁移

执行以下SQL验证字段是否添加成功：

```sql
USE `bar`;

-- 查看表结构
DESC t_user;

-- 应该看到password字段
-- +-------------+--------------+------+-----+---------+----------------+
-- | Field       | Type         | Null | Key | Default | Extra          |
-- +-------------+--------------+------+-----+---------+----------------+
-- | id          | bigint(20)   | NO   | PRI | NULL    | auto_increment |
-- | openid      | varchar(100) | YES  | UNI | NULL    |                |
-- | nickname    | varchar(50)  | YES  |     | NULL    |                |
-- | avatar      | varchar(255) | YES  |     | NULL    |                |
-- | phone       | varchar(20)  | YES  | MUL | NULL    |                |
-- | password    | varchar(100) | YES  |     | NULL    |                | ← 新增字段
-- | gender      | tinyint(1)   | YES  |     | 0       |                |
-- | ...         | ...          | ...  | ... | ...     | ...            |
-- +-------------+--------------+------+-----+---------+----------------+
```

### 注意事项

1. **备份数据库**：在执行迁移前，请先备份数据库
   ```bash
   mysqldump -u root -p bar > bar_backup.sql
   ```

2. **生产环境**：如果是生产环境，建议在非高峰期执行迁移

3. **密码加密**：用户密码使用MD5加密存储

4. **兼容性**：此迁移脚本使用了`IF NOT EXISTS`语法，可以安全地重复执行

### 相关变更

- 数据库表：`t_user`
- 实体类：`com.flowerstore.entity.User`
- 影响功能：
  - ✅ 用户密码登录
  - ✅ 用户注册
  - ✅ 密码修改

### 回滚方式

如需回滚此次迁移：

```sql
USE `bar`;

-- 删除password字段
ALTER TABLE `t_user` DROP COLUMN `password`;
```

⚠️ **警告**：回滚操作会删除所有用户的密码数据，请谨慎操作！

