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
/**app.wxss - 梭哈酒馆 设计系统**/
page {
    /* 品牌金 */
    --gold: #e8c547;
    --gold-light: #f7dc8a;
    --gold-dark: #c99a3a;
    --gold-gradient: linear-gradient(135deg, #f7dc8a 0%, #d4a72c 100%);

    /* 背景层级 */
    --bg-page: #0b0b0c;
    --bg-card: #1c1c1e;
    --bg-card-gradient: linear-gradient(145deg, #202022, #18181a);
    --bg-elevated: #2a2a2d;
    --bg-sunken: #141416;

    /* 文字层级 */
    --text-primary: #f5f5f5;
    --text-regular: #d0d0d2;
    --text-muted: #8a8a8e;
    --text-faint: #6b6b70;
    --text-secondary: #8a8a8e;

    /* 描边 */
    --border-subtle: rgba(255, 255, 255, 0.07);
    --border-gold: rgba(232, 197, 71, 0.15);

    /* 语义色 */
    --danger: #e5484d;
    --success: #30a46c;

    /* 兼容旧变量名 */
    --primary-color: #e8c547;
    --secondary-color: #c41e3a;
    --text-color: #f5f5f5;
    --price-color: #e8c547;
    --light-gray: #1a1a1a;
    --border-color: #333333;
    --disabled-color: #6b6b70;
    --bg-dark: #0b0b0c;
    --accent-gold: #e8c547;

    font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
    font-size: 14px;
    line-height: 1.5;
    color: var(--text-primary);
    background-color: var(--bg-page);
    box-sizing: border-box;
}

/* 常用颜色类 */
.text-primary {
    color: var(--gold);
}
.text-secondary {
    color: var(--text-muted);
}
.bg-primary {
    background: var(--gold-gradient);
}
.bg-secondary {
    background-color: var(--secondary-color);
}

/* 页面容器 */
.page-dark {
    min-height: 100vh;
    background-color: var(--bg-page);
    box-sizing: border-box;
}

/* 区块标题：左侧金色竖条 */
.section-head {
    position: relative;
    padding: 4rpx 0 24rpx 22rpx;
    color: var(--text-primary);
    font-size: 30rpx;
    font-weight: 600;
    letter-spacing: 1rpx;
}

.section-head::before {
    content: '';
    position: absolute;
    left: 0;
    top: 7rpx;
    width: 6rpx;
    height: 30rpx;
    border-radius: 4rpx;
    background: linear-gradient(180deg, var(--gold-light), var(--gold-dark));
}

/* 金色图标徽章：统一替代彩色 emoji */
.g-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    box-sizing: border-box;
    width: 76rpx;
    height: 76rpx;
    border-radius: 50%;
    border: 1rpx solid rgba(232, 197, 71, 0.34);
    background: radial-gradient(circle at 32% 26%, rgba(232, 197, 71, 0.2), rgba(232, 197, 71, 0.05));
    color: var(--gold-light);
    font-size: 28rpx;
    font-weight: 700;
    line-height: 1;
}

.g-icon-lg {
    width: 96rpx;
    height: 96rpx;
    font-size: 36rpx;
}

.g-icon-sm {
    width: 56rpx;
    height: 56rpx;
    font-size: 26rpx;
}

/* 购物袋图标（CSS 绘制，避免 emoji） */
.g-bag {
    position: relative;
    width: 34rpx;
    height: 30rpx;
    box-sizing: border-box;
    border: 3rpx solid currentColor;
    border-radius: 5rpx 5rpx 8rpx 8rpx;
}

.g-bag::before {
    content: '';
    position: absolute;
    top: -13rpx;
    left: 50%;
    transform: translateX(-50%);
    width: 18rpx;
    height: 14rpx;
    box-sizing: border-box;
    border: 3rpx solid currentColor;
    border-bottom: none;
    border-radius: 9rpx 9rpx 0 0;
}

/* 点按反馈 */
.g-tap {
    transition: transform 0.16s ease, opacity 0.16s ease;
}

.g-tap:active {
    transform: scale(0.97);
    opacity: 0.82;
}

/* 数字统一等宽，价格不跳动 */
.g-num {
    font-variant-numeric: tabular-nums;
    font-feature-settings: 'tnum';
    letter-spacing: 0;
}

/* 空状态 */
.empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 140rpx 40rpx;
}

.empty-state-icon {
    font-size: 28rpx;
    font-weight: 700;
    letter-spacing: 8rpx;
    color: var(--gold);
    opacity: 0.55;
    margin-bottom: 20rpx;
    padding: 18rpx 28rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.28);
    border-radius: 12rpx;
}

.empty-state-text {
    color: var(--text-muted);
    font-size: 28rpx;
}

.empty-state-tip {
    margin-top: 12rpx;
    color: var(--text-faint);
    font-size: 23rpx;
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
    color: var(--gold);
    font-weight: bold;
}

.price-original {
    color: var(--text-faint);
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
    font-weight: 600;
    border: none;
}

.btn::after {
    border: none;
}

.btn-primary {
    background: var(--gold-gradient);
    color: #171717;
    box-shadow: 0 8rpx 24rpx rgba(232, 197, 71, 0.22);
}

.btn-secondary {
    background-color: var(--bg-elevated);
    color: var(--text-regular);
}

.btn-outline {
    background: transparent;
    color: var(--gold);
    border: 2rpx solid var(--gold);
}

.btn-disabled {
    background: var(--bg-elevated);
    color: var(--text-faint);
    box-shadow: none;
}

.btn-block {
    width: 100%;
}

/* 卡片样式 */
.card {
    box-sizing: border-box;
    margin-bottom: 20rpx;
    padding: 28rpx;
    overflow: hidden;
    border: 1rpx solid var(--border-gold);
    border-radius: 22rpx;
    background: var(--bg-card-gradient);
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.24);
}

.card-header {
    padding-bottom: 22rpx;
    border-bottom: 1rpx solid var(--border-subtle);
    color: var(--text-primary);
    font-weight: 600;
}

.card-body {
    padding-top: 22rpx;
}

.card-footer {
    margin-top: 22rpx;
    padding-top: 22rpx;
    border-top: 1rpx solid var(--border-subtle);
}

/* 分割线 */
.divider {
    height: 1rpx;
    background-color: var(--border-subtle);
    margin: 20rpx 0;
}

/* 标签样式 */
.tag {
    display: inline-block;
    padding: 6rpx 16rpx;
    border-radius: 8rpx;
    font-size: 12px;
    margin-right: 10rpx;
}

.tag-primary {
    background-color: rgba(232, 197, 71, 0.12);
    border: 1rpx solid rgba(232, 197, 71, 0.35);
    color: var(--gold-light);
}

.tag-secondary {
    background-color: rgba(196, 30, 58, 0.12);
    border: 1rpx solid rgba(196, 30, 58, 0.35);
    color: #f08c9b;
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
