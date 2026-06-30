<template>
    <view class="page-wrap">
    <view class="detail-page" v-if="!isLoading && product">
        <!-- 商品轮播 -->
        <swiper class="product-swiper" :indicator-dots="true" indicator-color="rgba(0,0,0,0.2)" indicator-active-color="#000" :autoplay="false" :circular="true" @change="onSwiperChange">
            <swiper-item v-for="(img, index) in product.images" :key="index">
                <image :src="img" mode="aspectFit" class="swiper-img"></image>
            </swiper-item>
        </swiper>

        <!-- 商品信息 -->
        <view class="info-section">
            <view class="info-left">
                <text class="product-title">{{ product.name }}</text>
                <text class="product-meta">销量 {{ product.sales }}  库存 {{ product.inventory }}</text>
                <text class="product-desc">{{ product.description }}</text>
                <view class="price-row">
                    <text class="price-symbol">¥</text>
                    <text class="price-num">{{ product.price }}</text>
                    <text class="price-unit">/份</text>
                </view>
            </view>
            <view class="quick-add" @tap="quickAdd">+</view>
        </view>

        <!-- 商品详情 -->
        <view class="detail-section">
            <text class="detail-title">商品详情</text>
            <view class="detail-content">
                <text>{{ product.detail || '暂无详情' }}</text>
            </view>
        </view>

        <!-- 底部操作栏 -->
        <view class="footer-bar">
            <view class="footer-nav">
                <view class="nav-item" @tap="goHome">
                    <text class="nav-icon">🏠</text>
                    <text class="nav-text">首页</text>
                </view>
                <button class="nav-item share-btn" open-type="share">
                    <text class="nav-icon">↗</text>
                    <text class="nav-text">分享</text>
                </button>
                <view class="nav-item" @tap="goCart">
                    <text class="nav-icon">🛒</text>
                    <text class="nav-text">购物车</text>
                    <view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
                </view>
            </view>
            <view class="footer-actions">
                <view class="btn-add-cart" @tap="addToCart">加入购物车</view>
                <view class="btn-buy" @tap="buyNow">去结算</view>
            </view>
        </view>
    </view>

    <view class="loading-page" v-if="isLoading">
        <text>加载中...</text>
    </view>
    </view>
</template>

<script>
const productApi = require('../../api/product');
const cartApi = require('../../api/cart');
export default {
    data() {
        return {
            product: null,
            isLoading: true,
            current: 0,
            cartCount: 0
        };
    },
    onLoad(options) {
        if (options.id) {
            this.loadProduct(parseInt(options.id));
        }
    },
    onShow() {
        this.loadCartCount();
    },
    onShareAppMessage() {
        return {
            title: this.product ? this.product.name : '梭哈酒馆',
            path: `/pages/product/detail?id=${this.product.id}`,
            imageUrl: this.product ? this.product.image : ''
        };
    },
    methods: {
        loadProduct(id) {
            productApi.getProductById(id).then((product) => {
                if (product.images && typeof product.images === 'string') {
                    product.images = product.images.split(',').filter((img) => img.trim());
                }
                if (!Array.isArray(product.images) || !product.images.length) {
                    product.images = [product.image];
                }
                this.product = product;
                this.isLoading = false;
                uni.setNavigationBarTitle({ title: product.name });
            }).catch(() => {
                this.isLoading = false;
                uni.showToast({ title: '加载失败', icon: 'none' });
            });
        },
        loadCartCount() {
            const token = uni.getStorageSync('token');
            if (!token) return;
            cartApi.getCartList().then((list) => {
                this.cartCount = list.reduce((s, i) => s + i.count, 0);
            }).catch(() => {});
        },
        checkLogin() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1500);
                return false;
            }
            return true;
        },
        doAdd(count) {
            return cartApi.addToCart(this.product.id, count).then(() => {
                uni.showToast({ title: '已加入购物车', icon: 'success' });
                this.loadCartCount();
            });
        },
        quickAdd() {
            if (!this.checkLogin()) return;
            this.doAdd(1);
        },
        addToCart() {
            if (!this.checkLogin()) return;
            this.doAdd(1);
        },
        buyNow() {
            if (!this.checkLogin()) return;
            this.doAdd(1).then(() => {
                uni.navigateTo({ url: '/pages/cart/cart' });
            });
        },
        goHome() {
            uni.switchTab({ url: '/pages/index/index' });
        },
        goCart() {
            uni.navigateTo({ url: '/pages/cart/cart' });
        },
        onSwiperChange(e) {
            this.current = e.detail.current;
        }
    }
};
</script>

<style>
.page-wrap { min-height: 100vh; }
.detail-page {
    min-height: 100vh;
    background: #fff;
    padding-bottom: 120rpx;
}
.product-swiper {
    width: 100%;
    height: 750rpx;
    background: #f8f8f8;
}
.swiper-img { width: 100%; height: 100%; }

.info-section {
    display: flex;
    padding: 30rpx;
    align-items: flex-start;
    border-bottom: 16rpx solid #f5f5f5;
}
.info-left { flex: 1; }
.product-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #000;
    display: block;
    margin-bottom: 12rpx;
}
.product-meta {
    font-size: 24rpx;
    color: #999;
    display: block;
    margin-bottom: 12rpx;
}
.product-desc {
    font-size: 28rpx;
    color: #666;
    display: block;
    margin-bottom: 16rpx;
}
.price-row { display: flex; align-items: baseline; }
.price-symbol { font-size: 28rpx; color: #000; font-weight: bold; }
.price-num { font-size: 44rpx; color: #000; font-weight: bold; margin: 0 4rpx; }
.price-unit { font-size: 24rpx; color: #999; }
.quick-add {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    background: #e8c547;
    color: #000;
    font-size: 44rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-left: 20rpx;
    line-height: 1;
}

.detail-section { padding: 30rpx; }
.detail-title {
    font-size: 30rpx;
    font-weight: bold;
    color: #000;
    display: block;
    margin-bottom: 20rpx;
}
.detail-content text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.8;
}

.footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 110rpx;
    background: #fff;
    border-top: 1rpx solid #eee;
    display: flex;
    align-items: center;
    padding: 0 20rpx;
    z-index: 99;
    padding-bottom: env(safe-area-inset-bottom);
}
.footer-nav { display: flex; }
.nav-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    width: 90rpx;
    position: relative;
    background: transparent;
    padding: 0;
    margin: 0;
    line-height: 1;
    border: none;
}
.nav-item::after { border: none; }
.nav-icon { font-size: 36rpx; }
.nav-text { font-size: 18rpx; color: #666; margin-top: 4rpx; }
.cart-badge {
    position: absolute;
    top: -6rpx;
    right: 10rpx;
    background: #000;
    color: #fff;
    font-size: 18rpx;
    min-width: 28rpx;
    height: 28rpx;
    border-radius: 14rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 6rpx;
}
.footer-actions {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    height: 72rpx;
}
.btn-add-cart {
    height: 72rpx;
    padding: 0 36rpx;
    border: 2rpx solid #000;
    border-radius: 36rpx 0 0 36rpx;
    background: #fff;
    color: #000;
    font-size: 26rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
.btn-buy {
    height: 72rpx;
    padding: 0 36rpx;
    background: #000;
    color: #fff;
    border-radius: 0 36rpx 36rpx 0;
    font-size: 26rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
.share-btn { font-size: inherit; }

.loading-page {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #999;
    background: #fff;
}
</style>
