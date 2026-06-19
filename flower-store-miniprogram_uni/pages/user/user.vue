<template>
    <view class="user-page">
        <!-- 用户信息 -->
        <view class="user-header">
            <image class="user-avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
            <view class="user-text" v-if="isLogin">
                <text class="user-greet">{{ userInfo.nickname || '欢迎加入' }}</text>
                <text class="user-sub">注册后解锁更多会员特权</text>
            </view>
            <view class="user-text" v-else>
                <text class="user-greet">欢迎加入</text>
                <text class="user-sub">注册后解锁更多会员特权</text>
            </view>
            <view class="register-btn" @tap="goToLogin" v-if="!isLogin">注册会员</view>
            <view class="register-btn" @tap="goSettings" v-else>个人中心</view>
        </view>

        <!-- 资产统计 -->
        <view class="asset-card">
            <view class="asset-item">
                <text class="asset-label">余额</text>
                <text class="asset-value">{{ userInfo.balance || 0 }}</text>
            </view>
            <view class="asset-divider"></view>
            <view class="asset-item">
                <text class="asset-label">优惠券</text>
                <text class="asset-value">{{ userInfo.couponCount || 0 }}</text>
            </view>
            <view class="asset-divider"></view>
            <view class="asset-item">
                <text class="asset-label">27币</text>
                <text class="asset-value">{{ userInfo.coins || 0 }}</text>
            </view>
        </view>

        <!-- 常用功能 -->
        <view class="func-card">
            <text class="func-title">常用功能</text>
            <view class="func-grid">
                <view class="func-item" @tap="goSettings">
                    <text class="func-icon">👤</text>
                    <text class="func-name">个人中心</text>
                </view>
                <view class="func-item" @tap="navigateTo" data-url="/pages/address/list">
                    <text class="func-icon">📍</text>
                    <text class="func-name">我的地址</text>
                </view>
                <view class="func-item" @tap="goCoinMall">
                    <text class="func-icon">⚡</text>
                    <text class="func-name">27币商城</text>
                </view>
                <view class="func-item" @tap="showCoupon">
                    <text class="func-icon">🎫</text>
                    <text class="func-name">我的优惠券</text>
                </view>
                <view class="func-item" @tap="goOrders">
                    <text class="func-icon">📋</text>
                    <text class="func-name">订单中心</text>
                </view>
                <view class="func-item" @tap="goRank">
                    <text class="func-icon">🃏</text>
                    <text class="func-name">大师分</text>
                </view>
                <view class="func-item" @tap="contactUs">
                    <text class="func-icon">💬</text>
                    <text class="func-name">联系我们</text>
                </view>
            </view>
        </view>

        <!-- 门店信息 -->
        <view class="store-card">
            <view class="store-top">
                <view class="store-logo">27</view>
                <text class="store-name">27 POKER BAR</text>
                <view class="store-contact" @tap="contactUs">联系我们</view>
            </view>
            <view class="store-msg">
                <text>HI~ 欢迎光临本店</text>
                <text>如有需要，请点击联系我们按钮为您快速服务~</text>
            </view>
        </view>

        <!-- 技术支持 -->
        <view class="footer">
            <text class="footer-icon">🔥</text>
            <text class="footer-text">熠火</text>
            <text class="footer-support">熠火提供技术支持</text>
        </view>
    </view>
</template>

<script>
const userApi = require('../../api/user');
export default {
    data() {
        return {
            userInfo: { balance: 0, coins: 0, couponCount: 0 },
            isLogin: false
        };
    },
    onShow() {
        this.checkLogin();
    },
    methods: {
        checkLogin() {
            const token = uni.getStorageSync('token');
            this.isLogin = !!token;
            if (!token) {
                this.userInfo = { balance: 0, coins: 0, couponCount: 0 };
                return;
            }
            userApi.getUserInfo().then((data) => {
                this.userInfo = data;
            }).catch(() => {});
        },
        goToLogin() {
            uni.navigateTo({ url: '/pages/login/login' });
        },
        goSettings() {
            if (!this.isLogin) return this.goToLogin();
            uni.navigateTo({ url: '/pages/settings/settings' });
        },
        navigateTo(e) {
            if (!this.isLogin) return this.goToLogin();
            uni.navigateTo({ url: e.currentTarget.dataset.url });
        },
        goCoinMall() {
            uni.navigateTo({ url: '/pages/coin-mall/coin-mall' });
        },
        showCoupon() {
            uni.showToast({ title: '暂无可用优惠券', icon: 'none' });
        },
        goOrders() {
            if (!this.isLogin) return this.goToLogin();
            uni.navigateTo({ url: '/pages/order/list' });
        },
        goRank() {
            uni.switchTab({ url: '/pages/rank/rank' });
        },
        contactUs() {
            uni.showModal({
                title: '联系我们',
                content: '电话: 027-88888888\n微信: 27POKER_BAR',
                showCancel: false
            });
        }
    }
};
</script>

<style>
.user-page {
    min-height: 100vh;
    background: #000;
    padding: 30rpx;
    padding-bottom: 60rpx;
}
.user-header {
    display: flex;
    align-items: center;
    padding: 20rpx 0 40rpx;
}
.user-avatar {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: #333;
    flex-shrink: 0;
}
.user-text { flex: 1; margin-left: 20rpx; }
.user-greet { font-size: 32rpx; color: #fff; font-weight: bold; display: block; }
.user-sub { font-size: 22rpx; color: #888; margin-top: 6rpx; display: block; }
.register-btn {
    background: #2c2c2e;
    color: #fff;
    font-size: 24rpx;
    padding: 12rpx 28rpx;
    border-radius: 30rpx;
    white-space: nowrap;
}

.asset-card {
    background: #1c1c1e;
    border-radius: 16rpx;
    display: flex;
    padding: 30rpx 0;
    margin-bottom: 24rpx;
}
.asset-item { flex: 1; text-align: center; }
.asset-label { font-size: 24rpx; color: #888; display: block; margin-bottom: 10rpx; }
.asset-value { font-size: 40rpx; color: #fff; font-weight: bold; display: block; }
.asset-divider { width: 1rpx; background: #333; align-self: stretch; }

.func-card {
    background: #1c1c1e;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 24rpx;
}
.func-title { font-size: 30rpx; color: #fff; font-weight: bold; display: block; margin-bottom: 30rpx; }
.func-grid {
    display: flex;
    flex-wrap: wrap;
}
.func-item {
    width: 25%;
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 30rpx;
}
.func-icon { font-size: 44rpx; margin-bottom: 10rpx; }
.func-name { font-size: 22rpx; color: #ccc; }

.store-card {
    background: #1c1c1e;
    border-radius: 16rpx;
    padding: 30rpx;
    margin-bottom: 30rpx;
}
.store-top {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
}
.store-logo {
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    background: #c41e3a;
    color: #fff;
    font-size: 24rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 16rpx;
}
.store-name { flex: 1; font-size: 28rpx; color: #fff; font-weight: bold; }
.store-contact {
    background: #2c2c2e;
    color: #fff;
    font-size: 22rpx;
    padding: 10rpx 24rpx;
    border-radius: 30rpx;
}
.store-msg {
    background: #141416;
    border-radius: 12rpx;
    padding: 24rpx;
}
.store-msg text {
    font-size: 24rpx;
    color: #999;
    line-height: 1.6;
    display: block;
}

.footer {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8rpx;
    padding: 20rpx 0;
}
.footer-icon { font-size: 24rpx; }
.footer-text { font-size: 24rpx; color: #666; }
.footer-support { font-size: 22rpx; color: #555; }
</style>
