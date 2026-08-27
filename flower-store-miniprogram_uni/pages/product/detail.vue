<template>
    <view class="page-wrap">
        <view class="detail-page" v-if="!isLoading && product">
            <!-- 商品轮播 -->
            <view class="swiper-wrap">
                <swiper
                    class="product-swiper"
                    :indicator-dots="product.images.length > 1"
                    indicator-color="rgba(255,255,255,0.25)"
                    indicator-active-color="#e8c547"
                    :autoplay="false"
                    :circular="true"
                    @change="onSwiperChange"
                >
                    <swiper-item v-for="(img, index) in product.images" :key="index">
                        <image :src="img || '/static/allIn.jpg'" mode="aspectFit" class="swiper-img" @error="onImageError(index)"></image>
                    </swiper-item>
                </swiper>
                <view class="swiper-index" v-if="product.images.length > 1">{{ current + 1 }}/{{ product.images.length }}</view>
            </view>

            <!-- 商品信息 -->
            <view class="info-card">
                <view class="info-top">
                    <view class="info-left">
                        <text class="product-title">{{ product.name }}</text>
                        <view class="meta-row">
                            <text class="meta-tag">销量 {{ product.sales || 0 }}</text>
                            <text class="meta-tag">库存 {{ product.inventory || 0 }}</text>
                        </view>
                        <text class="product-desc">{{ product.description || '精选酒吧套餐' }}</text>
                    </view>
                    <view :class="'quick-add ' + (isSoldOut ? 'disabled' : '')" @tap="quickAdd">{{ isSoldOut ? '售罄' : '+' }}</view>
                </view>
                <view class="price-row">
                    <text class="price-symbol">¥</text>
                    <text class="price-num">{{ product.price }}</text>
                    <text class="price-unit">/份</text>
                    <text class="price-original" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
                </view>
            </view>

            <!-- 商品详情 -->
            <view class="detail-card">
                <view class="detail-head">
                    <view class="detail-line"></view>
                    <text class="detail-title">商品详情</text>
                    <view class="detail-line"></view>
                </view>
                <view class="detail-content">
                    <text>{{ product.detail || product.description || '暂无详情介绍' }}</text>
                </view>
            </view>

            <!-- 底部操作栏 -->
            <view class="footer-bar">
                <view class="footer-nav">
                    <view class="nav-item g-tap" @tap="goHome">
                        <text class="nav-icon">首</text>
                        <text class="nav-text">首页</text>
                    </view>
                    <button class="nav-item share-btn g-tap" open-type="share">
                        <text class="nav-icon">享</text>
                        <text class="nav-text">分享</text>
                    </button>
                    <view class="nav-item g-tap" @tap="goCart">
                        <text class="nav-icon">车</text>
                        <text class="nav-text">购物车</text>
                        <view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
                    </view>
                </view>
                <view class="footer-actions">
                    <view :class="'btn-add-cart ' + (isSoldOut ? 'disabled' : '')" @tap="addToCart">
                        {{ isSoldOut ? '暂时售罄' : '加入购物车' }}
                    </view>
                    <view :class="'btn-buy ' + (isSoldOut ? 'disabled' : '')" @tap="buyNow">立即购买</view>
                </view>
            </view>
        </view>

        <view class="loading-page" v-if="isLoading">
            <view class="loading-spinner"></view>
            <text class="loading-text">加载中...</text>
        </view>

        <spec-modal
            v-if="product"
            :visible="showSpecModal"
            :product="product"
            @close="showSpecModal = false"
            @confirm="onSpecConfirm"
        />
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
            cartCount: 0,
            isAdding: false,
            showSpecModal: false,
            specAction: 'cart'
        };
    },
    computed: {
        isSoldOut() {
            return !this.product || Number(this.product.inventory || 0) <= 0;
        },
        hasSpecs() {
            return !!(this.product && this.product.specs);
        }
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
                    product.images = product.images
                        .split(',')
                        .map((img) => img.trim())
                        .filter(Boolean);
                }
                if (!Array.isArray(product.images) || !product.images.length) {
                    product.images = [product.image || '/static/allIn.jpg'];
                }
                product.images = product.images.map((img) => img || '/static/allIn.jpg');
                product.image = product.image || product.images[0];
                this.product = product;
                this.isLoading = false;
                uni.setNavigationBarTitle({ title: product.name });
            }).catch(() => {
                this.isLoading = false;
                uni.showToast({ title: '加载失败', icon: 'none' });
            });
        },
        onImageError(index) {
            if (this.product && this.product.images) {
                this.product.images.splice(index, 1, '/static/allIn.jpg');
            }
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
        doAdd(count, specText) {
            if (this.isSoldOut || this.isAdding) {
                if (this.isSoldOut) {
                    uni.showToast({ title: '该商品暂时售罄', icon: 'none' });
                }
                return Promise.resolve();
            }
            this.isAdding = true;
            return cartApi.addToCart(this.product.id, count, specText || '').then(() => {
                uni.showToast({ title: '已加入购物车', icon: 'success' });
                this.loadCartCount();
            }).finally(() => {
                this.isAdding = false;
            });
        },
        quickAdd() {
            if (!this.checkLogin()) return;
            if (this.hasSpecs) {
                this.specAction = 'cart';
                this.showSpecModal = true;
                return;
            }
            this.doAdd(1);
        },
        addToCart() {
            if (!this.checkLogin()) return;
            if (this.hasSpecs) {
                this.specAction = 'cart';
                this.showSpecModal = true;
                return;
            }
            this.doAdd(1);
        },
        buyNow() {
            if (!this.checkLogin()) return;
            if (this.isSoldOut) {
                uni.showToast({ title: '该商品暂时售罄', icon: 'none' });
                return;
            }
            if (this.hasSpecs) {
                this.specAction = 'buy';
                this.showSpecModal = true;
                return;
            }
            this.checkoutNow(1, '', this.product.price);
        },
        onSpecConfirm(data) {
            this.showSpecModal = false;
            if (this.specAction === 'buy') {
                this.checkoutNow(data.count, data.specText, data.unitPrice);
            } else {
                this.doAdd(data.count, data.specText);
            }
        },
        checkoutNow(count, specText, unitPrice) {
            const item = {
                productId: this.product.id,
                name: this.product.name,
                image: this.product.image || this.product.images[0] || '/static/allIn.jpg',
                specText: specText || '',
                price: unitPrice,
                count
            };
            uni.setStorageSync('checkoutItems', [item]);
            uni.navigateTo({ url: '/pages/order/order' });
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
.page-wrap {
    min-height: 100vh;
    background: var(--bg-dark);
}

.detail-page {
    min-height: 100vh;
    background: var(--bg-dark);
    padding-bottom: calc(120rpx + env(safe-area-inset-bottom));
}

/* 轮播 */
.swiper-wrap {
    position: relative;
    background: #141414;
}

.product-swiper {
    width: 100%;
    height: 680rpx;
}

.swiper-img {
    width: 100%;
    height: 100%;
    background:
        radial-gradient(circle at center, rgba(232, 197, 71, 0.08), transparent 55%),
        #171719;
}

.swiper-index {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    background: rgba(0, 0, 0, 0.55);
    color: #e8c547;
    font-size: 22rpx;
    padding: 6rpx 18rpx;
    border-radius: 20rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.3);
}

/* 信息卡片 */
.info-card {
    margin: 24rpx;
    padding: 32rpx 28rpx;
    background: var(--bg-card);
    border-radius: 20rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.12);
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.35);
}

.info-top {
    display: flex;
    align-items: flex-start;
}

.info-left {
    flex: 1;
    min-width: 0;
}

.product-title {
    font-size: 36rpx;
    font-weight: bold;
    color: #fff;
    display: block;
    line-height: 1.4;
    margin-bottom: 16rpx;
}

.meta-row {
    display: flex;
    gap: 16rpx;
    margin-bottom: 16rpx;
}

.meta-tag {
    font-size: 22rpx;
    color: #8a8a8e;
    background: #2c2c2e;
    padding: 6rpx 16rpx;
    border-radius: 8rpx;
}

.product-desc {
    font-size: 26rpx;
    color: #aaa;
    display: block;
    line-height: 1.5;
}

.quick-add {
    width: 68rpx;
    height: 68rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #f7dc8a, #d4a72c);
    color: #1a1a1a;
    font-size: 44rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    margin-left: 20rpx;
    box-shadow: 0 4rpx 14rpx rgba(232, 197, 71, 0.35);
}

.quick-add.disabled {
    width: 86rpx;
    border-radius: 34rpx;
    background: #333;
    box-shadow: none;
    color: #777;
    font-size: 21rpx;
}

.price-row {
    display: flex;
    align-items: baseline;
    margin-top: 28rpx;
    padding-top: 24rpx;
    border-top: 1rpx solid rgba(255, 255, 255, 0.06);
}

.price-symbol {
    font-size: 28rpx;
    color: #e8c547;
    font-weight: bold;
}

.price-num {
    font-size: 48rpx;
    color: #e8c547;
    font-weight: bold;
    margin: 0 6rpx;
}

.price-unit {
    font-size: 24rpx;
    color: #8a8a8e;
}

.price-original {
    font-size: 24rpx;
    color: var(--text-muted);
    text-decoration: line-through;
    margin-left: 16rpx;
}

/* 详情卡片 */
.detail-card {
    margin: 0 24rpx 24rpx;
    padding: 32rpx 28rpx;
    background: var(--bg-card);
    border-radius: 20rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.12);
}

.detail-head {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20rpx;
    margin-bottom: 28rpx;
}

.detail-line {
    flex: 1;
    height: 1rpx;
    background: linear-gradient(90deg, transparent, rgba(232, 197, 71, 0.4), transparent);
}

.detail-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #f3d780;
    letter-spacing: 2rpx;
    flex-shrink: 0;
}

.detail-content text {
    font-size: 28rpx;
    color: #ccc;
    line-height: 1.85;
}

/* 底部栏 */
.footer-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 110rpx;
    background: linear-gradient(180deg, #242426, #161618);
    border-top: 1rpx solid rgba(232, 197, 71, 0.15);
    display: flex;
    align-items: center;
    padding: 0 20rpx;
    padding-bottom: env(safe-area-inset-bottom);
    z-index: 99;
    box-shadow: 0 -6rpx 24rpx rgba(0, 0, 0, 0.5);
}

.footer-nav {
    display: flex;
}

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

.nav-item::after {
    border: none;
}

.nav-icon {
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    border: 1rpx solid rgba(232, 197, 71, 0.3);
    background: rgba(232, 197, 71, 0.08);
    color: var(--gold-light);
    font-size: 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.nav-text {
    font-size: 18rpx;
    color: var(--text-muted);
    margin-top: 6rpx;
}

.cart-badge {
    position: absolute;
    top: -6rpx;
    right: 10rpx;
    background: #c41e3a;
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
    padding: 0 32rpx;
    border: 2rpx solid #e8c547;
    border-radius: 36rpx 0 0 36rpx;
    background: transparent;
    color: #e8c547;
    font-size: 26rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
}

.btn-buy {
    height: 72rpx;
    padding: 0 32rpx;
    background: linear-gradient(135deg, #f7dc8a, #d4a72c);
    color: #1a1a1a;
    border-radius: 0 36rpx 36rpx 0;
    font-size: 26rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 4rpx 14rpx rgba(232, 197, 71, 0.3);
}

.btn-add-cart.disabled,
.btn-buy.disabled {
    border-color: #444;
    background: #2c2c2e;
    box-shadow: none;
    color: #777;
}

.share-btn {
    font-size: inherit;
}

/* 加载态 */
.loading-page {
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: var(--bg-dark);
    gap: 24rpx;
}

.loading-spinner {
    width: 56rpx;
    height: 56rpx;
    border: 4rpx solid rgba(232, 197, 71, 0.2);
    border-top-color: #e8c547;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
}

.loading-text {
    color: #8a8a8e;
    font-size: 28rpx;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}
</style>
