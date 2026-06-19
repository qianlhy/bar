<template>
    <view class="payment-container">
        <view class="payment-header">
            <view class="amount">￥{{ amount }}</view>
            <view class="order-id">订单号：{{ orderId }}</view>
            <view class="countdown">支付剩余时间：{{ countdownFormatted }}</view>
        </view>

        <view class="payment-methods">
            <view class="section-title">选择支付方式</view>

            <view :class="'method-item ' + (paymentMethod === 'wechat' ? 'selected' : '')" data-method="wechat" @tap="switchPaymentMethod">
                <view class="method-icon wechat-icon"></view>
                <view class="method-name">微信支付</view>
                <view class="method-check" v-if="paymentMethod === 'wechat'">✓</view>
            </view>

            <view :class="'method-item ' + (paymentMethod === 'alipay' ? 'selected' : '')" data-method="alipay" @tap="switchPaymentMethod">
                <view class="method-icon alipay-icon"></view>
                <view class="method-name">支付宝</view>
                <view class="method-check" v-if="paymentMethod === 'alipay'">✓</view>
            </view>

            <view :class="'method-item ' + (paymentMethod === 'card' ? 'selected' : '')" data-method="card" @tap="switchPaymentMethod">
                <view class="method-icon card-icon"></view>
                <view class="method-name">银行卡支付</view>
                <view class="method-check" v-if="paymentMethod === 'card'">✓</view>
            </view>
        </view>

        <view class="payment-footer">
            <button class="cancel-btn" @tap="cancelPayment">取消支付</button>
            <button :class="'pay-btn ' + (isLoading ? 'loading' : '')" @tap="payNow" :disabled="isLoading">
                {{ isLoading ? '支付中...' : '立即支付' }}
            </button>
        </view>
    </view>
</template>

<script>
const orderApi = require('../../api/order');
export default {
    data() {
        return {
            orderId: '',
            amount: 0,
            paymentMethod: 'wechat',
            isLoading: false,
            countdown: 900,
            // 15分钟倒计时（秒）
            countdownFormatted: '15:00'
        };
    },
    onLoad: function (options) {
        const { id, amount } = options;
        if (id && amount) {
            this.setData({
                orderId: id,
                amount: parseFloat(amount)
            });

            // 开始倒计时
            this.startCountdown();
        } else {
            uni.showToast({
                title: '订单信息不完整',
                icon: 'none'
            });
            setTimeout(() => {
                uni.navigateBack();
            }, 1500);
        }
    },
    onUnload: function () {
        // 页面卸载时清除定时器
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
        }
    },
    methods: {
        // 开始倒计时
        startCountdown: function () {
            // 初始格式化倒计时
            this.updateFormattedCountdown();
            this.countdownTimer = setInterval(() => {
                let countdown = this.countdown - 1;
                if (countdown <= 0) {
                    // 倒计时结束，清除定时器
                    clearInterval(this.countdownTimer);

                    // 订单超时，更新订单状态
                    this.handlePaymentTimeout();
                }
                this.setData({
                    countdown
                });
                // 更新格式化后的倒计时显示
                this.updateFormattedCountdown();
            }, 1000);
        },

        // 格式化倒计时时间
        formatCountdown: function () {
            const { countdown } = this;
            const minutes = Math.floor(countdown / 60);
            const seconds = countdown % 60;
            return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        },

        // 更新格式化后的倒计时
        updateFormattedCountdown: function () {
            const formattedTime = this.formatCountdown();
            this.setData({
                countdownFormatted: formattedTime
            });
        },

        // 切换支付方式
        switchPaymentMethod: function (e) {
            const { method } = e.currentTarget.dataset;
            this.setData({
                paymentMethod: method
            });
        },

        // 立即支付
        payNow: function () {
            const { orderId } = this;

            // 显示加载状态
            this.setData({
                isLoading: true
            });

            // 更新订单状态为"待发货"
            orderApi
                .updateOrderStatus(orderId, 2)
                .then(() => {
                    // 隐藏加载状态
                    this.setData({
                        isLoading: false
                    });

                    // 显示支付成功提示
                    uni.showToast({
                        title: '支付成功',
                        icon: 'success'
                    });

                    // 跳转到订单详情页
                    setTimeout(() => {
                        uni.redirectTo({
                            url: `/pages/order/detail?id=${orderId}`
                        });
                    }, 1500);
                })
                .catch((err) => {
                    console.error('支付失败', err);
                    this.setData({
                        isLoading: false
                    });
                });
        },

        // 取消支付
        cancelPayment: function () {
            uni.showModal({
                title: '提示',
                content: '确定要取消支付吗？',
                success: (res) => {
                    if (res.confirm) {
                        uni.navigateBack();
                    }
                }
            });
        },

        // 处理支付超时
        handlePaymentTimeout: function () {
            const { orderId } = this;

            // 更新订单状态为"已取消"
            orderApi
                .cancelOrder(orderId)
                .then(() => {
                    // 显示支付超时提示
                    uni.showModal({
                        title: '支付超时',
                        content: '订单已自动取消',
                        showCancel: false,
                        success: () => {
                            // 返回上一页
                            uni.navigateBack();
                        }
                    });
                })
                .catch((err) => {
                    console.error('取消订单失败', err);
                });
        }
    }
};
</script>
<style>
/* pages/order/payment.wxss */
.payment-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
}

/* 支付头部 */
.payment-header {
    background-color: #ff6b81;
    color: #fff;
    padding: 40rpx 30rpx;
    text-align: center;
}

.amount {
    font-size: 60rpx;
    font-weight: bold;
    margin-bottom: 20rpx;
}

.order-id {
    font-size: 28rpx;
    margin-bottom: 10rpx;
    opacity: 0.8;
}

.countdown {
    font-size: 28rpx;
    background-color: rgba(255, 255, 255, 0.2);
    display: inline-block;
    padding: 10rpx 20rpx;
    border-radius: 30rpx;
    margin-top: 10rpx;
}

/* 支付方式 */
.payment-methods {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 0 30rpx;
}

.section-title {
    font-size: 32rpx;
    color: #333;
    padding: 30rpx 0;
    border-bottom: 1rpx solid #eee;
}

.method-item {
    display: flex;
    align-items: center;
    padding: 30rpx 0;
    border-bottom: 1rpx solid #eee;
}

.method-item:last-child {
    border-bottom: none;
}

.method-item.selected {
    background-color: #f9f9f9;
}

.method-icon {
    width: 60rpx;
    height: 60rpx;
    margin-right: 20rpx;
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 24rpx;
}

.wechat-icon {
    background-color: #09bb07;
    border-radius: 8rpx;
}

.wechat-icon::before {
    content: '微';
}

.alipay-icon {
    background-color: #1677ff;
    border-radius: 8rpx;
}

.alipay-icon::before {
    content: '支';
}

.card-icon {
    background-color: #ff9500;
    border-radius: 8rpx;
}

.card-icon::before {
    content: '卡';
}

.method-name {
    flex: 1;
    font-size: 30rpx;
}

.method-check {
    color: #ff6b81;
    font-size: 36rpx;
    font-weight: bold;
}

/* 底部按钮 */
.payment-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    padding: 20rpx 30rpx;
    display: flex;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.cancel-btn {
    flex: 1;
    background-color: #f5f5f5;
    color: #666;
    font-size: 30rpx;
    margin-right: 20rpx;
}

.pay-btn {
    flex: 2;
    background-color: #ff6b81;
    color: #fff;
    font-size: 30rpx;
}

.pay-btn.loading {
    background-color: #ffacb7;
}
</style>
