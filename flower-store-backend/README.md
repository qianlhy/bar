# 鲜花店后端系统

基于 Spring Boot 2.7.18 + MySQL + MyBatis-Plus 开发的鲜花店后端系统。

## 技术栈

- Spring Boot 2.7.18
- MySQL 8.0+
- MyBatis-Plus 3.5.3.1
- Druid 数据库连接池
- JWT 认证
- FastJSON 2
- WebSocket（订单通知）
- Hutool 工具库

## 项目结构

```
flower-store-backend/
├── src/main/java/com/flowerstore/
│   ├── FlowerStoreApplication.java     # 启动类
│   ├── common/                         # 通用类
│   │   ├── Result.java                # 统一返回结果
│   │   └── PageResult.java            # 分页结果
│   ├── config/                         # 配置类
│   │   ├── CorsConfig.java            # 跨域配置
│   │   ├── MyBatisPlusConfig.java     # MyBatis Plus配置
│   │   ├── MetaObjectHandlerConfig.java # 自动填充配置
│   │   ├── WebMvcConfig.java          # Web MVC配置
│   │   └── WebSocketConfig.java       # WebSocket配置
│   ├── controller/                     # 控制器
│   │   ├── AuthController.java        # 认证控制器
│   │   ├── CategoryController.java    # 分类控制器
│   │   ├── ProductController.java     # 商品控制器
│   │   ├── OrderController.java       # 订单控制器
│   │   ├── AddressController.java     # 地址控制器
│   │   ├── CartController.java        # 购物车控制器
│   │   ├── FavoriteController.java    # 收藏控制器
│   │   └── FileController.java        # 文件控制器
│   ├── entity/                         # 实体类
│   │   ├── User.java                  # 用户
│   │   ├── Admin.java                 # 管理员
│   │   ├── Category.java              # 分类
│   │   ├── Product.java               # 商品
│   │   ├── Order.java                 # 订单
│   │   ├── OrderItem.java             # 订单明细
│   │   ├── Address.java               # 地址
│   │   ├── Cart.java                  # 购物车
│   │   └── Favorite.java              # 收藏
│   ├── mapper/                         # Mapper接口
│   ├── service/                        # 服务层
│   │   ├── AuthService.java           # 认证服务
│   │   ├── CategoryService.java       # 分类服务
│   │   ├── ProductService.java        # 商品服务
│   │   ├── OrderService.java          # 订单服务
│   │   ├── AddressService.java        # 地址服务
│   │   ├── CartService.java           # 购物车服务
│   │   ├── FavoriteService.java       # 收藏服务
│   │   └── FileService.java           # 文件服务
│   ├── util/                           # 工具类
│   │   ├── JwtUtils.java              # JWT工具
│   │   ├── WeChatUtils.java           # 微信工具
│   │   └── MD5Utils.java              # MD5工具
│   └── websocket/                      # WebSocket
│       └── OrderNotificationWebSocket.java  # 订单通知
├── src/main/resources/
│   ├── application.yml                 # 配置文件
│   └── sql/
│       └── init.sql            # 数据库脚本
└── pom.xml                             # Maven配置

```

## 功能特点

### 1. 用户认证
- 微信小程序登录（通过code获取openid）
- 管理员登录
- JWT Token认证

### 2. 商品管理
- 商品分类管理
- 商品CRUD操作
- 商品搜索
- 热门商品、新品推荐

### 3. 订单管理
- 订单创建
- 订单状态更新（待付款、待发货、已发货、已完成、已取消）
- 订单查询（分页、按状态筛选）
- 订单统计

### 4. 地址管理
- 收货地址CRUD
- 默认地址设置

### 5. 购物车
- 添加商品到购物车
- 更新商品数量
- 删除商品
- 清空购物车

### 6. 收藏功能
- 收藏商品
- 取消收藏
- 查看收藏列表

### 7. 文件上传
- 支持本地文件存储
- 可配置上传路径
- 按日期自动创建子目录

### 8. 订单通知
- WebSocket实时通知
- 新订单语音播报提示

## 配置说明

### 1. 数据库配置
修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bar
    username: root
    password: root
```

### 2. 微信小程序配置
修改 `application.yml` 中的微信配置：
```yaml
wechat:
  appid: your_appid_here
  secret: your_secret_here
```

### 3. 文件上传配置
修改 `application.yml` 中的文件上传配置：
```yaml
file:
  upload:
    path: ./uploads/  # 上传文件保存路径
```

## 部署步骤

### 1. 创建数据库
执行 `src/main/resources/sql/init.sql` 脚本创建数据库和表。

### 2. 修改配置
根据实际情况修改 `application.yml` 配置文件。

### 3. 编译打包
```bash
mvn clean package
```

### 4. 运行项目
```bash
java -jar target/flower-store-backend-1.0.0.jar
```

或者使用IDE直接运行 `FlowerStoreApplication` 主类。

### 5. 访问接口
- 本地接口地址：http://localhost:8080/api
- 线上接口地址：https://bar.twst.work/api
- 线上WebSocket地址：wss://bar.twst.work/api/ws/order/notification

## 默认账号

- 管理员账号：admin
- 管理员密码：123456

## API文档

### 认证接口
- POST `/auth/wx/login` - 微信小程序登录
- POST `/auth/admin/login` - 管理员登录

### 商品接口
- GET `/product/page` - 分页查询商品（管理端）
- GET `/product/list` - 商品列表（小程序端）
- GET `/product/{id}` - 商品详情
- POST `/product` - 添加商品
- PUT `/product` - 更新商品
- DELETE `/product/{id}` - 删除商品
- GET `/product/hot` - 热门商品
- GET `/product/new` - 新品商品
- GET `/product/search` - 搜索商品

### 订单接口
- GET `/order/page` - 分页查询订单（管理端）
- GET `/order/list` - 订单列表（小程序端）
- GET `/order/{id}` - 订单详情
- POST `/order` - 创建订单
- PUT `/order/{id}/status` - 更新订单状态
- PUT `/order/{id}/cancel` - 取消订单
- GET `/order/statistics` - 订单统计

### 其他接口
- 分类、地址、购物车、收藏、文件上传等接口详见控制器代码

## 注意事项

1. 首次运行需要先执行数据库脚本
2. 需要配置正确的微信小程序AppID和Secret
3. 上传文件目录需要有写入权限
4. WebSocket连接需要支持ws协议

