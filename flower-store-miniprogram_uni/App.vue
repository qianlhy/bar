<script>
// app.js
export default {
    data() {
        return {};
    },
    globalData: {
        userInfo: null,
        isLoggedIn: false,
        cart: [],
        mockProducts: [],
        mockCategories: [],
        mockAddresses: [],
        mockOrders: [],
        favorites: [],

        // 用户收藏的商品
        // 临时存储需要切换的分类ID
        tempCategoryId: null,

        // 初始化模拟数据（仅用于开发测试）
        initMockData: function () {
            // 初始化商品分类数据
            const categories = [
                {
                    id: 1,
                    name: '休闲零食',
                    icon: '/static/images/category/snack.png'
                },
                {
                    id: 2,
                    name: '坚果炒货',
                    icon: '/static/images/category/nuts.png'
                },
                {
                    id: 3,
                    name: '糖果巧克力',
                    icon: '/static/images/category/candy.png'
                },
                {
                    id: 4,
                    name: '饼干糕点',
                    icon: '/static/images/category/cookie.png'
                },
                {
                    id: 5,
                    name: '果冻布丁',
                    icon: '/static/images/category/jelly.png'
                },
                {
                    id: 6,
                    name: '膨化食品',
                    icon: '/static/images/category/chips.png'
                },
                {
                    id: 7,
                    name: '肉干肉脯',
                    icon: '/static/images/category/jerky.png'
                }
            ];

            // 存储模拟数据到全局变量（保留用于向后兼容）
            this.mockCategories = categories;
            this.mockProducts = [];
            this.mockOrders = [];

            // 将数据存储到本地
            uni.setStorageSync('mockCategories', categories);
        },

        // 登录方法（保存用户信息和token）
        login: function (userInfo) {
            this.userInfo = userInfo;
            this.isLoggedIn = true;

            // 存储到本地缓存
            uni.setStorageSync('userInfo', userInfo);
            uni.setStorageSync('isLoggedIn', true);
        },

        // 登出方法
        logout: function () {
            this.userInfo = null;
            this.isLoggedIn = false;

            // 清除本地缓存（保留token由API模块处理）
            uni.removeStorageSync('userInfo');
            uni.removeStorageSync('isLoggedIn');
            uni.removeStorageSync('token');
        }
    },
    onLaunch: function () {
        // 从本地存储中获取用户登录状态
        const userInfo = uni.getStorageSync('userInfo');
        const isLoggedIn = uni.getStorageSync('isLoggedIn');
        if (userInfo && isLoggedIn) {
            this.globalData.userInfo = userInfo;
            this.globalData.isLoggedIn = isLoggedIn;
        }

        // 从本地存储中获取购物车数据
        const cart = uni.getStorageSync('cart') || [];
        this.globalData.cart = cart;

        // 从本地存储中获取收藏数据
        const favorites = uni.getStorageSync('favorites') || [];
        this.globalData.favorites = favorites;

        // 初始化模拟数据
        this.globalData.initMockData();
    }
};
</script>
<style>
/**app.wxss**/
page {
    --primary-color: #e8c547;
    --secondary-color: #c41e3a;
    --text-color: #ffffff;
    --text-secondary: #999999;
    --price-color: #ffffff;
    --light-gray: #1a1a1a;
    --border-color: #333333;
    --disabled-color: #666666;
    --bg-dark: #000000;
    --bg-card: #1c1c1e;
    --accent-gold: #e8c547;

    font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
    font-size: 14px;
    line-height: 1.5;
    color: var(--text-color);
    background-color: var(--bg-dark);
    box-sizing: border-box;
}

/* 常用颜色类 */
.text-primary {
    color: var(--primary-color);
}
.text-secondary {
    color: var(--secondary-color);
}
.bg-primary {
    background-color: var(--primary-color);
}
.bg-secondary {
    background-color: var(--secondary-color);
}
.bg-white {
    background-color: #ffffff;
}

/* 常用布局类 */
.container {
    padding: 20rpx;
    box-sizing: border-box;
}

.flex {
    display: flex;
}

.flex-column {
    display: flex;
    flex-direction: column;
}

.flex-between {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.flex-center {
    display: flex;
    justify-content: center;
    align-items: center;
}

.flex-around {
    display: flex;
    justify-content: space-around;
    align-items: center;
}

/* 常用间距类 */
.mt-10 {
    margin-top: 10rpx;
}
.mt-20 {
    margin-top: 20rpx;
}
.mb-10 {
    margin-bottom: 10rpx;
}
.mb-20 {
    margin-bottom: 20rpx;
}

/* 常用文本类 */
.text-bold {
    font-weight: bold;
}
.text-center {
    text-align: center;
}
.text-right {
    text-align: right;
}

.text-ellipsis {
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}

.text-small {
    font-size: 12px;
}
.text-large {
    font-size: 16px;
}
.text-xl {
    font-size: 18px;
}

/* 价格样式 */
.price {
    color: var(--price-color);
    font-weight: bold;
}

.price-original {
    color: #999;
    text-decoration: line-through;
    font-size: 12px;
    margin-left: 10rpx;
}

/* 按钮样式 */
.btn {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20rpx 30rpx;
    border-radius: 40rpx;
    font-size: 14px;
    font-weight: bold;
}

.btn-primary {
    background-color: var(--primary-color);
    color: #ffffff;
    border: none;
}

.btn-secondary {
    background-color: var(--secondary-color);
    color: #ffffff;
    border: none;
}

.btn-outline {
    background-color: #ffffff;
    color: var(--primary-color);
    border: 1px solid var(--primary-color);
}

.btn-block {
    width: 100%;
}

/* 卡片样式 */
.card {
    background-color: #ffffff;
    border-radius: 16rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
    margin-bottom: 20rpx;
}

.card-header {
    padding: 24rpx;
    border-bottom: 1px solid var(--border-color);
    font-weight: bold;
}

.card-body {
    padding: 24rpx;
}

.card-footer {
    padding: 24rpx;
    border-top: 1px solid var(--border-color);
}

/* 分割线 */
.divider {
    height: 1px;
    background-color: var(--border-color);
    margin: 20rpx 0;
}

/* 标签样式 */
.tag {
    display: inline-block;
    padding: 4rpx 12rpx;
    border-radius: 8rpx;
    font-size: 12px;
    margin-right: 10rpx;
}

.tag-primary {
    background-color: rgba(255, 107, 107, 0.1);
    color: var(--primary-color);
}

.tag-secondary {
    background-color: rgba(78, 205, 196, 0.1);
    color: var(--secondary-color);
}

/* 网格布局 */
.grid {
    display: flex;
    flex-wrap: wrap;
}

.grid-item {
    box-sizing: border-box;
}

.grid-2 .grid-item {
    width: 50%;
}

.grid-3 .grid-item {
    width: 33.33%;
}

.grid-4 .grid-item {
    width: 25%;
}
</style>
