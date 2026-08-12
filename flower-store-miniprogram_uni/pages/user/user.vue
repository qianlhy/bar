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
                <text class="asset-label">All In 币</text>
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
                    <text class="func-name">All In 币商城</text>
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
                <image class="store-logo" src="/static/allIn.jpg" mode="aspectFit"></image>
                <text class="store-name">梭哈酒馆</text>
                <view class="store-contact" @tap="contactUs">联系我们</view>
            </view>
            <view class="store-msg">
                <text>HI~ 欢迎光临本店</text>
                <text>如有需要，请点击联系我们按钮为您快速服务~</text>
            </view>
        </view>

        <!-- 退出登录 -->
        <view class="logout-btn" v-if="isLogin" @tap="logout">退出登录</view>

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
                content: '电话: 027-88888888\n微信: AllInTavern',
                showCancel: false
            });
        },
        logout() {
            uni.showModal({
                title: '退出登录',
                content: '确定要退出当前账号吗？',
                success: (res) => {
                    if (!res.confirm) {
                        return;
                    }
                    uni.removeStorageSync('token');
                    uni.removeStorageSync('userInfo');
                    uni.removeStorageSync('isLoggedIn');
                    uni.removeStorageSync('cartSpecs');
                    this.isLogin = false;
                    this.userInfo = { balance: 0, coins: 0, couponCount: 0 };
                    uni.showToast({ title: '已退出登录', icon: 'success' });
                }
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
    width: 104rpx;
    height: 104rpx;
    border-radius: 50%;
    background: #333;
    flex-shrink: 0;
    border: 3rpx solid #d4a72c;
    box-shadow: 0 0 20rpx rgba(232,197,71,0.25);
}
.user-text { flex: 1; margin-left: 22rpx; }
.user-greet { font-size: 34rpx; color: #fff; font-weight: bold; display: block; }
.user-sub { font-size: 22rpx; color: #888; margin-top: 8rpx; display: block; }
.register-btn {
    background: linear-gradient(135deg, #f7dc8a, #d4a72c);
    color: #000;
    font-size: 24rpx;
    font-weight: bold;
    padding: 14rpx 30rpx;
    border-radius: 30rpx;
    white-space: nowrap;
    box-shadow: 0 4rpx 12rpx rgba(232,197,71,0.3);
}

.asset-card {
    background: linear-gradient(135deg, #262628 0%, #1a1a1c 100%);
    border-radius: 20rpx;
    border: 1rpx solid rgba(232,197,71,0.12);
    display: flex;
    padding: 34rpx 0;
    margin-bottom: 24rpx;
    box-shadow: 0 4rpx 18rpx rgba(0,0,0,0.35);
}
.asset-item { flex: 1; text-align: center; }
.asset-label { font-size: 24rpx; color: #999; display: block; margin-bottom: 12rpx; }
.asset-value { font-size: 42rpx; color: #e8c547; font-weight: bold; display: block; }
.asset-divider { width: 1rpx; background: rgba(255,255,255,0.08); align-self: stretch; }

.func-card {
    background: #1c1c1e;
    border-radius: 20rpx;
    border: 1rpx solid rgba(255,255,255,0.04);
    padding: 34rpx 30rpx 6rpx;
    margin-bottom: 24rpx;
}
.func-title {
    font-size: 30rpx;
    color: #fff;
    font-weight: bold;
    display: block;
    margin-bottom: 34rpx;
    padding-left: 18rpx;
    position: relative;
}
.func-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 4rpx;
    width: 6rpx;
    height: 32rpx;
    background: linear-gradient(180deg, #f7dc8a, #c99a3a);
    border-radius: 4rpx;
}
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
.func-icon { font-size: 46rpx; margin-bottom: 12rpx; }
.func-name { font-size: 22rpx; color: #ccc; }

.store-card {
    background: #1c1c1e;
    border-radius: 20rpx;
    border: 1rpx solid rgba(255,255,255,0.04);
    padding: 30rpx;
    margin-bottom: 30rpx;
}
.store-top {
    display: flex;
    align-items: center;
    margin-bottom: 20rpx;
}
.store-logo {
    width: 64rpx;
    height: 64rpx;
    border-radius: 50%;
    margin-right: 16rpx;
    border: 2rpx solid #d4a72c;
}
.store-name { flex: 1; font-size: 28rpx; color: #fff; font-weight: bold; }
.store-contact {
    background: linear-gradient(135deg, #f7dc8a, #d4a72c);
    color: #000;
    font-size: 22rpx;
    font-weight: bold;
    padding: 12rpx 26rpx;
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

.logout-btn {
    background: #1c1c1e;
    border: 1rpx solid rgba(196,30,58,0.4);
    color: #c41e3a;
    font-size: 30rpx;
    text-align: center;
    padding: 26rpx 0;
    border-radius: 16rpx;
    margin-bottom: 20rpx;
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
