# 依赖更新说明

## JWT依赖问题修复

### 问题描述

在Java 11及以上版本中，`javax.xml.bind` 包已从JDK中移除，导致JWT库（jjwt 0.9.1）在运行时抛出以下异常：

```
java.lang.ClassNotFoundException: javax.xml.bind.DatatypeConverter
```

### 解决方案

在 `pom.xml` 中添加了JAXB相关依赖：

```xml
<!-- JAXB API (Java 11+ 需要) -->
<dependency>
    <groupId>javax.xml.bind</groupId>
    <artifactId>jaxb-api</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-impl</artifactId>
    <version>2.3.1</version>
</dependency>
<dependency>
    <groupId>com.sun.xml.bind</groupId>
    <artifactId>jaxb-core</artifactId>
    <version>2.3.0.1</version>
</dependency>
<dependency>
    <groupId>javax.activation</groupId>
    <artifactId>activation</artifactId>
    <version>1.1.1</version>
</dependency>
```

### 重新构建项目

1. **使用Maven重新构建**
   ```bash
   cd flower-store-backend
   mvn clean install
   ```

2. **或者在IDE中**
   - IDEA: 右键项目 → Maven → Reload Project
   - Eclipse: 右键项目 → Maven → Update Project

3. **重新启动应用**
   ```bash
   mvn spring-boot:run
   ```

### 验证

启动成功后，尝试登录功能，应该不再出现 `ClassNotFoundException` 错误。

### 影响范围

- ✅ 小程序密码登录
- ✅ 小程序微信登录
- ✅ 管理后台登录
- ✅ 所有需要JWT Token的接口

### 兼容性

- ✅ JDK 8
- ✅ JDK 11
- ✅ JDK 17
- ✅ JDK 21

## 其他说明

如果在其他环境（如JDK 8）中运行，这些依赖不会造成冲突，因为它们只是补充缺失的类库。

