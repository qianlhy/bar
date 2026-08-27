<template>
    <view class="user-page">
        <!-- 用户信息 -->
        <view class="user-header">
            <image class="user-avatar" :src="userInfo.avatar || '/static/allIn.jpg'" mode="aspectFill"></image>
            <view class="user-text" v-if="isLogin">
                <text class="user-greet">{{ userInfo.nickname || '欢迎加入' }}</text>
                <text class="user-sub">注册后解锁更多会员特权</text>
            </view>
            <view class="user-text" v-else>
                <text class="user-greet">欢迎加入</text>
                <text class="user-sub">注册后解锁更多会员特权</text>
            </view>
            <view class="register-btn g-tap" @tap="goToLogin" v-if="!isLogin">注册会员</view>
            <view class="register-btn g-tap" @tap="goSettings" v-else>个人中心</view>
        </view>

        <!-- 资产统计 -->
        <view class="asset-card">
            <view class="asset-item g-tap" @tap="goRecharge">
                <text class="asset-label">All In 币</text>
                <text class="asset-value g-num">{{ userInfo.coins || 0 }}</text>
            </view>
            <view class="asset-divider"></view>
            <view class="asset-item">
                <text class="asset-label">积分</text>
                <text class="asset-value g-num">{{ userInfo.points || 0 }}</text>
            </view>
        </view>

        <!-- 常用功能 -->
        <view class="func-card">
            <text class="func-title">常用功能</text>
            <view class="func-grid">
                <view class="func-item g-tap" @tap="goSettings">
                    <view class="g-icon">我</view>
                    <text class="func-name">个人中心</text>
                </view>
                <view class="func-item g-tap" @tap="goOrders">
                    <view class="g-icon">单</view>
                    <text class="func-name">订单中心</text>
                </view>
                <view class="func-item g-tap" @tap="goRecharge">
                    <view class="g-icon">充</view>
                    <text class="func-name">会员充值</text>
                </view>
                <view class="func-item g-tap" @tap="goRank">
                    <view class="g-icon">榜</view>
                    <text class="func-name">大师分</text>
                </view>
            </view>
        </view>

        <!-- 门店信息 -->
        <view class="store-card">
            <view class="store-top">
                <image class="store-logo" src="/static/allIn.jpg" mode="aspectFit"></image>
                <text class="store-name">梭哈酒馆</text>
            </view>
            <view class="store-msg">
                <text>HI~ 欢迎光临本店</text>
                <text>如需帮助，请到店咨询工作人员。</text>
            </view>
        </view>

        <!-- 退出登录 -->
        <view class="logout-btn g-tap" v-if="isLogin" @tap="logout">退出登录</view>

        <!-- 技术支持 -->
        <view class="footer">
            <text class="footer-text">熠火提供技术支持</text>
        </view>
    </view>
</template>

<script>
const userApi = require('../../api/user');
export default {
    data() {
        return {
            userInfo: { balance: 0, coins: 0, couponCount: 0, points: 0 },
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
                this.userInfo = { balance: 0, coins: 0, couponCount: 0, points: 0 };
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
        goOrders() {
            if (!this.isLogin) return this.goToLogin();
            uni.navigateTo({ url: '/pages/order/list' });
        },
        goRecharge() {
            if (!this.isLogin) return this.goToLogin();
            uni.navigateTo({ url: '/pages/recharge/recharge' });
        },
        goRank() {
            uni.switchTab({ url: '/pages/rank/rank' });
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
                    this.userInfo = { balance: 0, coins: 0, couponCount: 0, points: 0 };
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
    background: var(--bg-page);
    padding: 30rpx 24rpx calc(40rpx + env(safe-area-inset-bottom));
    box-sizing: border-box;
}
.user-header {
    display: flex;
    align-items: center;
    padding: 12rpx 4rpx 36rpx;
}
.user-avatar {
    width: 108rpx;
    height: 108rpx;
    border-radius: 50%;
    background: var(--bg-sunken);
    flex-shrink: 0;
    border: 3rpx solid var(--gold);
    box-shadow: 0 0 22rpx rgba(232, 197, 71, 0.28);
}
.user-text { flex: 1; min-width: 0; margin-left: 22rpx; }
.user-greet { font-size: 34rpx; color: var(--text-primary); font-weight: 700; display: block; }
.user-sub { font-size: 22rpx; color: var(--text-muted); margin-top: 8rpx; display: block; }
.register-btn {
    background: var(--gold-gradient);
    color: #171717;
    font-size: 24rpx;
    font-weight: 700;
    padding: 14rpx 30rpx;
    border-radius: 30rpx;
    white-space: nowrap;
    box-shadow: 0 6rpx 16rpx rgba(232, 197, 71, 0.26);
}

.asset-card {
    background: var(--bg-card-gradient);
    border-radius: 22rpx;
    border: 1rpx solid var(--border-gold);
    display: flex;
    padding: 34rpx 0;
    margin-bottom: 24rpx;
    box-shadow: 0 12rpx 32rpx rgba(0, 0, 0, 0.28);
}
.asset-item { flex: 1; text-align: center; }
.asset-label { font-size: 22rpx; color: var(--text-muted); display: block; margin-bottom: 12rpx; }
.asset-value { font-size: 40rpx; color: var(--gold); font-weight: 700; display: block; }
.asset-divider { width: 1rpx; background: var(--border-subtle); align-self: stretch; }

.func-card {
    background: var(--bg-card-gradient);
    border-radius: 22rpx;
    border: 1rpx solid var(--border-subtle);
    padding: 32rpx 20rpx 10rpx;
    margin-bottom: 24rpx;
}
.func-title {
    font-size: 30rpx;
    color: var(--text-primary);
    font-weight: 600;
    display: block;
    margin-bottom: 28rpx;
    padding-left: 18rpx;
    position: relative;
}
.func-title::before {
    content: '';
    position: absolute;
    left: 0;
    top: 6rpx;
    width: 6rpx;
    height: 28rpx;
    background: linear-gradient(180deg, var(--gold-light), var(--gold-dark));
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
    margin-bottom: 28rpx;
}
.func-name { font-size: 22rpx; color: var(--text-muted); margin-top: 12rpx; }

.store-card {
    background: var(--bg-card-gradient);
    border-radius: 22rpx;
    border: 1rpx solid var(--border-subtle);
    padding: 28rpx;
    margin-bottom: 24rpx;
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
    border: 2rpx solid var(--gold);
}
.store-name { flex: 1; font-size: 28rpx; color: var(--text-primary); font-weight: 600; }
.store-contact {
    background: var(--gold-gradient);
    color: #171717;
    font-size: 22rpx;
    font-weight: 700;
    padding: 12rpx 26rpx;
    border-radius: 30rpx;
}
.store-msg {
    background: var(--bg-sunken);
    border-radius: 14rpx;
    padding: 22rpx;
}
.store-msg text {
    font-size: 24rpx;
    color: var(--text-muted);
    line-height: 1.7;
    display: block;
}

.logout-btn {
    background: var(--bg-card);
    border: 1rpx solid rgba(196, 30, 58, 0.35);
    color: #f0616f;
    font-size: 30rpx;
    font-weight: 600;
    text-align: center;
    padding: 26rpx 0;
    border-radius: 20rpx;
    margin-bottom: 20rpx;
}

.footer {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12rpx 0 8rpx;
}
.footer-text { font-size: 22rpx; color: var(--text-faint); }
</style>
