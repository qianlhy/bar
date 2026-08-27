<template>
    <!-- pages/collection/collection.wxml -->
    <view class="collection-container">
        <!-- 未登录状态 -->
        <view class="not-login" v-if="!isLogin">
            <image class="login-image" src="/static/images/icons/favorite.png" mode="aspectFit"></image>
            <view class="login-tip">登录后才能查看收藏的商品</view>
            <button class="login-btn" @tap="goToLogin">去登录</button>
        </view>

        <!-- 已登录状态 -->
        <block v-else>
            <!-- 加载中 -->
            <view class="loading" v-if="isLoading">
                <image class="loading-icon" src="/static/images/icons/loading.png" mode="aspectFit"></image>
                <text>加载中...</text>
            </view>

            <!-- 空收藏 -->
            <view class="empty-collection" v-else-if="isEmpty">
                <image class="empty-icon" src="/static/images/icons/empty.png" mode="aspectFit"></image>
                <view class="empty-tip">您还没有收藏任何商品</view>
                <navigator url="/pages/index/index" open-type="switchTab" class="go-shopping">去逛逛</navigator>
            </view>

            <!-- 收藏列表 -->
            <view class="collection-list" v-else>
                <view class="collection-item" @tap="goToDetail" :data-id="item.id" v-for="(item, index) in favorites" :key="index">
                    <view class="product-card">
                        <image class="product-image" :src="item.image" mode="aspectFill"></image>
                        <view class="product-info">
                            <view class="product-name">{{ item.name }}</view>
                            <view class="product-price">
                                <text class="current-price">¥{{ item.price }}</text>
                                <text class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
                            </view>
                            <view class="product-sales">销量 {{ item.sales }}</view>
                            <view class="product-actions">
                                <view class="action-btn cancel" @tap.stop.prevent="cancelFavorite" :data-id="item.id">取消收藏</view>
                                <view class="action-btn add-cart" @tap.stop.prevent="addToCart" :data-id="item.id">加入购物车</view>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </block>
    </view>
</template>

<script>
// pages/collection/collection.js
const app = getApp();
const favoriteApi = require('../../api/favorite');
const cartApi = require('../../api/cart');
export default {
    data() {
        return {
            favorites: [],
            isEmpty: true,
            isLogin: false,
            isLoading: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        this.checkLoginStatus();
        if (this.isLogin) {
            this.getFavorites();
        }
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {},
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.checkLoginStatus();
        if (this.isLogin) {
            this.getFavorites();
        }
    },
    methods: {
        checkLoginStatus: function () {
            const isLogin = !!uni.getStorageSync('token');
            this.setData({
                isLogin
            });
        },

        // 获取收藏列表
        getFavorites: function () {
            favoriteApi
                .getFavoriteList()
                .then((data) => {
                    this.setData({
                        favorites: data,
                        isEmpty: data.length === 0
                    });
                })
                .catch((err) => {
                    console.error('获取收藏列表失败', err);
                });
        },

        // 跳转到商品详情
        goToDetail: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.navigateTo({
                url: `/pages/product/detail?id=${id}`
            });
        },

        // 取消收藏
        cancelFavorite: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.showModal({
                title: '提示',
                content: '确定要取消收藏吗？',
                success: (res) => {
                    if (res.confirm) {
                        // 使用id作为productId参数传递
                        favoriteApi
                            .removeFavorite(id)
                            .then(() => {
                                uni.showToast({
                                    title: '已取消收藏',
                                    icon: 'success'
                                });
                                this.getFavorites();
                            })
                            .catch((err) => {
                                console.error('取消收藏失败', err);
                                uni.showToast({
                                    title: err.message || '取消收藏失败',
                                    icon: 'none'
                                });
                            });
                    }
                }
            });
        },

        // 加入购物车
        addToCart: function (e) {
            const { id } = e.currentTarget.dataset;

            // 检查登录状态
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({
                    title: '请先登录',
                    icon: 'none'
                });
                setTimeout(() => {
                    uni.navigateTo({
                        url: '/pages/login/login'
                    });
                }, 1500);
                return;
            }

            // 添加到购物车
            cartApi
                .addToCart(id, 1)
                .then(() => {
                    uni.showToast({
                        title: '已加入购物车',
                        icon: 'success'
                    });
                })
                .catch((err) => {
                    console.error('加入购物车失败', err);
                    uni.showToast({
                        title: err.message || '加入购物车失败',
                        icon: 'none'
                    });
                });
        },

        // 跳转登录页
        goToLogin: function () {
            uni.navigateTo({
                url: '/pages/login/login'
            });
        }
    }
};
</script>
<style>
.collection-container {
    min-height: 100vh;
    background-color: var(--bg-page);
    padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
}

/* 未登录状态样式 */
.not-login {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 200rpx;
}

.login-image {
    width: 150rpx;
    height: 150rpx;
    margin-bottom: 30rpx;
    opacity: 0.6;
}

.login-tip {
    font-size: 28rpx;
    color: var(--text-muted);
    margin-bottom: 40rpx;
}

.login-btn {
    width: 240rpx;
    height: 84rpx;
    line-height: 84rpx;
    text-align: center;
    background: var(--gold-gradient);
    color: #171717;
    font-size: 30rpx;
    font-weight: 600;
    border: none;
    border-radius: 42rpx;
    margin: 0;
    padding: 0;
    box-shadow: 0 8rpx 22rpx rgba(232, 197, 71, 0.22);
}

.login-btn::after {
    border: none;
}

/* 加载中样式 */
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 200rpx;
}

.loading-icon {
    width: 80rpx;
    height: 80rpx;
    margin-bottom: 20rpx;
}

.loading text {
    font-size: 28rpx;
    color: var(--text-muted);
}

/* 空收藏样式 */
.empty-collection {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding-top: 200rpx;
}

.empty-icon {
    width: 150rpx;
    height: 150rpx;
    margin-bottom: 30rpx;
}

.empty-tip {
    font-size: 28rpx;
    color: var(--text-muted);
    margin-bottom: 40rpx;
}

.go-shopping {
    width: 240rpx;
    height: 84rpx;
    line-height: 84rpx;
    text-align: center;
    background: var(--gold-gradient);
    color: #171717;
    font-size: 30rpx;
    font-weight: 600;
    border-radius: 42rpx;
    box-shadow: 0 8rpx 22rpx rgba(232, 197, 71, 0.22);
}

/* 收藏列表样式 */
.collection-list {
    padding: 20rpx;
}

.collection-item {
    margin-bottom: 20rpx;
}

.product-card {
    display: flex;
    background: var(--bg-card-gradient);
    border: 1rpx solid var(--border-subtle);
    border-radius: 20rpx;
    overflow: hidden;
    box-shadow: 0 10rpx 28rpx rgba(0, 0, 0, 0.22);
}

.product-image {
    width: 200rpx;
    height: 200rpx;
    background-color: var(--bg-sunken);
    flex-shrink: 0;
}

.product-info {
    flex: 1;
    min-width: 0;
    padding: 22rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.product-name {
    font-size: 28rpx;
    color: var(--text-primary);
    line-height: 1.4;
    margin-bottom: 10rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.product-price {
    display: flex;
    align-items: baseline;
    margin-bottom: 10rpx;
}

.current-price {
    font-size: 32rpx;
    font-weight: 700;
    color: var(--gold);
}

.original-price {
    font-size: 24rpx;
    color: var(--text-faint);
    text-decoration: line-through;
    margin-left: 10rpx;
}

.product-sales {
    font-size: 24rpx;
    color: var(--text-faint);
    margin-bottom: 20rpx;
}

.product-actions {
    display: flex;
    justify-content: flex-end;
}

.action-btn {
    padding: 10rpx 20rpx;
    border-radius: 30rpx;
    font-size: 24rpx;
    margin-left: 20rpx;
}

.action-btn.cancel {
    color: var(--text-regular);
    border: 1rpx solid var(--border-subtle);
    background-color: var(--bg-elevated);
}

.action-btn.add-cart {
    color: #171717;
    font-weight: 600;
    background: var(--gold-gradient);
}
</style>
