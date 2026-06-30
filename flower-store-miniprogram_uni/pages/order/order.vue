<template>
    <!-- pages/order/order.wxml -->
    <view class="order-container">
        <!-- 桌台信息 -->
        <view class="table-section">
            <view class="section-title">就餐信息</view>
            <view class="table-info">
                <text class="table-label">桌台号</text>
                <input class="table-input" placeholder="请输入桌台号（选填）" :value="tableNo" @input="inputTableNo" />
            </view>
            <view class="store-tag-row">
                <text class="store-name">梭哈酒馆-武昌店</text>
                <view class="instore-tag">店内</view>
            </view>
        </view>

        <!-- 商品列表 -->
        <view class="goods-section">
            <view class="section-title">商品清单</view>
            <view class="goods-list">
                <view class="goods-item" v-for="(item, index) in orderItems" :key="index">
                    <image class="goods-image" :src="item.image" mode="aspectFill"></image>

                    <view class="goods-info">
                        <text class="goods-name text-ellipsis">{{ item.name }}</text>
                        <view class="goods-price-box">
                            <text class="price">¥{{ item.price }}</text>
                            <text class="count">x{{ item.count }}</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 支付方式 -->
        <view class="payment-section">
            <view class="section-title">支付方式</view>
            <view class="payment-options">
                <view :class="'payment-option ' + (paymentMethod === 'online' ? 'selected' : '')" @tap="switchPaymentMethod" data-method="online">
                    <text class="payment-icon-text">💳</text>
                    <text class="payment-name">微信支付</text>
                    <view class="radio-box">
                        <view :class="'radio ' + (paymentMethod === 'online' ? 'selected' : '')"></view>
                    </view>
                </view>

                <view :class="'payment-option ' + (paymentMethod === 'balance' ? 'selected' : '')" @tap="switchPaymentMethod" data-method="balance">
                    <text class="payment-icon-text">💰</text>
                    <text class="payment-name">余额支付</text>
                    <view class="radio-box">
                        <view :class="'radio ' + (paymentMethod === 'balance' ? 'selected' : '')"></view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 订单备注 -->
        <view class="remark-section">
            <view class="section-title">订单备注</view>
            <textarea class="remark-input" placeholder="请输入订单备注（选填）" maxlength="100" :value="remark" @input="inputRemark"></textarea>
        </view>

        <!-- 订单金额 -->
        <view class="amount-section">
            <view class="amount-item">
                <text class="amount-label">商品金额</text>
                <text class="amount-value">¥{{ totalPrice }}</text>
            </view>
            <view class="amount-item">
                <text class="amount-label">服务费</text>
                <text class="amount-value">¥0.00</text>
            </view>
            <view class="divider"></view>
            <view class="amount-item total">
                <text class="amount-label">实付款</text>
                <text class="amount-value price">¥{{ actualPayment }}</text>
            </view>
        </view>

        <!-- 底部提交栏 -->
        <view class="footer">
            <view class="total-info">
                <text class="total-label">合计：</text>
                <text class="total-price">¥{{ actualPayment }}</text>
            </view>
            <button :class="'submit-btn ' + (isLoading ? 'loading' : '')" @tap="submitOrder" :disabled="isLoading">
                {{ isLoading ? '提交中...' : '提交订单' }}
            </button>
        </view>
    </view>
</template>

<script>
// pages/order/order.js
const app = getApp();
const orderApi = require('../../api/order');
const addressApi = require('../../api/address');
export default {
    data() {
        return {
            orderItems: [],
            totalPrice: 0,
            totalCount: 0,
            freight: 0,
            actualPayment: 0,
            tableNo: '',
            paymentMethod: 'online',
            remark: '',
            isLoading: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        this.getCheckoutItems();
    },
    onShow() {},
    methods: {
        // 获取结算商品
        getCheckoutItems: function () {
            const checkoutItems = uni.getStorageSync('checkoutItems') || [];
            if (checkoutItems.length === 0) {
                uni.showToast({
                    title: '请选择商品',
                    icon: 'none'
                });
                setTimeout(() => {
                    uni.navigateBack();
                }, 1500);
                return;
            }

            // 计算总价和总数量
            let totalPrice = 0;
            let totalCount = 0;
            checkoutItems.forEach((item) => {
                totalPrice += item.price * item.count;
                totalCount += item.count;
            });

            const freight = 0;
            const actualPayment = totalPrice + freight;
            this.setData({
                orderItems: checkoutItems,
                totalPrice,
                totalCount,
                freight,
                actualPayment
            });
        },

        inputTableNo: function (e) {
            this.setData({ tableNo: e.detail.value });
        },

        // 切换支付方式
        switchPaymentMethod: function (e) {
            const { method } = e.currentTarget.dataset;
            this.setData({
                paymentMethod: method
            });
        },

        // 输入备注
        inputRemark: function (e) {
            this.setData({
                remark: e.detail.value
            });
        },

        // 提交订单
        submitOrder: function () {
            const { orderItems, paymentMethod, remark, tableNo } = this;
            const userInfo = uni.getStorageSync('userInfo') || {};

            this.setData({ isLoading: true });

            const orderData = {
                receiverName: userInfo.nickname || '店内顾客',
                receiverPhone: userInfo.phone || '00000000000',
                province: '湖北省',
                city: '武汉市',
                district: '武昌区',
                address: tableNo ? `桌台号: ${tableNo}` : '梭哈酒馆-武昌店 店内',
                paymentMethod: paymentMethod,
                remark: remark,
                items: orderItems.map((item) => ({
                    productId: item.productId,
                    productName: item.name,
                    productImage: item.image,
                    price: item.price,
                    count: item.count
                }))
            };

            // 创建订单
            orderApi
                .createOrder(orderData)
                .then((order) => {
                    // 清除结算商品缓存
                    uni.removeStorageSync('checkoutItems');

                    // 隐藏加载状态
                    this.setData({
                        isLoading: false
                    });
                    uni.showToast({
                        title: '下单成功',
                        icon: 'success'
                    });

                    // 跳转到订单列表
                    setTimeout(() => {
                        uni.redirectTo({
                            url: '/pages/order/list'
                        });
                    }, 1500);
                })
                .catch((err) => {
                    console.error('创建订单失败', err);
                    this.setData({
                        isLoading: false
                    });
                });
        }
    }
};
</script>
<style>
/* pages/order/order.wxss */
.order-container {
    min-height: 100vh;
    background-color: #000;
    padding-bottom: 120rpx;
}
.section-title {
    font-size: 28rpx;
    font-weight: bold;
    color: #fff;
    padding: 20rpx 0;
}

.arrow {
    color: #999;
    font-size: 28rpx;
}

.divider {
    height: 1px;
    background-color: #f5f5f5;
    margin: 20rpx 0;
}

/* 收货地址 */
.table-section {
    background-color: #1c1c1e;
    margin-bottom: 20rpx;
    padding: 30rpx;
    border-radius: 16rpx;
}
.table-info {
    display: flex;
    align-items: center;
    margin-top: 20rpx;
    padding: 20rpx;
    background: #2c2c2e;
    border-radius: 12rpx;
}
.table-label { font-size: 28rpx; color: #ccc; margin-right: 20rpx; }
.table-input { flex: 1; font-size: 28rpx; color: #fff; }
.store-tag-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 20rpx;
}
.store-name { font-size: 26rpx; color: #fff; font-weight: bold; }
.instore-tag {
    background: #e8c547;
    color: #000;
    font-size: 22rpx;
    padding: 6rpx 20rpx;
    border-radius: 20rpx;
    font-weight: bold;
}
.payment-icon-text { font-size: 40rpx; margin-right: 16rpx; }

.address-empty {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.add-address {
    display: flex;
    align-items: center;
}

.add-icon {
    width: 40rpx;
    height: 40rpx;
    margin-right: 20rpx;
}

.address-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.address-detail {
    flex: 1;
}

.name-phone {
    margin-bottom: 16rpx;
}

.name {
    font-size: 30rpx;
    font-weight: bold;
    margin-right: 20rpx;
}

.phone {
    font-size: 28rpx;
    color: #666;
}

.address {
    display: flex;
    align-items: flex-start;
}

.default-tag {
    font-size: 22rpx;
    color: #fff;
    background-color: var(--primary-color);
    padding: 4rpx 12rpx;
    border-radius: 4rpx;
    margin-right: 10rpx;
    margin-top: 4rpx;
}

.address-text {
    font-size: 28rpx;
    color: #333;
    line-height: 1.4;
    flex: 1;
}

/* 商品清单 */
.goods-section {
    background-color: #fff;
    margin-bottom: 20rpx;
    padding: 0 30rpx;
}

.goods-list {
    padding-bottom: 20rpx;
}

.goods-item {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1px solid #f5f5f5;
}

.goods-item:last-child {
    border-bottom: none;
}

.goods-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    margin-right: 20rpx;
}

.goods-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.goods-name {
    font-size: 28rpx;
    line-height: 1.4;
    margin-bottom: 10rpx;
}

.goods-price-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price {
    color: var(--price-color);
    font-weight: bold;
    font-size: 32rpx;
}

.count {
    font-size: 28rpx;
    color: #666;
}

/* 支付方式 */
.payment-section {
    background-color: #fff;
    margin-bottom: 20rpx;
    padding: 0 30rpx;
}

.payment-options {
    padding-bottom: 20rpx;
}

.payment-option {
    display: flex;
    align-items: center;
    padding: 20rpx 0;
    border-bottom: 1px solid #f5f5f5;
}

.payment-option:last-child {
    border-bottom: none;
}

.payment-icon {
    width: 48rpx;
    height: 48rpx;
    margin-right: 20rpx;
}

.payment-name {
    flex: 1;
    font-size: 28rpx;
    color: #333;
}

.radio-box {
    width: 40rpx;
    height: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

.radio {
    width: 30rpx;
    height: 30rpx;
    border-radius: 50%;
    border: 1px solid #ddd;
    box-sizing: border-box;
    position: relative;
}

.radio.selected {
    border-color: var(--primary-color);
}

.radio.selected::after {
    content: '';
    position: absolute;
    width: 16rpx;
    height: 16rpx;
    border-radius: 50%;
    background-color: var(--primary-color);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

/* 订单备注 */
.remark-section {
    background-color: #fff;
    margin-bottom: 20rpx;
    padding: 0 30rpx;
}

.remark-input {
    width: 100%;
    height: 160rpx;
    font-size: 28rpx;
    padding: 20rpx 0;
}

/* 订单金额 */
.amount-section {
    background-color: #fff;
    padding: 0 30rpx 30rpx;
}

.amount-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
}

.amount-label {
    font-size: 28rpx;
    color: #666;
}

.amount-value {
    font-size: 28rpx;
    color: #333;
}

.amount-item.total {
    font-weight: bold;
}

/* 底部提交栏 */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 100rpx;
    background-color: #fff;
    display: flex;
    align-items: center;
    padding: 0 30rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
    z-index: 99;
}

.total-info {
    flex: 1;
}

.total-label {
    font-size: 28rpx;
    color: #333;
}

.total-price {
    font-size: 36rpx;
    color: var(--price-color);
    font-weight: bold;
}

.submit-btn {
    width: 240rpx;
    height: 80rpx;
    background-color: var(--primary-color);
    color: #fff;
    font-size: 30rpx;
    border-radius: 40rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0;
}

.submit-btn.loading {
    opacity: 0.8;
}
</style>
