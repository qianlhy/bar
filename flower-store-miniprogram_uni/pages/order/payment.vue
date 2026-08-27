<template>
    <view class="payment-container">
        <view class="payment-header">
            <view class="amount-label">待支付金额</view>
            <view class="amount">
                <text class="amount-symbol">¥</text>
                <text class="amount-number">{{ amountText }}</text>
            </view>
            <view class="countdown">
                <text class="countdown-dot"></text>
                <text>支付剩余 {{ countdownFormatted }}</text>
            </view>
            <view class="order-id">订单号 {{ orderId }}</view>
            <view class="coins-avail">可用 All In 币 {{ coins }}</view>
        </view>

        <view class="payment-methods">
            <view class="section-head">选择支付方式</view>

            <view
                :class="'method-item ' + (paymentMethod === 'coins' ? 'selected' : '') + (coinsEnough ? '' : ' disabled')"
                data-method="coins"
                @tap="switchPaymentMethod"
            >
                <view class="method-icon coins-icon"></view>
                <view class="method-text">
                    <view class="method-name">All In 币支付</view>
                    <view class="method-desc">{{ coinsEnough ? ('将扣除 ' + needCoins + ' 币') : '币不足，请充值或选其他方式' }}</view>
                </view>
                <view class="method-check" v-if="paymentMethod === 'coins'">✓</view>
            </view>

            <view :class="'method-item ' + (paymentMethod === 'wechat' ? 'selected' : '')" data-method="wechat" @tap="switchPaymentMethod">
                <view class="method-icon wechat-icon"></view>
                <view class="method-text">
                    <view class="method-name">微信支付</view>
                    <view class="method-desc">微信全额支付 ¥{{ amountText }}</view>
                </view>
                <view class="method-check" v-if="paymentMethod === 'wechat'">✓</view>
            </view>

            <view :class="'method-item ' + (paymentMethod === 'mixed' ? 'selected' : '')" data-method="mixed" @tap="switchPaymentMethod">
                <view class="method-icon mixed-icon"></view>
                <view class="method-text">
                    <view class="method-name">混合支付</view>
                    <view class="method-desc">优先扣 {{ mixedCoins }} 币，微信再付 ¥{{ mixedWechatText }}</view>
                </view>
                <view class="method-check" v-if="paymentMethod === 'mixed'">✓</view>
            </view>
        </view>

        <view class="payment-tip">
            <text>1 All In 币 = 1 元。混合支付在微信成功后再扣币；取消微信不会扣币。</text>
        </view>

        <view class="payment-footer">
            <button class="cancel-btn" @tap="cancelPayment">取消支付</button>
            <button :class="'pay-btn ' + (isLoading ? 'loading' : '')" @tap="payNow" :disabled="isLoading">
                {{ payButtonText }}
            </button>
        </view>
    </view>
</template>

<script>
const orderApi = require('../../api/order');
const payApi = require('../../api/pay');
const userApi = require('../../api/user');
export default {
    data() {
        return {
            orderId: '',
            amount: 0,
            coins: 0,
            paymentMethod: 'wechat',
            isLoading: false,
            countdown: 900,
            countdownFormatted: '15:00'
        };
    },
    computed: {
        amountText() {
            return Number(this.amount || 0).toFixed(2);
        },
        needCoins() {
            return Math.ceil(Number(this.amount || 0));
        },
        coinsEnough() {
            return this.coins >= this.needCoins && this.coins >= Number(this.amount || 0);
        },
        mixedCoins() {
            return Math.min(this.coins, Math.floor(Number(this.amount || 0)));
        },
        mixedWechat() {
            const left = Number(this.amount || 0) - this.mixedCoins;
            return left > 0 ? left : 0;
        },
        mixedWechatText() {
            return this.mixedWechat.toFixed(2);
        },
        payButtonText() {
            if (this.isLoading) return '支付中...';
            if (this.paymentMethod === 'coins') return '币支付';
            if (this.paymentMethod === 'mixed') {
                return this.mixedWechat > 0 ? ('微信付 ¥' + this.mixedWechatText) : '币支付';
            }
            return '立即支付';
        }
    },
    onLoad: function (options) {
        const { id, amount } = options;
        if (id && amount) {
            this.orderId = id;
            this.amount = parseFloat(amount);
            this.loadCoins();
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
        if (this.countdownTimer) {
            clearInterval(this.countdownTimer);
        }
    },
    methods: {
        loadCoins() {
            const token = uni.getStorageSync('token');
            if (!token) {
                this.coins = 0;
                return;
            }
            userApi.getUserInfo().then((data) => {
                this.coins = data.coins || 0;
                if (this.coinsEnough) {
                    this.paymentMethod = 'coins';
                } else if (this.coins > 0) {
                    this.paymentMethod = 'mixed';
                } else {
                    this.paymentMethod = 'wechat';
                }
            }).catch(() => {});
        },
        startCountdown: function () {
            this.updateFormattedCountdown();
            this.countdownTimer = setInterval(() => {
                let countdown = this.countdown - 1;
                if (countdown <= 0) {
                    clearInterval(this.countdownTimer);
                    this.handlePaymentTimeout();
                }
                this.countdown = countdown;
                this.updateFormattedCountdown();
            }, 1000);
        },
        formatCountdown: function () {
            const { countdown } = this;
            const minutes = Math.floor(countdown / 60);
            const seconds = countdown % 60;
            return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        },
        updateFormattedCountdown: function () {
            this.countdownFormatted = this.formatCountdown();
        },
        switchPaymentMethod: function (e) {
            const { method } = e.currentTarget.dataset;
            if (method === 'coins' && !this.coinsEnough) {
                uni.showToast({ title: 'All In 币不足', icon: 'none' });
                return;
            }
            this.paymentMethod = method;
        },
        payNow: function () {
            const { orderId, paymentMethod } = this;
            if (paymentMethod === 'coins' && !this.coinsEnough) {
                uni.showToast({ title: 'All In 币不足', icon: 'none' });
                return;
            }

            this.isLoading = true;

            payApi
                .payOrder(orderId, { payMethod: paymentMethod })
                .then((result) => {
                    if (result && result.paid) {
                        return Promise.resolve({ paid: true });
                    }
                    return payApi.requestPayment(result).then(() => {
                        // 兜底查询，确保混合扣币与状态落库
                        return orderApi.getOrderDetail(orderId).then((order) => {
                            if (order && order.orderNo) {
                                return payApi.queryPayResult(order.orderNo).catch(() => false);
                            }
                            return false;
                        }).catch(() => false);
                    });
                })
                .then(() => {
                    this.isLoading = false;
                    if (this.countdownTimer) {
                        clearInterval(this.countdownTimer);
                    }
                    uni.showToast({ title: '支付成功', icon: 'success' });
                    setTimeout(() => {
                        uni.redirectTo({ url: `/pages/order/detail?id=${orderId}` });
                    }, 1500);
                })
                .catch((err) => {
                    this.isLoading = false;
                    const cancelled = err && typeof err.errMsg === 'string' && err.errMsg.indexOf('cancel') > -1;
                    if (!cancelled) {
                        console.error('支付失败', err);
                        const msg = (err && err.message) || (typeof err === 'string' ? err : '') || '支付未完成';
                        uni.showToast({ title: msg.length > 20 ? '支付未完成' : msg, icon: 'none' });
                    }
                });
        },
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
        handlePaymentTimeout: function () {
            const { orderId } = this;
            orderApi
                .cancelOrder(orderId)
                .then(() => {
                    uni.showModal({
                        title: '支付超时',
                        content: '订单已自动取消',
                        showCancel: false,
                        success: () => {
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
.payment-container {
    min-height: 100vh;
    box-sizing: border-box;
    background: var(--bg-page);
    padding: 0 24rpx calc(180rpx + env(safe-area-inset-bottom));
}

.payment-header {
    margin-top: 24rpx;
    padding: 56rpx 40rpx 48rpx;
    text-align: center;
    border: 1rpx solid var(--border-gold);
    border-radius: 26rpx;
    background:
        radial-gradient(circle at 50% 0, rgba(232, 197, 71, 0.16), transparent 62%),
        var(--bg-card-gradient);
    box-shadow: 0 16rpx 40rpx rgba(0, 0, 0, 0.35);
}

.amount-label {
    color: var(--text-muted);
    font-size: 25rpx;
    letter-spacing: 2rpx;
}

.amount {
    display: flex;
    align-items: baseline;
    justify-content: center;
    margin-top: 18rpx;
    color: var(--gold);
}

.amount-symbol {
    font-size: 38rpx;
    font-weight: 600;
    margin-right: 6rpx;
}

.amount-number {
    font-size: 82rpx;
    font-weight: 700;
    line-height: 1.1;
    letter-spacing: 1rpx;
}

.countdown {
    display: inline-flex;
    align-items: center;
    margin-top: 26rpx;
    padding: 10rpx 26rpx;
    border-radius: 30rpx;
    background: rgba(232, 197, 71, 0.1);
    border: 1rpx solid rgba(232, 197, 71, 0.26);
    color: var(--gold-light);
    font-size: 25rpx;
}

.countdown-dot {
    width: 10rpx;
    height: 10rpx;
    margin-right: 12rpx;
    border-radius: 50%;
    background: var(--gold);
}

.order-id {
    margin-top: 22rpx;
    color: var(--text-faint);
    font-size: 23rpx;
}

.coins-avail {
    margin-top: 12rpx;
    color: var(--gold);
    font-size: 24rpx;
}

.payment-methods {
    margin-top: 32rpx;
    padding: 28rpx 28rpx 8rpx;
    border: 1rpx solid var(--border-subtle);
    border-radius: 22rpx;
    background: var(--bg-card-gradient);
}

.section-head {
    color: var(--text-muted);
    font-size: 24rpx;
    margin-bottom: 18rpx;
}

.method-item {
    display: flex;
    align-items: center;
    padding: 26rpx 24rpx;
    margin-bottom: 20rpx;
    border-radius: 18rpx;
    border: 2rpx solid transparent;
    background: var(--bg-sunken);
}

.method-item.selected {
    border-color: rgba(232, 197, 71, 0.5);
    background: rgba(232, 197, 71, 0.07);
}

.method-item.disabled {
    opacity: 0.45;
}

.method-icon {
    width: 68rpx;
    height: 68rpx;
    margin-right: 22rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #ffffff;
    font-weight: bold;
    font-size: 28rpx;
    flex-shrink: 0;
    border-radius: 16rpx;
}

.wechat-icon {
    background: linear-gradient(135deg, #2ecc4a, #07b83c);
}
.wechat-icon::before { content: '微'; }

.coins-icon {
    background: linear-gradient(135deg, #f0d878, #c9a227);
    color: #171717;
}
.coins-icon::before { content: '币'; }

.mixed-icon {
    background: linear-gradient(135deg, #5b8def, #3a5fc8);
}
.mixed-icon::before { content: '混'; }

.method-text {
    flex: 1;
    min-width: 0;
}

.method-name {
    color: var(--text-primary);
    font-size: 30rpx;
    font-weight: 600;
}

.method-desc {
    margin-top: 6rpx;
    color: var(--text-faint);
    font-size: 22rpx;
}

.method-check {
    color: var(--gold);
    font-size: 36rpx;
    font-weight: bold;
}

.payment-tip {
    margin-top: 26rpx;
    padding: 0 12rpx;
    color: var(--text-faint);
    font-size: 22rpx;
    line-height: 1.7;
    text-align: center;
}

.payment-footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    align-items: center;
    padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
    background: rgba(16, 16, 18, 0.96);
    border-top: 1rpx solid var(--border-subtle);
}

.cancel-btn,
.pay-btn {
    height: 92rpx;
    line-height: 92rpx;
    border-radius: 46rpx;
    font-size: 30rpx;
    font-weight: 600;
    border: none;
}

.cancel-btn::after,
.pay-btn::after {
    border: none;
}

.cancel-btn {
    flex: 1;
    margin-right: 20rpx;
    background: var(--bg-elevated);
    color: var(--text-regular);
}

.pay-btn {
    flex: 2;
    background: var(--gold-gradient);
    color: #171717;
    box-shadow: 0 10rpx 26rpx rgba(232, 197, 71, 0.24);
}

.pay-btn.loading {
    background: var(--bg-elevated);
    color: var(--text-faint);
    box-shadow: none;
}
</style>
