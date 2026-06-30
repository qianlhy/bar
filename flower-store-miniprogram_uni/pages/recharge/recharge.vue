<template>
    <view class="recharge-page">
        <view class="balance-card">
            <text class="balance-label">当前余额</text>
            <text class="balance-value">¥{{ balance }}</text>
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
                <text class="pkg-balance">到账 ¥{{ item.balance }}</text>
                <text class="pkg-gift" v-if="item.giftCoins > 0">送 {{ item.giftCoins }} All In币</text>
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
export default {
    data() {
        return {
            balance: 0,
            packages: [],
            selectedIndex: -1,
            isLoading: true,
            submitting: false,
            rechargeTip: '充值享受更多专属优惠福利'
        };
    },
    computed: {
        currentPayAmount() {
            if (this.selectedIndex < 0) return '0.00';
            const p = this.packages[this.selectedIndex];
            return p ? p.payAmount : '0.00';
        }
    },
    onShow() {
        this.loadUser();
        this.loadPackages();
        this.loadConfig();
    },
    methods: {
        loadUser() {
            const token = uni.getStorageSync('token');
            if (!token) { this.balance = 0; return; }
            userApi.getUserInfo().then((data) => {
                this.balance = data.balance || 0;
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
            rechargeApi.createRecharge(pkg.id).then(() => {
                this.submitting = false;
                // 微信支付能力预留：当前生成待支付订单，到账以门店确认为准
                uni.showModal({
                    title: '充值订单已提交',
                    content: '微信支付功能即将上线，当前订单已生成，请向店员出示订单完成到账。',
                    showCancel: false
                });
            }).catch(() => { this.submitting = false; });
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
.tip { font-size: 24rpx; color: #999; text-align: center; margin: 24rpx 0; }
.package-list {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
}
.package-item {
    width: calc(50% - 10rpx);
    box-sizing: border-box;
    background: #1c1c1e;
    border: 2rpx solid #2c2c2e;
    border-radius: 16rpx;
    padding: 30rpx 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
}
.package-item.active {
    border-color: #e8c547;
    background: #26240f;
}
.pkg-name { font-size: 30rpx; color: #fff; font-weight: bold; }
.pkg-balance { font-size: 24rpx; color: #e8c547; margin-top: 10rpx; }
.pkg-gift { font-size: 22rpx; color: #c41e3a; margin-top: 6rpx; }
.pkg-pay { font-size: 22rpx; color: #999; margin-top: 10rpx; }
.empty { width: 100%; text-align: center; color: #666; padding: 60rpx 0; font-size: 26rpx; }
.footer {
    position: fixed;
    left: 0; right: 0; bottom: 0;
    height: 110rpx;
    background: #1c1c1e;
    display: flex;
    align-items: center;
    padding: 0 30rpx;
}
.footer-info { flex: 1; }
.footer-label { font-size: 26rpx; color: #ccc; }
.footer-price { font-size: 38rpx; color: #e8c547; font-weight: bold; }
.pay-btn {
    background: #c41e3a;
    color: #fff;
    font-size: 30rpx;
    font-weight: bold;
    padding: 20rpx 50rpx;
    border-radius: 40rpx;
}
.pay-btn.disabled { opacity: 0.5; }
</style>
