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
                <view class="store-copy">
                    <text class="store-name">梭哈酒馆 · 南京店</text>
                    <text class="store-address">南京市浦口区江浦街道</text>
                </view>
                <view class="instore-tag">店内</view>
            </view>
        </view>

        <!-- 商品列表 -->
        <view class="goods-section">
            <view class="section-title">商品清单</view>
            <view class="goods-list">
                <view class="goods-item" v-for="(item, index) in orderItems" :key="index">
                    <image
                        class="goods-image"
                        :src="item.image || '/static/allIn.jpg'"
                        mode="aspectFill"
                        @error="onGoodsImageError(index)"
                    ></image>

                    <view class="goods-info">
                        <text class="goods-name text-ellipsis">{{ item.name }}</text>
                        <text class="goods-spec" v-if="item.specText">{{ item.specText }}</text>
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
                    <view class="payment-icon wechat-pay">微</view>
                    <view class="payment-copy">
                        <text class="payment-name">微信支付</text>
                        <text class="payment-desc">安全快捷支付</text>
                    </view>
                    <view class="radio-box">
                        <view :class="'radio ' + (paymentMethod === 'online' ? 'selected' : '')"></view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 积分抵扣 -->
        <view class="points-section" v-if="pointsEnabled">
            <view class="section-title">积分抵扣</view>
            <view class="points-row" @tap="toggleUsePoints">
                <view class="points-left">
                    <text class="points-label">使用积分</text>
                    <text class="points-desc">可用 {{ availablePoints }} 分 · 100分抵1元 · 最多抵50%</text>
                </view>
                <view :class="'points-switch ' + (usePointsToggle ? 'on' : '')">
                    <view class="points-switch-dot"></view>
                </view>
            </view>
            <view class="points-row" v-if="usePointsToggle">
                <text class="points-label">抵扣</text>
                <text class="points-value">-{{ usePoints }}分 / ¥{{ pointsDiscount }}</text>
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
                <text class="amount-value">¥{{ formatMoney(totalPrice) }}</text>
            </view>
            <view class="amount-item">
                <text class="amount-label">服务费</text>
                <text class="amount-value">¥0.00</text>
            </view>
            <view class="amount-item" v-if="usePointsToggle && pointsDiscount > 0">
                <text class="amount-label">积分抵扣</text>
                <text class="amount-value discount">-¥{{ formatMoney(pointsDiscount) }}</text>
            </view>
            <view class="divider"></view>
            <view class="amount-item total">
                <text class="amount-label">实付款</text>
                <text class="amount-value price">¥{{ formatMoney(actualPayment) }}</text>
            </view>
        </view>

        <!-- 底部提交栏 -->
        <view class="footer">
            <view class="total-info">
                <text class="total-label">合计：</text>
                <text class="total-price">¥{{ formatMoney(actualPayment) }}</text>
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
const pointsApi = require('../../api/points');
const { ensureAdultDrinking } = require('../../utils/adult-verification');
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
            isLoading: false,
            pointsEnabled: false,
            availablePoints: 0,
            usePointsToggle: false,
            usePoints: 0,
            pointsDiscount: 0,
            adultAccessGranted: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        ensureAdultDrinking()
            .then(() => {
                this.adultAccessGranted = true;
                this.getCheckoutItems();
            })
            .catch(() => {
                const pages = getCurrentPages();
                if (pages.length > 1) {
                    uni.navigateBack();
                    return;
                }
                uni.switchTab({ url: '/pages/index/index' });
            });
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
            this.loadPointsPreview();
        },

        loadPointsPreview: function () {
            const amount = this.totalPrice;
            if (!amount) {
                return;
            }
            pointsApi
                .previewPoints(amount)
                .then((data) => {
                    const enabled = !!data.enabled && (data.usablePoints || 0) >= 0;
                    this.setData({
                        pointsEnabled: !!data.enabled,
                        availablePoints: data.availablePoints || 0,
                        usePoints: data.usablePoints || 0,
                        pointsDiscount: Number(data.discountAmount || 0)
                    });
                    if (this.usePointsToggle) {
                        this.recalcPayment();
                    }
                })
                .catch(() => {
                    this.setData({ pointsEnabled: false });
                });
        },

        toggleUsePoints: function () {
            if (!this.pointsEnabled) {
                return;
            }
            const next = !this.usePointsToggle;
            this.setData({ usePointsToggle: next });
            this.recalcPayment();
        },

        recalcPayment: function () {
            let pay = Number(this.totalPrice) || 0;
            if (this.usePointsToggle) {
                pay = Math.max(0, pay - Number(this.pointsDiscount || 0));
            }
            this.setData({ actualPayment: Number(pay.toFixed(2)) });
        },

        inputTableNo: function (e) {
            this.setData({ tableNo: e.detail.value });
        },

        onGoodsImageError: function (index) {
            const orderItems = this.orderItems.slice();
            if (orderItems[index]) {
                orderItems[index] = {
                    ...orderItems[index],
                    image: '/static/allIn.jpg'
                };
                this.setData({ orderItems });
            }
        },

        formatMoney: function (value) {
            return Number(value || 0).toFixed(2);
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
            if (!this.adultAccessGranted) {
                uni.showToast({ title: '请先确认已满18周岁', icon: 'none' });
                return;
            }
            const { orderItems, paymentMethod, remark, tableNo } = this;
            const userInfo = uni.getStorageSync('userInfo') || {};

            this.setData({ isLoading: true });

            const orderData = {
                receiverName: userInfo.nickname || userInfo.nickName || '店内顾客',
                receiverPhone: userInfo.phone || '00000000000',
                province: '江苏省',
                city: '南京市',
                district: '浦口区',
                address: tableNo ? `桌台号: ${tableNo}` : '梭哈酒馆-南京店 店内',
                paymentMethod: paymentMethod,
                remark: remark,
                usePoints: this.usePointsToggle ? this.usePoints : 0,
                items: orderItems.map((item) => ({
                    cartId: item.id || null,
                    productId: item.productId,
                    productName: item.name,
                    productImage: item.image,
                    specText: item.specText || '',
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

                    // 跳转到支付页完成付款
                    setTimeout(() => {
                        const isZeroOrder = Number(order.actualPayment || 0) <= 0;
                        uni.redirectTo({
                            url: isZeroOrder
                                ? `/pages/order/detail?id=${order.id}`
                                : `/pages/order/payment?id=${order.id}&amount=${order.actualPayment}`
                        });
                    }, 1000);
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
.order-container {
    min-height: 100vh;
    box-sizing: border-box;
    background:
        radial-gradient(circle at 100% 0, rgba(232, 197, 71, 0.08), transparent 34%),
        #0b0b0c;
    padding: 20rpx 20rpx calc(140rpx + env(safe-area-inset-bottom));
}

.section-title {
    position: relative;
    padding: 4rpx 0 24rpx 22rpx;
    color: #f7f7f7;
    font-size: 30rpx;
    font-weight: 600;
    letter-spacing: 1rpx;
}

.section-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 7rpx;
    width: 6rpx;
    height: 30rpx;
    border-radius: 4rpx;
    background: linear-gradient(180deg, #f7dc8a, #c99a3a);
}

.divider {
    height: 1rpx;
    background: rgba(255, 255, 255, 0.07);
    margin: 20rpx 0;
}

.table-section,
.goods-section,
.payment-section,
.points-section,
.remark-section,
.amount-section {
    box-sizing: border-box;
    margin-bottom: 20rpx;
    padding: 28rpx;
    overflow: hidden;
    border: 1rpx solid rgba(232, 197, 71, 0.1);
    border-radius: 22rpx;
    background: linear-gradient(145deg, #202022, #18181a);
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.24);
}

.table-section {
    border-color: rgba(232, 197, 71, 0.18);
}

.table-info {
    display: flex;
    align-items: center;
    box-sizing: border-box;
    height: 92rpx;
    padding: 0 24rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.07);
    border-radius: 14rpx;
    background: #2a2a2d;
}

.table-label {
    margin-right: 22rpx;
    color: #d3d3d3;
    font-size: 27rpx;
}

.table-input {
    flex: 1;
    height: 92rpx;
    color: #fff;
    font-size: 27rpx;
}

.store-tag-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 24rpx;
}

.store-copy {
    display: flex;
    flex-direction: column;
}

.store-name {
    color: #f1f1f1;
    font-size: 27rpx;
    font-weight: 600;
}

.store-address {
    margin-top: 6rpx;
    color: #777;
    font-size: 21rpx;
}

.instore-tag {
    padding: 7rpx 20rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.4);
    border-radius: 22rpx;
    background: rgba(232, 197, 71, 0.12);
    color: #f3d780;
    font-size: 21rpx;
    font-weight: 600;
}

.goods-list {
    display: flex;
    flex-direction: column;
    gap: 22rpx;
}

.goods-item {
    display: flex;
    min-width: 0;
    padding: 18rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.05);
    border-radius: 16rpx;
    background: rgba(255, 255, 255, 0.025);
}

.goods-image {
    width: 146rpx;
    height: 146rpx;
    flex-shrink: 0;
    margin-right: 22rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.12);
    border-radius: 14rpx;
    background: #29292c;
}

.goods-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.goods-name {
    display: block;
    overflow: hidden;
    color: #f5f5f5;
    font-size: 29rpx;
    font-weight: 600;
    line-height: 1.45;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-bottom: 10rpx;
}

.goods-spec {
    display: block;
    margin-top: 4rpx;
    color: var(--text-muted);
    font-size: 23rpx;
}

.goods-price-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price {
    color: #f3d780;
    font-weight: bold;
    font-size: 34rpx;
}

.count {
    color: #8a8a8e;
    font-size: 25rpx;
}

.payment-options {
    overflow: hidden;
    border: 1rpx solid rgba(255, 255, 255, 0.05);
    border-radius: 16rpx;
    background: rgba(255, 255, 255, 0.025);
}

.payment-option {
    display: flex;
    align-items: center;
    padding: 22rpx;
}

.payment-icon {
    width: 58rpx;
    height: 58rpx;
    flex-shrink: 0;
    margin-right: 18rpx;
    border-radius: 14rpx;
    color: #fff;
    font-size: 24rpx;
    font-weight: bold;
    line-height: 58rpx;
    text-align: center;
}

.wechat-pay {
    background: linear-gradient(135deg, #21c66b, #08a849);
}

.payment-copy {
    flex: 1;
    display: flex;
    flex-direction: column;
}

.payment-name {
    color: #f4f4f4;
    font-size: 28rpx;
    font-weight: 500;
}

.payment-desc {
    margin-top: 4rpx;
    color: #777;
    font-size: 21rpx;
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
    box-sizing: border-box;
    position: relative;
    border: 2rpx solid #555;
    border-radius: 50%;
}

.radio.selected {
    border-color: #e8c547;
}

.radio.selected::after {
    content: '';
    position: absolute;
    width: 16rpx;
    height: 16rpx;
    border-radius: 50%;
    background: #e8c547;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}

.points-section {
    padding-bottom: 20rpx;
}

.points-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
}

.points-left {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
}

.points-label {
    font-size: 28rpx;
    color: #f4f4f4;
}

.points-desc {
    font-size: 22rpx;
    color: #8a8a8e;
}

.points-value {
    font-size: 28rpx;
    color: #e8c547;
    font-weight: bold;
}

.points-switch {
    width: 88rpx;
    height: 48rpx;
    border-radius: 24rpx;
    background: #3a3a3c;
    position: relative;
    transition: background 0.2s;
}

.points-switch.on {
    background: #e8c547;
}

.points-switch-dot {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    background: #fff;
    position: absolute;
    top: 4rpx;
    left: 4rpx;
    transition: left 0.2s;
}

.points-switch.on .points-switch-dot {
    left: 44rpx;
}

.amount-value.discount {
    color: #e8c547;
}

.remark-input {
    box-sizing: border-box;
    width: 100%;
    height: 150rpx;
    padding: 22rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.06);
    border-radius: 14rpx;
    background: #29292c;
    color: #eee;
    font-size: 27rpx;
}

.amount-section {
    margin-bottom: 0;
}

.amount-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16rpx 0;
}

.amount-label {
    color: #999;
    font-size: 26rpx;
}

.amount-value {
    color: #ddd;
    font-size: 27rpx;
}

.amount-item.total {
    padding-top: 6rpx;
    font-weight: bold;
}

.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    box-sizing: content-box;
    height: 110rpx;
    padding: 0 24rpx env(safe-area-inset-bottom);
    border-top: 1rpx solid rgba(232, 197, 71, 0.12);
    background: rgba(22, 22, 24, 0.98);
    display: flex;
    align-items: center;
    box-shadow: 0 -8rpx 28rpx rgba(0, 0, 0, 0.42);
    z-index: 99;
}

.total-info {
    flex: 1;
}

.total-label {
    color: #999;
    font-size: 25rpx;
}

.total-price {
    color: #f3d780;
    font-size: 38rpx;
    font-weight: bold;
}

.submit-btn {
    width: 250rpx;
    height: 78rpx;
    margin: 0;
    border: none;
    border-radius: 39rpx;
    background: linear-gradient(135deg, #f7dc8a, #d4a72c);
    box-shadow: 0 8rpx 24rpx rgba(232, 197, 71, 0.22);
    color: #171717;
    font-size: 29rpx;
    font-weight: 600;
    display: flex;
    justify-content: center;
    align-items: center;
}

.submit-btn::after {
    border: none;
}

.submit-btn.loading {
    opacity: 0.65;
}
</style>
