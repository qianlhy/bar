# 鲜花店小程序 - 后端接口集成说明

## 概述

本文档说明如何将小程序前端从使用本地模拟数据切换到调用后端接口。

## 修改步骤

### 1. 配置后端接口地址

修改 `utils/request.js` 文件中的 `BASE_URL`：

```javascript
const BASE_URL = 'http://your-backend-domain.com/api' // 改为你的后端地址
```

**注意：** 
- 开发环境可以使用 `http://localhost:8080/api`
- 生产环境需要使用正式域名，且必须是 `https://` 协议
- 需要在微信小程序后台配置服务器域名白名单

### 2. 登录功能改造

原来的登录代码（`pages/login/login.js`）需要调用后端接口：

```javascript
const authApi = require('../../api/auth')

// 获取微信登录code
wx.login({
  success: res => {
    if (res.code) {
      // 调用后端登录接口
      authApi.wxLogin(res.code, userInfo.nickName, userInfo.avatarUrl)
        .then(data => {
          // 保存token和用户信息
          wx.setStorageSync('token', data.token)
          wx.setStorageSync('userInfo', data.userInfo)
          app.globalData.userInfo = data.userInfo
          app.globalData.isLoggedIn = true
          
          wx.showToast({
            title: '登录成功',
            icon: 'success'
          })
          
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        })
        .catch(err => {
          console.error(err)
        })
    }
  }
})
```

### 3. 首页数据获取

修改 `pages/index/index.js`：

```javascript
const categoryApi = require('../../api/category')
const productApi = require('../../api/product')

// 获取商品分类
getCategories: function() {
  categoryApi.getCategoryList().then(data => {
    this.setData({ categories: data })
  })
}

// 获取特惠商品（价格最低的商品）
getPromotions: function() {
  productApi.getProductList().then(data => {
    const promotions = data.sort((a, b) => a.price - b.price).slice(0, 6)
    this.setData({ promotions })
  })
}

// 获取推荐商品（热门商品）
getRecommendProducts: function() {
  productApi.getHotProducts(6).then(data => {
    this.setData({ recommendProducts: data })
  })
}

// 获取新品上市
getNewProducts: function() {
  productApi.getNewProducts(4).then(data => {
    this.setData({ newProducts: data })
  })
}
```

### 4. 商品详情页

修改 `pages/product/detail.js`：

```javascript
const productApi = require('../../api/product')
const favoriteApi = require('../../api/favorite')

// 加载商品详情
loadProductDetail: function(productId) {
  this.setData({ isLoading: true })
  
  productApi.getProductById(productId).then(product => {
    // 检查是否已收藏
    return favoriteApi.checkFavorite(productId).then(isFavorite => {
      this.setData({
        product,
        isFavorite,
        isLoading: false
      })
    })
  })
}

// 收藏商品
collectProduct: function() {
  const { product, isFavorite } = this.data
  
  if (!app.globalData.isLoggedIn) {
    // 跳转登录
    wx.navigateTo({ url: '/pages/login/login' })
    return
  }
  
  if (isFavorite) {
    // 取消收藏
    favoriteApi.removeFavorite(product.id).then(() => {
      this.setData({ isFavorite: false })
      wx.showToast({ title: '已取消收藏', icon: 'success' })
    })
  } else {
    // 添加收藏
    favoriteApi.addFavorite(product.id).then(() => {
      this.setData({ isFavorite: true })
      wx.showToast({ title: '收藏成功', icon: 'success' })
    })
  }
}
```

### 5. 购物车页面

修改 `pages/cart/cart.js`：

```javascript
const cartApi = require('../../api/cart')

// 获取购物车数据
getCartData: function() {
  cartApi.getCartList().then(data => {
    const cartList = data.map(item => ({
      ...item,
      selected: true
    }))
    
    const { totalPrice, totalCount } = this.calculateTotal(cartList)
    
    this.setData({
      cartList,
      totalPrice,
      totalCount,
      isEmpty: cartList.length === 0
    })
  })
}

// 修改商品数量
updateCount: function(id, count) {
  cartApi.updateCartCount(id, count).then(() => {
    this.getCartData()
  })
}

// 删除商品
deleteItem: function(id) {
  cartApi.deleteCartItem(id).then(() => {
    this.getCartData()
  })
}
```

### 6. 订单创建

修改 `pages/order/order.js`：

```javascript
const orderApi = require('../../api/order')
const addressApi = require('../../api/address')

// 获取默认地址
getDefaultAddress: function() {
  addressApi.getDefaultAddress().then(address => {
    if (address) {
      this.setData({
        selectedAddress: address,
        hasAddress: true
      })
    }
  })
}

// 提交订单
submitOrder: function() {
  const { selectedAddress, orderItems, paymentMethod, remark } = this.data
  
  if (!selectedAddress) {
    wx.showToast({ title: '请选择收货地址', icon: 'none' })
    return
  }
  
  const orderData = {
    receiverName: selectedAddress.name,
    receiverPhone: selectedAddress.phone,
    province: selectedAddress.province,
    city: selectedAddress.city,
    district: selectedAddress.district,
    address: selectedAddress.address,
    paymentMethod: paymentMethod,
    remark: remark,
    items: orderItems.map(item => ({
      productId: item.id,
      productName: item.name,
      productImage: item.image,
      price: item.price,
      count: item.count
    }))
  }
  
  this.setData({ isLoading: true })
  
  orderApi.createOrder(orderData).then(order => {
    this.setData({ isLoading: false })
    // 跳转到支付页面
    wx.navigateTo({
      url: `/pages/order/payment?id=${order.id}&amount=${order.actualPayment}`
    })
  }).catch(() => {
    this.setData({ isLoading: false })
  })
}
```

### 7. 订单列表

修改 `pages/order/list.js`：

```javascript
const orderApi = require('../../api/order')

// 获取订单列表
getOrderList: function(status) {
  orderApi.getOrderList(status).then(data => {
    this.setData({ orders: data })
  })
}
```

### 8. 地址管理

修改 `pages/address/list.js` 和 `pages/address/edit.js`：

```javascript
const addressApi = require('../../api/address')

// 获取地址列表
getAddressList: function() {
  addressApi.getAddressList().then(data => {
    this.setData({ addressList: data })
  })
}

// 保存地址
saveAddress: function() {
  const addressData = { ...this.data }
  
  if (this.data.isEdit) {
    addressApi.updateAddress(addressData).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    })
  } else {
    addressApi.addAddress(addressData).then(() => {
      wx.showToast({ title: '保存成功', icon: 'success' })
      setTimeout(() => wx.navigateBack(), 1500)
    })
  }
}

// 删除地址
deleteAddress: function(id) {
  addressApi.deleteAddress(id).then(() => {
    wx.showToast({ title: '删除成功', icon: 'success' })
    this.getAddressList()
  })
}
```

## 注意事项

### 1. Token管理

所有需要认证的接口都会自动带上token（在 `utils/request.js` 中处理）。登录成功后需要保存token：

```javascript
wx.setStorageSync('token', data.token)
```

### 2. 错误处理

接口调用失败时会自动显示错误提示（在 `utils/request.js` 中处理）。

### 3. 图片地址

- 如果使用后端上传的图片，图片URL需要拼接完整的服务器地址
- 如果图片URL是相对路径，需要在显示时拼接：`BASE_URL.replace('/api', '') + imageUrl`

### 4. 数据格式

后端返回的数据格式与小程序中使用的格式可能略有差异，需要注意：
- 金额：后端使用 `BigDecimal`，前端获取时自动转为数字
- 时间：后端使用 `LocalDateTime`，格式为 `yyyy-MM-dd HH:mm:ss`
- 图片：后端可能返回单个图片URL或多个图片URL（逗号分隔）

### 5. 微信小程序配置

需要在微信小程序后台配置：

1. **服务器域名**
   - 进入微信小程序后台
   - 开发 -> 开发管理 -> 开发设置 -> 服务器域名
   - 配置 request 合法域名、uploadFile 合法域名

2. **不校验合法域名**（仅开发阶段）
   - 微信开发者工具 -> 详情 -> 本地设置
   - 勾选"不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书"

## 调试建议

1. 使用微信开发者工具的 Network 面板查看接口请求和响应
2. 在 `utils/request.js` 中添加日志输出，方便调试
3. 先确保后端接口正常运行再进行前端调用
4. 可以先用 Postman 等工具测试后端接口

## 完整示例

参考 `api/` 目录下的所有接口文件，已经封装了所有常用接口。在页面中使用时只需引入对应的API文件即可。

