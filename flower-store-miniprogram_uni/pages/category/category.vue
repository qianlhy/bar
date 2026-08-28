<template>
    <view class="order-page poker-bg">
        <!-- 门店信息栏 -->
        <view class="store-bar">
            <view class="store-info" @tap="showStorePicker">
                <text class="store-text">梭哈酒馆-南京店</text>
                <text class="store-arrow">›</text>
            </view>
            <view class="store-tag">店内</view>
        </view>

        <view class="order-body">
            <!-- 左侧分类 -->
            <view class="sidebar">
                <scroll-view scroll-y class="sidebar-scroll">
                    <view
                        :class="'sidebar-item ' + (currentCategory && currentCategory.id === item.id ? 'active' : '')"
                        @tap="switchCategory"
                        :data-id="item.id"
                        v-for="(item, index) in categories"
                        :key="index"
                    >
                        <image v-if="currentCategory && currentCategory.id === item.id" class="cat-icon" :src="item.icon || '/static/images/cat-default.png'" mode="aspectFill"></image>
                        <text class="cat-name">{{ item.name }}</text>
                    </view>
                </scroll-view>
            </view>

            <!-- 右侧商品 -->
            <view class="product-area">
                <scroll-view scroll-y class="product-scroll">
                    <view class="section-title" v-if="currentCategory">{{ currentCategory.name }}</view>

                    <view class="no-data" v-if="!isLoading && products.length === 0">
                        <text>暂无商品</text>
                    </view>

                    <view class="product-item" v-for="(item, index) in products" :key="index" @tap="onTapProduct" :data-id="item.id">
                        <image
                            class="product-img"
                            :src="item.image || '/static/allIn.jpg'"
                            mode="aspectFill"
                            @error="onProductImageError(index)"
                        ></image>
                        <view class="product-detail">
                            <text class="product-name">{{ item.name }}</text>
                            <text class="product-desc">{{ item.description }}</text>
                            <text class="product-sales">销量{{ item.sales }}</text>
                            <view class="price-row">
                                <text class="product-price">¥{{ item.price }}</text>
                                <text class="product-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
                            </view>
                        </view>
                        <view
                            v-if="hasSpecs(item)"
                            class="spec-btn"
                            @tap.stop="openSpecModal"
                            :data-id="item.id"
                        >选规格</view>
                        <view
                            v-else
                            class="add-btn g-tap"
                            @tap.stop="onAddToCart"
                            :data-id="item.id"
                        >+</view>
                    </view>
                </scroll-view>
            </view>
        </view>

        <!-- 购物车底栏 -->
        <view class="cart-bar" v-if="cartCount > 0" @tap="goCart">
            <view class="cart-icon-wrap">
                <view class="g-bag"></view>
                <view class="cart-badge">{{ cartCount }}</view>
            </view>
            <view class="cart-info">
                <text class="cart-total g-num">¥{{ cartTotal }}</text>
                <text class="cart-tip">点击查看购物车</text>
            </view>
            <view class="checkout-btn" @tap.stop="goCart">去结算</view>
        </view>

        <!-- 规格弹窗 -->
        <spec-modal
            :visible="showSpecModal"
            :product="specProduct"
            @close="showSpecModal = false"
            @confirm="onSpecConfirm"
        />

        <view class="loading-mask" v-if="isLoading">
            <text class="loading-text">加载中...</text>
        </view>
    </view>
</template>

<script>
const app = getApp();
const eventBus = require('../../utils/eventBus');
const categoryApi = require('../../api/category');
const productApi = require('../../api/product');
const { ensureAdultDrinking } = require('../../utils/adult-verification');
import specModal from '@/components/spec-modal/spec-modal';

export default {
    components: { specModal },
    data() {
        return {
            categories: [],
            products: [],
            currentCategory: null,
            isLoading: true,
            showSpecModal: false,
            specProduct: {},
            cartCount: 0,
            cartTotal: '0.00',
            adultAccessGranted: false,
            adultChecking: false
        };
    },
    onLoad() {
        eventBus.on('switchCategory', this.handleSwitchCategory);
    },
    onShow() {
        if (this.adultChecking) {
            return;
        }
        if (!this.adultAccessGranted) {
            this.adultChecking = true;
            ensureAdultDrinking()
                .then(() => {
                    this.adultAccessGranted = true;
                    this.adultChecking = false;
                    this.enterOrderingPage();
                })
                .catch(() => {
                    this.adultChecking = false;
                    uni.switchTab({ url: '/pages/index/index' });
                });
            return;
        }
        this.enterOrderingPage();
    },
    onUnload() {
        eventBus.off('switchCategory', this.handleSwitchCategory);
    },
    methods: {
        enterOrderingPage() {
            if (this.categories.length === 0) {
                this.getCategories();
                this.loadCartInfo();
                return;
            }
        if (app.globalData.tempCategoryId) {
            const categoryId = app.globalData.tempCategoryId;
            const category = this.categories.find((item) => item.id === categoryId);
            if (category) {
                this.currentCategory = category;
                this.getCategoryProducts(categoryId);
            }
            app.globalData.tempCategoryId = null;
        } else if (this.currentCategory) {
            this.getCategoryProducts(this.currentCategory.id);
        }
        this.loadCartInfo();
    },
        hasSpecs(item) {
            if (!item.specs) return false;
            try {
                const specs = typeof item.specs === 'string' ? JSON.parse(item.specs) : item.specs;
                return specs && specs.length > 0;
            } catch (e) {
                return false;
            }
        },
        getCategories() {
            this.isLoading = true;
            categoryApi.getCategoryList().then((categories) => {
                if (categories && categories.length > 0) {
                    this.categories = categories;
                    this.currentCategory = categories[0];
                    this.isLoading = false;
                    this.getCategoryProducts(categories[0].id);
                } else {
                    this.isLoading = false;
                }
            }).catch(() => { this.isLoading = false; });
        },
        getCategoryProducts(categoryId) {
            productApi.getProductsByCategory(categoryId).then((data) => {
                this.products = (data || []).map((p) => {
                    if (p.images && typeof p.images === 'string') {
                        p.images = p.images.split(',').filter((img) => img.trim());
                    }
                    return p;
                });
            }).catch(() => {
                this.products = [];
            });
        },
        onProductImageError(index) {
            if (!this.products[index]) return;
            this.products.splice(index, 1, {
                ...this.products[index],
                image: '/static/allIn.jpg'
            });
        },
        switchCategory(e) {
            const categoryId = e.currentTarget.dataset.id;
            const category = this.categories.find((item) => item.id === categoryId);
            if (category) {
                this.currentCategory = category;
                this.getCategoryProducts(categoryId);
            }
        },
        handleSwitchCategory(data) {
            if (data && data.categoryId) {
                const category = this.categories.find((item) => item.id === data.categoryId);
                if (category) {
                    this.currentCategory = category;
                    this.getCategoryProducts(data.categoryId);
                }
            }
        },
        onTapProduct(e) {
            const { id } = e.currentTarget.dataset;
            uni.navigateTo({ url: `/pages/product/detail?id=${id}` });
        },
        openSpecModal(e) {
            const id = e.currentTarget.dataset.id;
            const item = this.products.find((p) => p.id === id);
            if (item) {
                this.specProduct = item;
                this.showSpecModal = true;
            }
        },
        onSpecConfirm(data) {
            this.showSpecModal = false;
            this.addToCart(data.product.id, data.count, data.specText);
        },
        onAddToCart(e) {
            const { id } = e.currentTarget.dataset;
            this.addToCart(id, 1, '');
        },
        addToCart(productId, count, specText) {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1500);
                return;
            }
            const cartApi = require('../../api/cart');
            cartApi.addToCart(productId, count, specText).then(() => {
                uni.showToast({ title: '已加入购物车', icon: 'success' });
                this.loadCartInfo();
            });
        },
        loadCartInfo() {
            const token = uni.getStorageSync('token');
            if (!token) { this.cartCount = 0; return; }
            const cartApi = require('../../api/cart');
            cartApi.getCartList().then((list) => {
                let count = 0, total = 0;
                list.forEach((item) => {
                    count += item.count;
                    total += item.price * item.count;
                });
                this.cartCount = count;
                this.cartTotal = total.toFixed(2);
            }).catch(() => {});
        },
        goCart() {
            uni.navigateTo({ url: '/pages/cart/cart' });
        },
        showStorePicker() {
            uni.showToast({ title: '梭哈酒馆-南京店', icon: 'none' });
        }
    }
};
</script>

<style>
.order-page {
    height: 100vh;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}
.store-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 24rpx;
    background: rgba(8, 8, 10, 0.92);
    border-bottom: 1rpx solid rgba(160, 28, 45, 0.2);
}
.store-info { display: flex; align-items: center; }
.store-text { color: #fff; font-size: 28rpx; font-weight: bold; font-family: 'DouyinSans', sans-serif; }
.store-arrow { color: #c9788f; font-size: 32rpx; margin-left: 6rpx; }
.store-tag {
    background: linear-gradient(135deg, #a01c30, #8f3d52);
    color: #fff;
    font-size: 22rpx;
    padding: 6rpx 20rpx;
    border-radius: 20rpx;
    font-weight: bold;
}
.order-body {
    flex: 1;
    display: flex;
    overflow: hidden;
}

.sidebar {
    width: 180rpx;
    background: rgba(20, 20, 24, 0.95);
    flex-shrink: 0;
    border-right: 1rpx solid rgba(255, 255, 255, 0.05);
}
.sidebar-scroll { height: 100%; }
.sidebar-item {
    padding: 26rpx 12rpx;
    text-align: center;
    position: relative;
}
.sidebar-item.active { background: rgba(160, 28, 45, 0.08); }
.sidebar-item.active::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 6rpx;
    height: 40rpx;
    background: linear-gradient(180deg, #a01c30, #8f3d52);
    border-radius: 0 6rpx 6rpx 0;
}
.cat-icon {
    width: 60rpx;
    height: 60rpx;
    border-radius: 10rpx;
    margin-bottom: 8rpx;
}
.cat-name {
    font-size: 24rpx;
    color: #888;
    line-height: 1.3;
    display: block;
}
.sidebar-item.active .cat-name {
    color: #c9788f;
    font-weight: bold;
}

.product-area {
    flex: 1;
    overflow: hidden;
}
.product-scroll { height: 100%; padding-bottom: calc(160rpx + env(safe-area-inset-bottom)); }
.section-title {
    padding: 20rpx 24rpx 10rpx;
    font-size: 28rpx;
    color: #fff;
    font-weight: bold;
    font-family: 'DouyinSans', sans-serif;
}
.product-item {
    display: flex;
    padding: 24rpx;
    position: relative;
    align-items: flex-start;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.04);
}
.product-img {
    width: 160rpx;
    height: 160rpx;
    border-radius: 14rpx;
    flex-shrink: 0;
    background: #222;
    box-shadow: 0 4rpx 16rpx rgba(160, 28, 45, 0.15);
    border: 1rpx solid rgba(255, 255, 255, 0.06);
}
.product-detail {
    flex: 1;
    margin-left: 20rpx;
    padding-right: 100rpx;
}
.product-name {
    font-size: 30rpx;
    color: #fff;
    font-weight: bold;
    display: block;
    margin-bottom: 8rpx;
}
.product-desc {
    font-size: 24rpx;
    color: #888;
    display: block;
    margin-bottom: 8rpx;
}
.product-sales {
    font-size: 22rpx;
    color: var(--text-muted);
    display: block;
    margin-bottom: 8rpx;
}
.price-row { display: flex; align-items: baseline; gap: 12rpx; }
.product-price { font-size: 34rpx; color: #d4bc82; font-weight: bold; }
.product-original {
    font-size: 24rpx;
    color: var(--text-muted);
    text-decoration: line-through;
}
.add-btn {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    width: 58rpx;
    height: 58rpx;
    border-radius: 50%;
    background: linear-gradient(135deg, #a01c30, #8f3d52);
    color: #fff;
    font-size: 40rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
    box-shadow: 0 4rpx 16rpx rgba(160, 28, 45, 0.45);
}
.spec-btn {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    background: linear-gradient(135deg, #a01c30, #8f3d52);
    color: #fff;
    font-size: 22rpx;
    font-weight: bold;
    padding: 12rpx 22rpx;
    border-radius: 24rpx;
    box-shadow: 0 4rpx 16rpx rgba(160, 28, 45, 0.35);
}

.cart-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: auto;
    min-height: 108rpx;
    background: rgba(12, 10, 14, 0.96);
    display: flex;
    align-items: center;
    padding: 16rpx 24rpx calc(16rpx + env(safe-area-inset-bottom));
    z-index: 100;
    border-top: 1rpx solid rgba(160, 28, 45, 0.3);
    box-shadow: 0 -8rpx 28rpx rgba(0, 0, 0, 0.45);
}
.cart-icon-wrap {
    position: relative;
    margin-right: 20rpx;
    width: 76rpx;
    height: 76rpx;
    border-radius: 50%;
    background: radial-gradient(circle at 32% 26%, rgba(160, 28, 45, 0.25), rgba(160, 28, 45, 0.06));
    border: 1rpx solid rgba(160, 28, 45, 0.45);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #c9788f;
}
.cart-total { font-size: 36rpx; color: #c9788f; font-weight: bold; display: block; }
.checkout-btn {
    background: linear-gradient(135deg, #a01c30, #8f3d52);
    color: #fff;
    padding: 18rpx 44rpx;
    border-radius: 40rpx;
    font-size: 28rpx;
    font-weight: bold;
    box-shadow: 0 4rpx 16rpx rgba(160, 28, 45, 0.4);
}

.no-data { text-align: center; padding: 100rpx 0; color: var(--text-muted); font-size: 28rpx; }
.loading-mask {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 200;
}
.loading-text { color: #fff; font-size: 28rpx; }
</style>
