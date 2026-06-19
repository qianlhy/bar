<template>
    <!-- pages/cart/cart.wxml -->
    <view class="cart-container">
        <!-- 未登录提示 -->
        <view class="login-tip" v-if="!isLogin">
            <text>登录后才能同步购物车哦</text>
            <view class="login-btn" @tap="goToLogin">去登录</view>
        </view>

        <!-- 购物车为空 -->
        <view class="empty-cart" v-if="isEmpty">
            <image src="/static/images/icons/empty-cart.png" mode="aspectFit" class="empty-icon"></image>
            <text class="empty-text">购物车还是空的</text>
            <button class="go-shopping-btn" @tap="goToShopping">去逛逛</button>
        </view>

        <!-- 购物车商品列表 -->
        <view class="cart-list" v-if="!isEmpty">
            <view class="cart-item" v-for="(item, index) in cartList" :key="index">
                <!-- 选择框 -->

                <view class="select-box" @tap="toggleSelect" :data-index="index">
                    <view :class="'select-icon ' + (item.selected ? 'selected' : '')"></view>
                </view>

                <!-- 商品信息 -->

                <image class="product-image" :src="item.image" mode="aspectFill"></image>

                <view class="product-info">
                    <text class="product-name text-ellipsis">{{ item.name }}</text>
                    <view class="product-price-box">
                        <text class="price">¥{{ item.price }}</text>
                    </view>

                    <!-- 商品数量控制 -->
                    <view class="product-action">
                        <view class="counter">
                            <view :class="'counter-btn ' + (item.count <= 1 ? 'disabled' : '')" @tap="decreaseCount" :data-id="item.id" :data-count="item.count">-</view>
                            <view class="counter-num">{{ item.count }}</view>
                            <view
                                :class="'counter-btn ' + (item.count >= item.inventory ? 'disabled' : '')"
                                @tap="increaseCount"
                                :data-id="item.id"
                                :data-count="item.count"
                                :data-inventory="item.inventory"
                            >
                                +
                            </view>
                        </view>
                        <view class="delete-btn" @tap="deleteItem" :data-id="item.id">
                            <image src="/static/images/icons/delete.png" class="delete-icon"></image>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 底部结算栏 -->
        <view class="cart-footer" v-if="!isEmpty">
            <view class="select-all" @tap="toggleSelectAll">
                <view :class="'select-icon ' + (isAllSelected ? 'selected' : '')"></view>
                <text>全选</text>
            </view>
            <view class="cart-total">
                <view class="total-price">
                    <text class="price-label">合计：</text>
                    <text class="price">¥{{ totalPrice }}</text>
                </view>
                <text class="price-desc">不含运费</text>
            </view>
            <button class="checkout-btn" @tap="goToCheckout">结算({{ totalCount }})</button>
        </view>
    </view>
</template>

<script>
// pages/cart/cart.js
const app = getApp();
const cartApi = require('../../api/cart');
export default {
    data() {
        return {
            cartList: [],
            totalPrice: 0,
            totalCount: 0,
            isAllSelected: true,
            isEmpty: true,
            isLogin: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        // 检查登录状态
        this.checkLoginStatus();
    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        // 获取购物车数据
        this.checkLoginStatus();
        if (this.isLogin) {
            this.getCartData();
        }
    },
    methods: {
        // 检查登录状态
        checkLoginStatus: function () {
            const token = uni.getStorageSync('token');
            this.setData({
                isLogin: !!token
            });
        },

        // 获取购物车数据
        getCartData: function () {
            cartApi
                .getCartList()
                .then((data) => {
                    const cartList = data.map((item) => ({
                        ...item,
                        selected: true
                    }));
                    const { totalPrice, totalCount } = this.calculateTotal(cartList);
                    this.setData({
                        cartList,
                        totalPrice,
                        totalCount,
                        isEmpty: cartList.length === 0
                    });
                })
                .catch((err) => {
                    console.error('获取购物车失败', err);
                });
        },

        // 计算总价和总数量
        calculateTotal: function (cartList) {
            let totalPrice = 0;
            let totalCount = 0;
            cartList.forEach((item) => {
                if (item.selected) {
                    totalPrice += item.price * item.count;
                    totalCount += item.count;
                }
            });
            return {
                totalPrice: totalPrice.toFixed(2),
                totalCount
            };
        },

        // 商品数量减1
        decreaseCount: function (e) {
            const { id, count } = e.currentTarget.dataset;
            if (count > 1) {
                cartApi
                    .updateCartCount(id, count - 1)
                    .then(() => {
                        this.getCartData();
                    })
                    .catch((err) => {
                        console.error('更新失败', err);
                    });
            }
        },

        // 商品数量加1
        increaseCount: function (e) {
            const { id, count, inventory } = e.currentTarget.dataset;
            if (count < inventory) {
                cartApi
                    .updateCartCount(id, count + 1)
                    .then(() => {
                        this.getCartData();
                    })
                    .catch((err) => {
                        console.error('更新失败', err);
                    });
            } else {
                uni.showToast({
                    title: '库存不足',
                    icon: 'none'
                });
            }
        },

        // 删除商品
        deleteItem: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.showModal({
                title: '提示',
                content: '确定要删除该商品吗？',
                success: (res) => {
                    if (res.confirm) {
                        cartApi
                            .deleteCartItem(id)
                            .then(() => {
                                this.getCartData();
                            })
                            .catch((err) => {
                                console.error('删除失败', err);
                            });
                    }
                }
            });
        },

        // 选择/取消选择商品
        toggleSelect: function (e) {
            const { index } = e.currentTarget.dataset;
            const { cartList } = this;
            cartList[index].selected = !cartList[index].selected;

            // 重新计算总价和总数量
            const { totalPrice, totalCount } = this.calculateTotal(cartList);

            // 检查是否全选
            const isAllSelected = cartList.every((item) => item.selected);
            this.setData({
                cartList,
                totalPrice,
                totalCount,
                isAllSelected
            });
        },

        // 全选/取消全选
        toggleSelectAll: function () {
            const { cartList, isAllSelected } = this;
            const newIsAllSelected = !isAllSelected;

            // 更新所有商品的选中状态
            const newCartList = cartList.map((item) => {
                return {
                    ...item,
                    selected: newIsAllSelected
                };
            });

            // 重新计算总价和总数量
            const { totalPrice, totalCount } = this.calculateTotal(newCartList);
            this.setData({
                cartList: newCartList,
                isAllSelected: newIsAllSelected,
                totalPrice,
                totalCount
            });
        },

        // 去结算
        goToCheckout: function () {
            if (!this.isLogin) {
                uni.navigateTo({
                    url: '/pages/login/login'
                });
                return;
            }
            if (this.totalCount === 0) {
                uni.showToast({
                    title: '请选择商品',
                    icon: 'none'
                });
                return;
            }

            // 获取选中的商品
            const selectedItems = this.cartList.filter((item) => item.selected);

            // 存储到缓存
            uni.setStorageSync('checkoutItems', selectedItems);

            // 跳转到结算页面
            uni.navigateTo({
                url: '/pages/order/order'
            });
        },

        // 继续购物
        goToShopping: function () {
            uni.switchTab({
                url: '/pages/index/index'
            });
        },

        // 去登录
        goToLogin: function () {
            uni.navigateTo({
                url: '/pages/login/login'
            });
        }
    }
};
</script>
<style>
/* pages/cart/cart.wxss */
.cart-container {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: #f7f7f7;
    padding-bottom: 100rpx;
}

/* 未登录提示 */
.login-tip {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 30rpx;
    background-color: #fff9f0;
    color: #ff9500;
    font-size: 26rpx;
}

.login-btn {
    background-color: #ff9500;
    color: #fff;
    font-size: 24rpx;
    padding: 6rpx 20rpx;
    line-height: 1.5;
    border-radius: 30rpx;
    margin: 0;
}

/* 空购物车 */
.empty-cart {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 100rpx;
}

.empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 30rpx;
}

.empty-text {
    color: #999;
    font-size: 28rpx;
    margin-bottom: 40rpx;
}

.go-shopping-btn {
    background-color: var(--primary-color);
    color: #fff;
    font-size: 28rpx;
    width: 300rpx;
    border-radius: 40rpx;
}

/* 购物车列表 */
.cart-list {
    background-color: #fff;
}

.cart-item {
    display: flex;
    padding: 30rpx 20rpx;
    position: relative;
    border-bottom: 1px solid #f5f5f5;
}

/* 选择框 */
.select-box {
    display: flex;
    align-items: center;
    padding: 0 20rpx;
}

.select-icon {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    border: 1px solid #ddd;
    box-sizing: border-box;
}

.select-icon.selected {
    border: none;
    background-color: var(--primary-color);
    position: relative;
}

.select-icon.selected::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -60%) rotate(-45deg);
    width: 16rpx;
    height: 8rpx;
    border-left: 2px solid #fff;
    border-bottom: 2px solid #fff;
}

/* 商品图片 */
.product-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 12rpx;
    margin-right: 20rpx;
}

/* 商品信息 */
.product-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.product-name {
    font-size: 28rpx;
    font-weight: bold;
    line-height: 1.4;
    margin-bottom: 10rpx;
}

.product-price-box {
    margin-bottom: 20rpx;
}

.price {
    color: var(--price-color);
    font-weight: bold;
    font-size: 32rpx;
}

/* 商品操作 */
.product-action {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.counter {
    display: flex;
    align-items: center;
}

.counter-btn {
    width: 60rpx;
    height: 60rpx;
    border: 1px solid #eee;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 28rpx;
}

.counter-btn.disabled {
    color: #ccc;
}

.counter-num {
    width: 80rpx;
    height: 60rpx;
    border-top: 1px solid #eee;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 28rpx;
}

.delete-btn {
    padding: 10rpx;
}

.delete-icon {
    width: 40rpx;
    height: 40rpx;
}

/* 底部结算栏 */
.cart-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    box-shadow: 0 -2rpx 6rpx rgba(0, 0, 0, 0.05);
    z-index: 99;
}

.select-all {
    display: flex;
    align-items: center;
    padding: 0 30rpx;
}

.select-all text {
    margin-left: 10rpx;
    font-size: 28rpx;
}

.cart-total {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.total-price {
    display: flex;
    align-items: baseline;
}

.price-label {
    font-size: 28rpx;
}

.price-desc {
    font-size: 22rpx;
    color: #999;
}

.checkout-btn {
    width: 240rpx;
    height: 100%;
    background-color: var(--primary-color);
    color: #fff;
    font-size: 30rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 0;
    margin: 0;
}
</style>
