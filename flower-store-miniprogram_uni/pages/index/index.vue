<template>
    <view class="home-page">
        <!-- 自定义顶部 -->
        <view class="status-bar" :style="'height:' + statusBarHeight + 'px'"></view>
        <view class="store-selector" @tap="showStorePicker">
            <text class="store-name">{{ storeName }}</text>
            <text class="store-arrow">▶</text>
        </view>

        <scroll-view scroll-y class="home-scroll">
            <!-- 品牌区域 -->
            <view class="brand-section">
                <image class="brand-logo" src="/static/allIn.jpg" mode="aspectFit"></image>
                <text class="brand-title">梭哈酒馆</text>
                <text class="texas-bar">All In Tavern</text>
            </view>

            <!-- 会员信息卡 -->
            <view class="member-card">
                <view class="avatar-wrap">
                    <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                </view>
                <view class="member-stats">
                    <view class="stat-item" @tap="showMemberCode">
                        <text class="stat-icon">▦</text>
                        <text class="stat-label">会员码</text>
                    </view>
                    <view class="stat-item">
                        <text class="stat-value">{{ userInfo.balance || 20 }}</text>
                        <text class="stat-label">我的余额</text>
                    </view>
                    <view class="stat-item">
                        <text class="stat-value">{{ userInfo.coins || 0 }}</text>
                        <text class="stat-label">我的 All In 币</text>
                    </view>
                </view>
            </view>

            <!-- 主操作按钮 -->
            <view class="action-grid">
                <view class="action-card" @tap="goOrder">
                    <text class="action-icon">🍸</text>
                    <text class="action-title">立即点单</text>
                    <text class="action-sub">ORDER</text>
                </view>
                <view class="action-card" @tap="goCoinMall">
                    <text class="action-icon">🃏</text>
                    <text class="action-title">All In 币商城</text>
                    <text class="action-sub">POINT</text>
                </view>
            </view>

            <!-- 会员充值 -->
            <view class="recharge-card">
                <view class="recharge-bg-text">MEMBER</view>
                <view class="recharge-content">
                    <view class="recharge-info">
                        <text class="recharge-title">会员充值</text>
                        <text class="recharge-desc">享受更多专属优惠福利</text>
                    </view>
                    <view class="recharge-btn" @tap="goRecharge">立即充值</view>
                </view>
            </view>

            <!-- 服务功能 -->
            <view class="service-grid">
                <view class="service-card" @tap="goReservation">
                    <text class="service-title">房台预定</text>
                    <text class="service-sub">RESERVATION</text>
                    <text class="service-icon">🪑</text>
                </view>
                <view class="service-card" @tap="goStorage">
                    <text class="service-title">我的存酒</text>
                    <text class="service-sub">STORAGE</text>
                    <text class="service-icon">🍾</text>
                </view>
                <view class="service-card" @tap="showWifi">
                    <text class="service-title">查看WIFI</text>
                    <text class="service-sub">WIFI</text>
                    <text class="service-icon">📶</text>
                </view>
            </view>

            <!-- 位置信息 -->
            <view class="location-bar">
                <text class="diamond">◆</text>
                <text class="location-text">位置：{{ storeAddress }}</text>
                <text class="diamond">◆</text>
            </view>

            <!-- 技术支持 -->
            <view class="footer">
                <text class="footer-icon">🔥</text>
                <text class="footer-text">熠火</text>
                <text class="footer-divider">|</text>
                <text class="footer-support">熠火提供技术支持</text>
            </view>
        </scroll-view>

        <!-- 隐私弹窗 -->
        <view class="privacy-mask" v-if="showPrivacy" @tap.stop>
            <view class="privacy-modal">
                <text class="privacy-title">用户隐私保护提示</text>
                <scroll-view scroll-y class="privacy-content">
                    <text>欢迎使用梭哈酒馆小程序。我们将严格按照相关法律法规要求，采取相应安全保护措施，保护您的个人信息安全。在使用本小程序前，请您仔细阅读并充分理解《用户隐私保护指引》的全部内容。</text>
                    <text class="privacy-p">当您点击"同意"并开始使用本小程序时，即表示您已理解并同意该指引。我们将收集您的位置信息用于查找附近门店，收集您的订单信息用于完成交易服务。</text>
                </scroll-view>
                <view class="privacy-btns">
                    <view class="privacy-btn decline" @tap="declinePrivacy">拒绝</view>
                    <view class="privacy-btn agree" @tap="agreePrivacy">同意</view>
                </view>
            </view>
        </view>

        <!-- 会员码弹窗 -->
        <view class="member-mask" v-if="showMemberModal" @tap="showMemberModal = false">
            <view class="member-modal" @tap.stop>
                <text class="member-modal-title">会员码</text>
                <image class="qrcode-img" v-if="memberCodeUrl" :src="memberCodeUrl" mode="aspectFit"></image>
                <view class="qrcode-placeholder" v-else>
                    <text class="qrcode-text">请先登录</text>
                </view>
                <text class="member-modal-tip">请向店员出示此码</text>
            </view>
        </view>
    </view>
</template>

<script>
const userApi = require('../../api/user');
const configApi = require('../../api/config');
const { BASE_URL } = require('../../utils/request');
export default {
    data() {
        return {
            statusBarHeight: 20,
            userInfo: { balance: 20, coins: 0 },
            showPrivacy: false,
            showMemberModal: false,
            storeName: '梭哈酒馆 - 武昌店',
            storeAddress: '星耀·狮子座购物中心（马房山地铁站C口旁）3层3001-1',
            wifiName: 'AllInTavern',
            wifiPassword: '27272727',
            memberCodeUrl: ''
        };
    },
    onLoad() {
        const sys = uni.getSystemInfoSync();
        this.statusBarHeight = sys.statusBarHeight || 20;
        const agreed = uni.getStorageSync('privacyAgreed');
        if (!agreed) {
            this.showPrivacy = true;
        }
        this.loadUserInfo();
        this.loadConfig();
    },
    onShow() {
        this.loadUserInfo();
    },
    methods: {
        loadUserInfo() {
            const token = uni.getStorageSync('token');
            if (!token) return;
            userApi.getUserInfo().then((data) => {
                this.userInfo = {
                    ...data,
                    balance: data.balance || 20,
                    coins: data.coins || 0
                };
                if (data.id) {
                    this.memberCodeUrl = BASE_URL + '/user/membercode/' + data.id;
                }
            }).catch(() => {});
        },
        loadConfig() {
            configApi.getPublicConfig().then((cfg) => {
                if (!cfg) return;
                if (cfg.store_name) this.storeName = cfg.store_name;
                if (cfg.store_address) this.storeAddress = cfg.store_address;
                if (cfg.wifi_name) this.wifiName = cfg.wifi_name;
                if (cfg.wifi_password) this.wifiPassword = cfg.wifi_password;
            }).catch(() => {});
        },
        showStorePicker() {
            uni.showToast({ title: this.storeName, icon: 'none' });
        },
        showMemberCode() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1200);
                return;
            }
            this.showMemberModal = true;
        },
        goOrder() {
            uni.switchTab({ url: '/pages/category/category' });
        },
        goCoinMall() {
            uni.navigateTo({ url: '/pages/coin-mall/coin-mall' });
        },
        goRecharge() {
            uni.navigateTo({ url: '/pages/recharge/recharge' });
        },
        goReservation() {
            uni.showToast({ title: '房台预定功能开发中', icon: 'none' });
        },
        goStorage() {
            uni.showToast({ title: '我的存酒功能开发中', icon: 'none' });
        },
        showWifi() {
            uni.showModal({
                title: 'WIFI信息',
                content: 'WiFi名称: ' + this.wifiName + '\n密码: ' + this.wifiPassword,
                showCancel: false
            });
        },
        agreePrivacy() {
            uni.setStorageSync('privacyAgreed', true);
            this.showPrivacy = false;
        },
        declinePrivacy() {
            uni.showToast({ title: '需同意隐私政策才能使用', icon: 'none' });
        }
    }
};
</script>

<style>
.home-page {
    min-height: 100vh;
    background: #0a0a0a;
    background-image: radial-gradient(ellipse at 50% 0%, #1a1a1a 0%, #0a0a0a 70%);
}
.status-bar { width: 100%; }
.store-selector {
    display: flex;
    align-items: center;
    padding: 10rpx 30rpx 20rpx;
}
.store-name {
    color: #e8c547;
    font-size: 26rpx;
}
.store-arrow {
    color: #e8c547;
    font-size: 18rpx;
    margin-left: 8rpx;
    transform: rotate(90deg);
}
.home-scroll {
    height: calc(100vh - 120rpx);
    padding-bottom: 30rpx;
}

/* 品牌区 */
.brand-section {
    text-align: center;
    padding: 20rpx 30rpx 40rpx;
}
.brand-logo {
    width: 280rpx;
    height: 280rpx;
    border-radius: 28rpx;
    margin: 10rpx auto 20rpx;
    display: block;
}
.brand-title {
    font-size: 52rpx;
    font-weight: bold;
    color: #fff;
    letter-spacing: 4rpx;
    display: block;
}
.texas-bar {
    font-size: 30rpx;
    color: #ccc;
    font-weight: bold;
    letter-spacing: 4rpx;
    display: block;
    margin-top: 12rpx;
}

/* 会员卡 */
.member-card {
    margin: 0 30rpx 30rpx;
    background: linear-gradient(135deg, #3a3a3c 0%, #2c2c2e 100%);
    border-radius: 20rpx;
    padding: 60rpx 30rpx 30rpx;
    position: relative;
    box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.4);
}
.avatar-wrap {
    position: absolute;
    top: -50rpx;
    left: 50%;
    transform: translateX(-50%);
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    background: #555;
    border: 4rpx solid #3a3a3c;
    overflow: hidden;
}
.avatar { width: 100%; height: 100%; }
.member-stats {
    display: flex;
    justify-content: space-around;
    align-items: center;
}
.stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
}
.stat-icon { font-size: 40rpx; color: #fff; margin-bottom: 8rpx; }
.stat-value {
    font-size: 40rpx;
    font-weight: bold;
    color: #fff;
    margin-bottom: 6rpx;
}
.stat-label { font-size: 22rpx; color: #aaa; }

/* 主操作 */
.action-grid {
    display: flex;
    gap: 20rpx;
    padding: 0 30rpx;
    margin-bottom: 24rpx;
}
.action-card {
    flex: 1;
    background: #1c1c1e;
    border-radius: 20rpx;
    padding: 40rpx 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.3);
}
.action-icon { font-size: 56rpx; margin-bottom: 16rpx; }
.action-title { font-size: 30rpx; color: #fff; font-weight: bold; }
.action-sub { font-size: 20rpx; color: #666; margin-top: 6rpx; letter-spacing: 2rpx; }

/* 充值卡 */
.recharge-card {
    margin: 0 30rpx 24rpx;
    background: linear-gradient(135deg, #3a3a3c 0%, #2c2c2e 100%);
    border-radius: 20rpx;
    padding: 30rpx;
    position: relative;
    overflow: hidden;
}
.recharge-bg-text {
    position: absolute;
    right: 20rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 80rpx;
    font-weight: 900;
    color: rgba(255,255,255,0.06);
    letter-spacing: 8rpx;
}
.recharge-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    z-index: 1;
}
.recharge-title { font-size: 32rpx; color: #fff; font-weight: bold; display: block; }
.recharge-desc { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.recharge-btn {
    background: #c41e3a;
    color: #fff;
    padding: 16rpx 36rpx;
    border-radius: 40rpx;
    font-size: 26rpx;
    font-weight: bold;
    white-space: nowrap;
}

/* 服务 */
.service-grid {
    display: flex;
    gap: 16rpx;
    padding: 0 30rpx;
    margin-bottom: 24rpx;
}
.service-card {
    flex: 1;
    background: linear-gradient(135deg, #3a3a3c 0%, #2c2c2e 100%);
    border-radius: 16rpx;
    padding: 24rpx 16rpx;
    position: relative;
    min-height: 140rpx;
}
.service-title { font-size: 26rpx; color: #fff; font-weight: bold; display: block; }
.service-sub { font-size: 18rpx; color: #666; margin-top: 4rpx; display: block; letter-spacing: 1rpx; }
.service-icon {
    position: absolute;
    right: 16rpx;
    bottom: 16rpx;
    font-size: 36rpx;
    opacity: 0.7;
}

/* 位置 */
.location-bar {
    margin: 0 30rpx 30rpx;
    background: #1c1c1e;
    border-radius: 12rpx;
    padding: 24rpx 20rpx;
    display: flex;
    align-items: center;
    gap: 12rpx;
}
.diamond { color: #c41e3a; font-size: 20rpx; flex-shrink: 0; }
.location-text { font-size: 22rpx; color: #ccc; line-height: 1.5; flex: 1; }

/* 页脚 */
.footer {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20rpx 0 40rpx;
    gap: 8rpx;
}
.footer-icon { font-size: 24rpx; }
.footer-text { font-size: 24rpx; color: #666; }
.footer-divider { color: #444; font-size: 24rpx; }
.footer-support { font-size: 22rpx; color: #555; }

/* 隐私弹窗 */
.privacy-mask {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.7);
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60rpx;
}
.privacy-modal {
    background: #fff;
    border-radius: 24rpx;
    padding: 40rpx;
    width: 100%;
    max-height: 70vh;
}
.privacy-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    display: block;
    margin-bottom: 24rpx;
}
.privacy-content {
    max-height: 400rpx;
    margin-bottom: 30rpx;
}
.privacy-content text {
    font-size: 26rpx;
    color: #666;
    line-height: 1.6;
    display: block;
}
.privacy-p { margin-top: 16rpx; }
.privacy-btns {
    display: flex;
    gap: 20rpx;
}
.privacy-btn {
    flex: 1;
    height: 80rpx;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
}
.privacy-btn.decline { background: #f0f0f0; color: #666; }
.privacy-btn.agree { background: #000; color: #fff; }

/* 会员码 */
.member-mask {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0,0,0,0.7);
    z-index: 9998;
    display: flex;
    align-items: center;
    justify-content: center;
}
.member-modal {
    background: #fff;
    border-radius: 24rpx;
    padding: 50rpx;
    width: 500rpx;
    text-align: center;
}
.member-modal-title { font-size: 32rpx; font-weight: bold; color: #333; display: block; margin-bottom: 30rpx; }
.qrcode-placeholder {
    width: 300rpx;
    height: 300rpx;
    background: #f5f5f5;
    margin: 0 auto 20rpx;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
}
.qrcode-text { font-size: 32rpx; color: #999; letter-spacing: 4rpx; }
.qrcode-img { width: 360rpx; height: 360rpx; margin: 0 auto 20rpx; display: block; }
.member-modal-tip { font-size: 24rpx; color: #999; }
</style>
