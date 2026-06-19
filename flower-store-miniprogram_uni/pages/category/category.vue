<template>
    <view class="order-page">
        <!-- 门店信息栏 -->
        <view class="store-bar">
            <view class="store-info" @tap="showStorePicker">
                <text class="store-text">27 POKER BAR-武昌店</text>
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
                        <image class="product-img" :src="item.image" mode="aspectFill"></image>
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
                            class="add-btn"
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
                <text class="cart-icon">🛒</text>
                <view class="cart-badge">{{ cartCount }}</view>
            </view>
            <view class="cart-info">
                <text class="cart-total">¥{{ cartTotal }}</text>
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
            cartTotal: '0.00'
        };
    },
    onLoad() {
        this.getCategories();
        eventBus.on('switchCategory', this.handleSwitchCategory);
    },
    onShow() {
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
    onUnload() {
        eventBus.off('switchCategory', this.handleSwitchCategory);
    },
    methods: {
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
                this.products = data.map((p) => {
                    if (p.images && typeof p.images === 'string') {
                        p.images = p.images.split(',').filter((img) => img.trim());
                    }
                    return p;
                });
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
            cartApi.addToCart(productId, count).then(() => {
                if (specText) {
                    const specs = uni.getStorageSync('cartSpecs') || {};
                    specs[productId] = specText;
                    uni.setStorageSync('cartSpecs', specs);
                }
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
            uni.showToast({ title: '27 POKER BAR-武昌店', icon: 'none' });
        }
    }
};
</script>

<style>
.order-page {
    height: 100vh;
    background: #000;
    display: flex;
    flex-direction: column;
    overflow: hidden;
}
.store-bar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 24rpx;
    background: #000;
    border-bottom: 1rpx solid #1a1a1a;
}
.store-info { display: flex; align-items: center; }
.store-text { color: #fff; font-size: 28rpx; font-weight: bold; }
.store-arrow { color: #999; font-size: 32rpx; margin-left: 6rpx; }
.store-tag {
    background: #e8c547;
    color: #000;
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

/* 侧边栏 */
.sidebar {
    width: 180rpx;
    background: #1a1a1a;
    flex-shrink: 0;
}
.sidebar-scroll { height: 100%; }
.sidebar-item {
    padding: 24rpx 12rpx;
    text-align: center;
    border-bottom: 1rpx solid #222;
}
.sidebar-item.active {
    background: #fff;
}
.cat-icon {
    width: 60rpx;
    height: 60rpx;
    border-radius: 8rpx;
    margin-bottom: 8rpx;
}
.cat-name {
    font-size: 22rpx;
    color: #888;
    line-height: 1.3;
    display: block;
}
.sidebar-item.active .cat-name {
    color: #000;
    font-weight: bold;
}

/* 商品区 */
.product-area {
    flex: 1;
    background: #000;
    overflow: hidden;
}
.product-scroll { height: 100%; padding-bottom: 120rpx; }
.section-title {
    padding: 20rpx 24rpx 10rpx;
    font-size: 28rpx;
    color: #fff;
    font-weight: bold;
}
.product-item {
    display: flex;
    padding: 20rpx 24rpx;
    position: relative;
    align-items: flex-start;
}
.product-img {
    width: 160rpx;
    height: 160rpx;
    border-radius: 12rpx;
    flex-shrink: 0;
    background: #222;
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
    color: #666;
    display: block;
    margin-bottom: 8rpx;
}
.price-row { display: flex; align-items: baseline; gap: 12rpx; }
.product-price { font-size: 32rpx; color: #fff; font-weight: bold; }
.product-original {
    font-size: 24rpx;
    color: #666;
    text-decoration: line-through;
}
.add-btn {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    background: #e8c547;
    color: #000;
    font-size: 40rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
}
.spec-btn {
    position: absolute;
    right: 24rpx;
    bottom: 24rpx;
    background: #e8c547;
    color: #000;
    font-size: 22rpx;
    font-weight: bold;
    padding: 10rpx 20rpx;
    border-radius: 24rpx;
}

/* 购物车底栏 */
.cart-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background: #1c1c1e;
    display: flex;
    align-items: center;
    padding: 0 24rpx;
    z-index: 100;
    border-top: 1rpx solid #333;
}
.cart-icon-wrap { position: relative; margin-right: 20rpx; }
.cart-icon { font-size: 48rpx; }
.cart-badge {
    position: absolute;
    top: -8rpx;
    right: -12rpx;
    background: #c41e3a;
    color: #fff;
    font-size: 20rpx;
    min-width: 32rpx;
    height: 32rpx;
    border-radius: 16rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 6rpx;
}
.cart-info { flex: 1; }
.cart-total { font-size: 36rpx; color: #fff; font-weight: bold; display: block; }
.cart-tip { font-size: 20rpx; color: #888; }
.checkout-btn {
    background: #e8c547;
    color: #000;
    padding: 16rpx 40rpx;
    border-radius: 40rpx;
    font-size: 28rpx;
    font-weight: bold;
}

.no-data { text-align: center; padding: 100rpx 0; color: #666; font-size: 28rpx; }
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
