# 登录页面背景图片

## 使用说明

### 方式一：使用图片背景（推荐）

1. **准备背景图片**
   - 图片名称：`login-bg.jpg`（或 `login-bg.png`）
   - 建议尺寸：750px × 1334px（iPhone 6/7/8尺寸）或更大
   - 图片内容：零食、美食或温馨的背景图
   - 文件大小：建议不超过500KB

2. **放置图片**
   - 将图片放置在此文件夹下（`/images/banners/login-bg.jpg`）

3. **启用图片背景**
   - 打开 `/pages/login/login.wxss` 文件
   - 找到 `.login-container` 样式
   - 将 `background-color: #f5f7fa;` 替换为：
     ```css
     background-image: url('/images/banners/login-bg.jpg');
     background-size: cover;
     background-position: center;
     ```
   - 同时修改 `.login-container::before` 为半透明遮罩：
     ```css
     .login-container::before {
       content: '';
       position: absolute;
       top: 0;
       left: 0;
       right: 0;
       bottom: 0;
       background: rgba(0, 0, 0, 0.3);
       z-index: 0;
     }
     ```

### 方式二：使用纯色 + 装饰图案（当前）

当前使用浅色背景 + 顶部紫色装饰图案，提供简洁现代的视觉效果。

## 推荐图片资源

可以从以下网站下载免费的零食/美食背景图：
- Unsplash: https://unsplash.com/s/photos/snacks
- Pexels: https://www.pexels.com/search/candy/
- Pixabay: https://pixabay.com/images/search/snacks/

## 推荐图片风格

- ✅ 零食、糖果、饼干等美食特写
- ✅ 温馨的零食商店场景
- ✅ 色彩温暖柔和的图片
- ❌ 避免过于复杂的图片（影响文字可读性）
- ❌ 避免色彩过于鲜艳刺眼的图片

