# 鲜花店管理系统

基于 Vue3 + Element Plus 开发的鲜花店后台管理系统。

## 技术栈

- Vue 3.3
- Vue Router 4.2
- Pinia 2.1
- Element Plus 2.3
- Axios
- Vite 4.4

## 功能特点

### 1. 用户认证
- 管理员登录
- Token认证
- 自动登录

### 2. 首页看板
- 订单统计（待付款、待发货、已发货、已完成）
- WebSocket实时接收新订单通知
- 新订单语音播报（使用浏览器TTS）

### 3. 分类管理
- 分类列表（分页）
- 添加/编辑分类
- 删除分类
- 分类状态管理

### 4. 商品管理
- 商品列表（分页、搜索）
- 添加/编辑商品
- 删除商品
- 商品状态管理（上架/下架）
- 商品分类关联

### 5. 订单管理
- 订单列表（分页、状态筛选）
- 订单详情查看
- 订单发货
- 订单完成
- 订单状态更新

### 6. 文件上传
- 图片上传
- 支持多种图片格式

## 项目结构

```
flower-store-admin/
├── index.html                 # HTML入口
├── package.json              # 依赖配置
├── vite.config.js            # Vite配置
└── src/
    ├── main.js               # 入口文件
    ├── App.vue               # 根组件
    ├── api/                  # API接口
    │   ├── request.js       # Axios封装
    │   ├── auth.js          # 认证接口
    │   ├── category.js      # 分类接口
    │   ├── product.js       # 商品接口
    │   ├── order.js         # 订单接口
    │   └── file.js          # 文件接口
    ├── router/               # 路由配置
    │   └── index.js
    ├── stores/               # Pinia状态管理
    │   └── user.js          # 用户状态
    ├── layouts/              # 布局组件
    │   └── MainLayout.vue   # 主布局
    └── views/                # 页面组件
        ├── Login.vue         # 登录页
        ├── Dashboard.vue     # 首页看板
        ├── Settings.vue      # 系统设置
        ├── category/         # 分类管理
        │   └── CategoryList.vue
        ├── product/          # 商品管理
        │   └── ProductList.vue
        ├── order/            # 订单管理
        │   ├── OrderList.vue
        │   └── OrderDetail.vue
        └── user/             # 用户管理
            └── UserList.vue
```

## 安装依赖

```bash
npm install
```

## 开发运行

```bash
npm run dev
```

访问地址：http://localhost:3000

## 生产构建

```bash
npm run build
```

## 默认账号

- 用户名：admin
- 密码：123456

## 主要功能说明

### WebSocket订单通知

系统会自动连接后端WebSocket服务，实时接收新订单通知。

当有新订单时：
- 显示桌面通知
- 播放语音提示
- 自动刷新订单统计

### 文件上传

支持图片文件上传到后端服务器，上传后返回访问路径。

### 响应式设计

界面采用Element Plus组件库，支持各种屏幕尺寸。

## 注意事项

1. 需要先启动后端服务
2. 确保后端接口地址配置正确（vite.config.js中的proxy配置）
3. WebSocket地址需要根据实际后端地址修改
4. 浏览器需要支持TTS（Text-to-Speech）API才能使用语音播报功能

## 开发建议

1. API请求统一使用`src/api`目录下的接口函数
2. 状态管理使用Pinia
3. 路由配置在`src/router/index.js`
4. 页面组件放在`src/views`目录下
5. 公共组件可放在`src/components`目录下

