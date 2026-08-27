<template>
    <view class="recharge-page">
        <view class="balance-card">
            <text class="balance-label">当前 All In 币</text>
            <text class="balance-value">{{ coins }}</text>
        </view>

        <view class="tip">{{ rechargeTip }}</view>

        <view class="package-list">
            <view
                v-for="(item, index) in packages"
                :key="item.id"
                :class="'package-item ' + (selectedIndex === index ? 'active' : '')"
                @tap="selectPackage(index)"
            >
                <text class="pkg-name">{{ item.name }}</text>
                <text class="pkg-balance">到账 {{ creditCoins(item) }} 币</text>
                <text class="pkg-gift" v-if="item.giftCoins > 0">含赠送 {{ item.giftCoins }} 币</text>
                <text class="pkg-pay">实付 ¥{{ item.payAmount }}</text>
            </view>
            <view class="empty" v-if="!isLoading && packages.length === 0">
                <text>暂无充值套餐</text>
            </view>
        </view>

        <view class="footer">
            <view class="footer-info">
                <text class="footer-label">实付：</text>
                <text class="footer-price">¥{{ currentPayAmount }}</text>
                <text class="footer-credit" v-if="selectedIndex >= 0"> · 到账 {{ currentCreditCoins }} 币</text>
            </view>
            <view :class="'pay-btn ' + (selectedIndex < 0 || submitting ? 'disabled' : '')" @tap="submit">
                {{ submitting ? '提交中...' : '立即充值' }}
            </view>
        </view>
    </view>
</template>

<script>
const rechargeApi = require('../../api/recharge');
const configApi = require('../../api/config');
const userApi = require('../../api/user');
const payApi = require('../../api/pay');
export default {
    data() {
        return {
            coins: 0,
            packages: [],
            selectedIndex: -1,
            isLoading: true,
            submitting: false,
            rechargeTip: '充值金额全部到账为 All In 币，可用于点单支付（1币=1元）'
        };
    },
    computed: {
        currentPayAmount() {
            if (this.selectedIndex < 0) return '0.00';
            const p = this.packages[this.selectedIndex];
            return p ? p.payAmount : '0.00';
        },
        currentCreditCoins() {
            if (this.selectedIndex < 0) return 0;
            return this.creditCoins(this.packages[this.selectedIndex]);
        }
    },
    onShow() {
        this.loadUser();
        this.loadPackages();
        this.loadConfig();
    },
    methods: {
        creditCoins(pkg) {
            if (!pkg) return 0;
            const pay = Math.floor(Number(pkg.payAmount) || 0);
            const gift = Number(pkg.giftCoins) || 0;
            return pay + gift;
        },
        loadUser() {
            const token = uni.getStorageSync('token');
            if (!token) { this.coins = 0; return; }
            userApi.getUserInfo().then((data) => {
                this.coins = data.coins || 0;
            }).catch(() => {});
        },
        loadPackages() {
            this.isLoading = true;
            rechargeApi.getRechargePackages().then((list) => {
                this.packages = list || [];
                this.isLoading = false;
            }).catch(() => { this.isLoading = false; });
        },
        loadConfig() {
            configApi.getPublicConfig().then((cfg) => {
                if (cfg && cfg.recharge_tip) this.rechargeTip = cfg.recharge_tip;
            }).catch(() => {});
        },
        selectPackage(index) {
            this.selectedIndex = index;
        },
        submit() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1500);
                return;
            }
            if (this.selectedIndex < 0) {
                uni.showToast({ title: '请选择充值套餐', icon: 'none' });
                return;
            }
            const pkg = this.packages[this.selectedIndex];
            this.submitting = true;
            let orderNo = '';
            rechargeApi.createRecharge(pkg.id)
                .then((order) => {
                    orderNo = order.orderNo || '';
                    return payApi.payRecharge(order.id);
                })
                .then((params) => payApi.requestPayment(params))
                .then(() => {
                    if (orderNo) {
                        return payApi.queryPayResult(orderNo).catch(() => false);
                    }
                    return false;
                })
                .then(() => {
                    this.submitting = false;
                    uni.showToast({ title: '充值成功', icon: 'success' });
                    this.loadUser();
                })
                .catch((err) => {
                    this.submitting = false;
                    const cancelled = err && typeof err.errMsg === 'string' && err.errMsg.indexOf('cancel') > -1;
                    if (!cancelled) {
                        console.error('充值支付失败', err);
                        uni.showToast({ title: '支付未完成', icon: 'none' });
                    }
                });
        }
    }
};
</script>

<style>
.recharge-page {
    min-height: 100vh;
    background: #0a0a0a;
    padding: 30rpx;
    padding-bottom: 140rpx;
}
.balance-card {
    background: linear-gradient(135deg, #3a3a3c 0%, #2c2c2e 100%);
    border-radius: 20rpx;
    padding: 40rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.balance-label { font-size: 24rpx; color: #aaa; }
.balance-value { font-size: 60rpx; color: #e8c547; font-weight: bold; margin-top: 10rpx; }
.tip { font-size: 24rpx; color: #999; text-align: center; margin: 24rpx 0; line-height: 1.6; }
.package-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
}
.package-item {
    width: calc(50% - 10rpx);
    box-sizing: border-box;
    background: #1c1c1e;
    border-radius: 16rpx;
    padding: 28rpx 20rpx;
    border: 2rpx solid transparent;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.package-item.active {
    border-color: #e8c547;
    background: rgba(232, 197, 71, 0.08);
}
.pkg-name { font-size: 28rpx; color: #fff; font-weight: 600; }
.pkg-balance { font-size: 32rpx; color: #e8c547; font-weight: bold; margin-top: 12rpx; }
.pkg-gift { font-size: 22rpx; color: #c9a227; margin-top: 8rpx; }
.pkg-pay { font-size: 24rpx; color: #999; margin-top: 10rpx; }
.empty { width: 100%; text-align: center; color: #666; padding: 60rpx 0; }
.footer {
    position: fixed;
    left: 0; right: 0; bottom: 0;
    display: flex;
    align-items: center;
    padding: 20rpx 30rpx calc(20rpx + env(safe-area-inset-bottom));
    background: #141416;
    border-top: 1rpx solid #2a2a2c;
}
.footer-info { flex: 1; }
.footer-label { font-size: 24rpx; color: #999; }
.footer-price { font-size: 36rpx; color: #e8c547; font-weight: bold; }
.footer-credit { font-size: 22rpx; color: #aaa; }
.pay-btn {
    background: linear-gradient(135deg, #f0d878, #e8c547);
    color: #171717;
    font-size: 28rpx;
    font-weight: 700;
    padding: 22rpx 48rpx;
    border-radius: 40rpx;
}
.pay-btn.disabled { opacity: 0.45; }
</style>
