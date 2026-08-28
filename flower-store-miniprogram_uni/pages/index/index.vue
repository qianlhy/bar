<template>
    <view class="home-page poker-bg">
        <view class="status-bar" :style="'height:' + statusBarHeight + 'px'"></view>

        <!-- 扑克装饰 -->
        <text class="poker-float spade">♠</text>
        <text class="poker-float heart">♥</text>
        <text class="poker-float club">♣</text>
        <text class="poker-float diamond">♦</text>

        <scroll-view scroll-y class="home-scroll">
            <view class="top-bar">
                <view class="top-greet g-tap font-art" @tap="showStorePicker">
                    <text class="top-greet-text">{{ storeName }}</text>
                </view>
                <view class="top-icons">
                    <view class="top-icon g-tap" @tap="showMemberCode">码</view>
                    <view class="top-icon g-tap" @tap="goSettings">设</view>
                </view>
            </view>

            <!-- 品牌英雄区 -->
            <view class="hero-brand">
                <view class="deco-wine hero-wine"></view>
                <view class="deco-cards hero-cards"></view>
                <view class="suoha-logo-stack">
                    <text class="art-suoha font-art">梭哈</text>
                    <view class="logo-stage">
                        <view class="logo-ring">
                            <image class="brand-logo" src="/static/allIn.jpg" mode="aspectFill"></image>
                        </view>
                        <text class="card-deco left">♠A</text>
                        <text class="card-deco right">♥K</text>
                    </view>
                </view>
                <text class="brand-en font-art">ALL IN TAVERN</text>
                <view class="hours-pill">
                    <text class="hours-label">营业时间</text>
                    <text class="hours-value font-art">{{ businessHours }}</text>
                </view>
            </view>

            <!-- 会员数据条 -->
            <view class="member-strip mesh-card">
                <view class="strip-item g-tap" @tap="showMemberCode">
                    <view class="icon-box strip-icon-box"><view class="icon-qrcode"></view></view>
                    <text class="strip-label font-art">会员码</text>
                </view>
                <view class="strip-divider"></view>
                <view class="strip-item">
                    <text class="strip-num font-art neon-gold">{{ userInfo.points || 0 }}</text>
                    <text class="strip-label">我的积分</text>
                </view>
                <view class="strip-divider"></view>
                <view class="strip-item">
                    <text class="strip-num font-art neon-cyan">{{ userInfo.coins || 0 }}</text>
                    <text class="strip-label">All In 币</text>
                </view>
            </view>

            <!-- 双主入口 -->
            <view class="dual-hero">
                <view class="hero-btn order-btn g-tap mesh-card" @tap="goOrder">
                    <text class="hero-btn-cn font-art">立即点单</text>
                    <text class="hero-btn-en">ORDER</text>
                    <view class="hero-deco"><view class="line-icon icon-cocktail"></view></view>
                </view>
                <view class="hero-btn mall-btn g-tap mesh-card" @tap="goCoinMall">
                    <text class="hero-btn-cn font-art">币商城</text>
                    <text class="hero-btn-en">POINT</text>
                    <view class="hero-deco"><view class="line-icon icon-coin"></view></view>
                </view>
            </view>

            <!-- 充值横幅 -->
            <view class="recharge-banner g-tap" @tap="goRecharge">
                <text class="banner-watermark">MEMBER</text>
                <view class="banner-body">
                    <view class="banner-copy">
                        <text class="banner-title font-art">会员充值</text>
                        <text class="banner-sub">享受更多专属优惠福利</text>
                    </view>
                    <view class="banner-btn btn-neon font-art">立即充值</view>
                </view>
            </view>

            <!-- 签到 / WiFi -->
            <view class="util-row">
                <view :class="'util-card g-tap mesh-card ' + (checkedIn ? 'done' : '')" @tap="doCheckin">
                    <view class="util-icon-wrap"><text class="util-char">签</text></view>
                    <text class="util-title font-art">{{ checkedIn ? '今日已签' : '每日签到' }}</text>
                    <text class="util-sub">{{ checkedIn ? ('积分 ' + (userInfo.points || 0)) : ('+' + checkinReward + ' 积分') }}</text>
                </view>
                <view class="util-card g-tap mesh-card" @tap="showWifi">
                    <view class="util-icon-wrap"><view class="line-icon icon-wifi"></view></view>
                    <text class="util-title font-art">查看 WiFi</text>
                    <text class="util-sub">店内专属网络</text>
                </view>
                <view class="util-card g-tap mesh-card" @tap="showMemberCode">
                    <view class="util-icon-wrap"><view class="icon-qrcode"></view></view>
                    <text class="util-title font-art">会员特权</text>
                    <text class="util-sub">出示会员码</text>
                </view>
            </view>

            <!-- 门店地址 -->
            <view class="store-card mesh-card">
                <view class="store-card-head">
                    <text class="store-card-title font-art">门店地址</text>
                    <view class="store-nav-link g-tap" @tap="openNavigation">
                        <text class="store-nav-text neon-cyan">导航</text>
                        <text class="store-nav-arrow">›</text>
                    </view>
                </view>
                <text class="store-card-address">{{ storeAddress }}</text>
                <text class="store-card-phone">预定电话 {{ storePhone }}</text>
                <view class="store-card-actions">
                    <view class="store-primary-btn btn-gold g-tap" @tap="callStore">
                        <text class="store-primary-text font-art">立即预定</text>
                    </view>
                    <view class="store-secondary-row">
                        <view class="store-secondary-item g-tap" @tap="goStoreGallery">
                            <text class="sec-icon">◫</text>
                            <text class="sec-text font-art">店内环境</text>
                        </view>
                        <view class="store-secondary-divider"></view>
                        <view class="store-secondary-item g-tap" @tap="callStore">
                            <text class="sec-icon">☎</text>
                            <text class="sec-text font-art">拨打电话</text>
                        </view>
                    </view>
                </view>
            </view>

            <view class="footer">
                <text class="disclaimer-text">绿色竞技，禁止赌博！筹码仅做计分牌使用，无任何现金价值！</text>
                <text class="footer-text">熠火提供技术支持</text>
            </view>
        </scroll-view>

        <!-- 隐私弹窗 -->
        <view class="privacy-mask" v-if="showPrivacy" @tap.stop>
            <view class="privacy-modal">
                <text class="privacy-title">用户隐私保护提示</text>
                <scroll-view scroll-y class="privacy-content">
                    <text>欢迎使用梭哈酒馆小程序。请在授权前阅读微信官方{{ privacyContractName }}，了解头像、昵称以及订单和支付必要信息的处理方式。</text>
                    <text class="privacy-p">您可以拒绝非必要授权并继续浏览商品；登录、下单或资料设置等功能只会在实际需要时申请对应信息。</text>
                    <text class="privacy-link" @tap="openPrivacyContract">查看微信官方{{ privacyContractName }}</text>
                </scroll-view>
                <view class="privacy-btns">
                    <view class="privacy-btn decline" @tap="declinePrivacy">暂不授权</view>
                    <button
                        class="privacy-btn agree"
                        open-type="agreePrivacyAuthorization"
                        @agreeprivacyauthorization="onAgreePrivacyAuthorization"
                    >同意并继续</button>
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
const pointsApi = require('../../api/points');
const { BASE_URL } = require('../../utils/request');
export default {
    data() {
        return {
            statusBarHeight: 20,
            userInfo: { balance: 20, coins: 0, points: 0 },
            showPrivacy: false,
            privacyContractName: '《用户隐私保护指引》',
            showMemberModal: false,
            storeName: '梭哈酒馆 - 南京店',
            storeAddress: '江苏省南京市浦口区江浦街道明发新城中心2栋4单元1007',
            storePhone: '15896269275',
            businessHours: '19:30-1:30',
            storeLatitude: 32.0618,
            storeLongitude: 118.6286,
            wifiName: 'AllInTavern',
            wifiPassword: '66668888',
            memberCodeUrl: '',
            checkedIn: false,
            checkinReward: 500,
            checkinLoading: false,
            storeGalleryCount: 0
        };
    },
    computed: {
        memberIdText() {
            const id = this.userInfo && this.userInfo.id;
            if (!id) return '————';
            const s = String(id).padStart(8, '0');
            return s.slice(0, 4) + ' ' + s.slice(4);
        }
    },
    onLoad() {
        const sys = uni.getSystemInfoSync();
        this.statusBarHeight = sys.statusBarHeight || 20;
        this.checkPrivacyAuthorization();
        this.loadUserInfo();
        this.loadConfig();
    },
    onShow() {
        this.loadUserInfo();
        this.loadCheckinStatus();
    },
    methods: {
        loadUserInfo() {
            const token = uni.getStorageSync('token');
            if (!token) return;
            userApi.getUserInfo().then((data) => {
                this.userInfo = {
                    ...data,
                    balance: data.balance || 20,
                    coins: data.coins || 0,
                    points: data.points || 0
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
                if (cfg.store_phone) this.storePhone = cfg.store_phone;
                if (cfg.business_hours) this.businessHours = cfg.business_hours;
                if (cfg.wifi_name) this.wifiName = cfg.wifi_name;
                if (cfg.wifi_password) this.wifiPassword = cfg.wifi_password;
                if (cfg.store_latitude) this.storeLatitude = parseFloat(cfg.store_latitude);
                if (cfg.store_longitude) this.storeLongitude = parseFloat(cfg.store_longitude);
                if (cfg.checkin_points) this.checkinReward = parseInt(cfg.checkin_points, 10) || 500;
                if (cfg.store_gallery) {
                    const raw = cfg.store_gallery.trim();
                    if (raw.startsWith('[')) {
                        try { this.storeGalleryCount = JSON.parse(raw).filter(Boolean).length; } catch (e) { this.storeGalleryCount = 0; }
                    } else {
                        this.storeGalleryCount = raw.split(',').filter((s) => s.trim()).length;
                    }
                }
            }).catch(() => {});
        },
        goStoreGallery() {
            uni.navigateTo({ url: '/pages/store-gallery/store-gallery' });
        },
        loadCheckinStatus() {
            const token = uni.getStorageSync('token');
            if (!token) {
                this.checkedIn = false;
                return;
            }
            pointsApi.getCheckinStatus().then((data) => {
                this.checkedIn = !!(data && data.checkedIn);
                if (data && data.rewardPoints) this.checkinReward = data.rewardPoints;
                if (data && data.points != null) {
                    this.userInfo = { ...this.userInfo, points: data.points };
                }
            }).catch(() => {});
        },
        doCheckin() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1200);
                return;
            }
            if (this.checkedIn) {
                uni.showToast({ title: '今日已签到', icon: 'none' });
                return;
            }
            if (this.checkinLoading) return;
            this.checkinLoading = true;
            pointsApi.checkin().then((data) => {
                this.checkinLoading = false;
                this.checkedIn = true;
                const reward = (data && data.rewardPoints) || this.checkinReward;
                if (data && data.points != null) {
                    this.userInfo = { ...this.userInfo, points: data.points };
                }
                uni.showToast({ title: '签到成功 +' + reward, icon: 'success' });
            }).catch((err) => {
                this.checkinLoading = false;
                const msg = (err && err.message) || '签到失败';
                uni.showToast({ title: msg, icon: 'none' });
                this.loadCheckinStatus();
            });
        },
        showStorePicker() {
            uni.showToast({ title: this.storeName, icon: 'none' });
        },
        goSettings() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1200);
                return;
            }
            uni.navigateTo({ url: '/pages/settings/settings' });
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
        goRecharge() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1200);
                return;
            }
            uni.navigateTo({ url: '/pages/recharge/recharge' });
        },
        goCoinMall() {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1200);
                return;
            }
            uni.navigateTo({ url: '/pages/coin-mall/coin-mall' });
        },
        showWifi() {
            uni.showModal({
                title: 'WIFI信息',
                content: 'WiFi名称: ' + this.wifiName + '\n密码: ' + this.wifiPassword,
                showCancel: false
            });
        },
        openNavigation() {
            const lat = Number(this.storeLatitude);
            const lng = Number(this.storeLongitude);
            const name = this.storeName || '梭哈酒馆';
            const address = this.storeAddress || '';
            uni.showActionSheet({
                itemList: ['微信地图导航（可选高德/百度）', '复制地址去高德搜索'],
                success: (res) => {
                    if (res.tapIndex === 0) {
                        uni.openLocation({
                            latitude: lat,
                            longitude: lng,
                            name,
                            address,
                            scale: 16,
                            fail: () => {
                                uni.showToast({ title: '打开地图失败', icon: 'none' });
                            }
                        });
                    } else if (res.tapIndex === 1) {
                        uni.setClipboardData({
                            data: address,
                            success: () => {
                                uni.showToast({ title: '地址已复制，请打开高德地图', icon: 'none' });
                            }
                        });
                    }
                }
            });
        },
        callStore() {
            const phone = (this.storePhone || '').replace(/\s/g, '');
            if (!phone) {
                uni.showToast({ title: '暂无门店电话', icon: 'none' });
                return;
            }
            uni.makePhoneCall({
                phoneNumber: phone,
                fail: () => {
                    uni.showToast({ title: '拨号失败', icon: 'none' });
                }
            });
        },
        checkPrivacyAuthorization() {
            // #ifdef MP-WEIXIN
            if (typeof wx === 'undefined' || !wx.getPrivacySetting) {
                return;
            }
            wx.getPrivacySetting({
                success: (res) => {
                    this.privacyContractName = res.privacyContractName || '《用户隐私保护指引》';
                    this.showPrivacy = !!res.needAuthorization;
                },
                fail: () => {
                    this.showPrivacy = false;
                }
            });
            // #endif
        },
        openPrivacyContract() {
            // #ifdef MP-WEIXIN
            if (typeof wx === 'undefined' || !wx.openPrivacyContract) {
                uni.showToast({ title: '当前微信版本暂不支持', icon: 'none' });
                return;
            }
            wx.openPrivacyContract({
                fail: () => {
                    uni.showToast({ title: '隐私指引暂未配置完成', icon: 'none' });
                }
            });
            // #endif
        },
        onAgreePrivacyAuthorization() {
            this.showPrivacy = false;
        },
        declinePrivacy() {
            this.showPrivacy = false;
            uni.showToast({ title: '您仍可浏览商品', icon: 'none' });
        }
    }
};
</script>

<style>
.home-page {
    min-height: 100vh;
    position: relative;
    overflow: hidden;
}
.status-bar { width: 100%; }
.home-scroll {
    height: 100vh;
    box-sizing: border-box;
    padding-bottom: calc(40rpx + env(safe-area-inset-bottom));
    position: relative;
    z-index: 1;
}

/* 扑克漂浮装饰 */
.poker-float {
    position: absolute;
    z-index: 0;
    font-size: 80rpx;
    opacity: 0.08;
    pointer-events: none;
}
.poker-float.spade { top: 120rpx; left: 20rpx; color: #fff; transform: rotate(-15deg); }
.poker-float.heart { top: 200rpx; right: 30rpx; color: #a05060; transform: rotate(12deg); }
.poker-float.club { bottom: 400rpx; left: 40rpx; color: #708088; transform: rotate(8deg); }
.poker-float.diamond { bottom: 280rpx; right: 50rpx; color: #a05060; transform: rotate(-10deg); }

.top-bar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8rpx 28rpx 4rpx;
}
.top-greet-text {
    color: #f5f5f5;
    font-size: 26rpx;
}
.top-icons { display: flex; gap: 14rpx; }
.top-icon {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    border: 1rpx solid rgba(255, 255, 255, 0.12);
    background: rgba(255, 255, 255, 0.05);
    color: #bbb;
    font-size: 22rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* 品牌英雄区 */
.hero-brand {
    text-align: center;
    padding: 16rpx 30rpx 20rpx;
    position: relative;
    min-height: 480rpx;
}
.hero-wine { top: 40rpx; right: 40rpx; transform: rotate(12deg); }
.hero-cards { top: 60rpx; left: 36rpx; }
.suoha-logo-stack {
    position: relative;
    margin: 0 auto;
    width: 100%;
    padding-top: 20rpx;
}
.art-suoha {
    display: block;
    font-size: 148rpx;
    font-weight: 900;
    font-style: italic;
    letter-spacing: 32rpx;
    padding-left: 32rpx;
    line-height: 1;
    background: linear-gradient(180deg, rgba(240, 240, 240, 0.95) 0%, rgba(196, 90, 106, 0.85) 55%, rgba(160, 28, 45, 0.7) 100%);
    -webkit-background-clip: text;
    color: transparent;
    position: relative;
    z-index: 1;
    transform: skewX(-3deg);
    margin-bottom: -60rpx;
    opacity: 0.92;
}
.logo-stage {
    position: relative;
    margin: 0 auto;
    width: 240rpx;
    height: 220rpx;
    z-index: 2;
}
.logo-ring {
    width: 190rpx;
    height: 190rpx;
    margin: 0 auto;
    border-radius: 50%;
    padding: 4rpx;
    background: linear-gradient(135deg, #a01c30, #333, #a01c30);
    box-shadow: 0 0 40rpx rgba(160, 28, 45, 0.25), 0 16rpx 40rpx rgba(0, 0, 0, 0.5);
    position: relative;
}
.brand-logo {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    border: 4rpx solid #0a0a0a;
    display: block;
}
.card-deco {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    font-size: 40rpx;
    font-weight: 900;
    opacity: 0.3;
    z-index: 0;
    font-family: serif;
}
.card-deco.left { left: -16rpx; color: #ddd; }
.card-deco.right { right: -16rpx; color: #a01c30; }
.brand-en {
    display: block;
    font-size: 24rpx;
    color: #999;
    letter-spacing: 10rpx;
    margin-top: 16rpx;
    position: relative;
    z-index: 2;
}
.hours-pill {
    margin-top: 20rpx;
    display: inline-flex;
    align-items: center;
    padding: 10rpx 28rpx;
    border-radius: 40rpx;
    border: 1rpx solid rgba(160, 28, 45, 0.25);
    background: rgba(160, 28, 45, 0.06);
    position: relative;
    z-index: 2;
}
.hours-label { font-size: 22rpx; color: #888; margin-right: 12rpx; }
.hours-value { font-size: 26rpx; color: #ccc; }

/* 会员数据条 */
.member-strip {
    margin: 20rpx 28rpx;
    display: flex;
    align-items: center;
    padding: 28rpx 16rpx;
}
.strip-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
}
.strip-icon-box { width: 64rpx; height: 64rpx; margin-bottom: 8rpx; }
.strip-icon-box .icon-qrcode { width: 28rpx; height: 28rpx; }
.strip-num { font-size: 40rpx; font-weight: 800; line-height: 1.2; }
.strip-label { font-size: 22rpx; color: #999; margin-top: 6rpx; }
.strip-divider { width: 1rpx; height: 60rpx; background: rgba(255, 255, 255, 0.1); }

/* 双主入口 */
.dual-hero {
    margin: 0 28rpx 20rpx;
    display: flex;
    gap: 16rpx;
}
.hero-btn {
    flex: 1;
    min-height: 200rpx;
    padding: 28rpx 20rpx;
    display: flex;
    flex-direction: column;
    justify-content: center;
    position: relative;
    overflow: hidden;
}
.hero-btn.order-btn,
.hero-btn.mall-btn {
    border-color: rgba(255, 255, 255, 0.08);
    background: linear-gradient(160deg, #222224, #18181a);
}
.mall-btn .hero-btn-cn { color: #e8e8e8; }
.hero-btn-cn { font-size: 34rpx; color: #eee; display: block; }
.hero-btn-en {
    font-size: 22rpx;
    color: rgba(255, 255, 255, 0.4);
    letter-spacing: 4rpx;
    margin-top: 8rpx;
    display: block;
}
.hero-deco {
    position: absolute;
    right: 20rpx;
    bottom: 20rpx;
    opacity: 0.7;
}
.hero-deco .line-icon { width: 52rpx; height: 52rpx; }

.recharge-banner {
    margin: 0 28rpx 20rpx;
    border-radius: 20rpx;
    overflow: hidden;
    position: relative;
    border: 1rpx solid rgba(255, 255, 255, 0.08);
    background: linear-gradient(160deg, #242426, #18181a 70%);
}
.banner-watermark {
    position: absolute;
    right: -20rpx;
    top: 50%;
    transform: translateY(-50%);
    font-size: 100rpx;
    font-weight: 900;
    color: rgba(255, 255, 255, 0.04);
    letter-spacing: 8rpx;
    pointer-events: none;
}
.banner-body {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 32rpx 28rpx;
    position: relative;
    z-index: 1;
}
.banner-title { font-size: 32rpx; color: #fff; display: block; }
.banner-sub { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.banner-btn {
    padding: 16rpx 32rpx;
    border-radius: 40rpx;
    font-size: 26rpx;
    flex-shrink: 0;
}

/* 工具行 */
.util-row {
    margin: 0 28rpx 20rpx;
    display: flex;
    gap: 14rpx;
}
.util-card {
    flex: 1;
    padding: 24rpx 12rpx;
    text-align: center;
    min-width: 0;
}
.util-card.done { opacity: 0.65; }
.util-icon-wrap {
    width: 56rpx;
    height: 56rpx;
    margin: 0 auto 10rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
.util-char { font-size: 28rpx; color: #ccc; font-weight: 600; }
.util-icon-wrap .line-icon { width: 36rpx; height: 36rpx; }
.util-icon-wrap .icon-qrcode { width: 30rpx; height: 30rpx; }
.util-title { font-size: 24rpx; color: #ddd; display: block; }
.util-sub { font-size: 20rpx; color: #888; margin-top: 6rpx; display: block; }

/* 门店 */
.store-card {
    margin: 0 28rpx 24rpx;
    padding: 28rpx 24rpx;
}
.store-card-head {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 16rpx;
}
.store-card-title { font-size: 28rpx; color: #fff; }
.store-nav-text { font-size: 24rpx; }
.store-nav-arrow { color: #aaa; font-size: 28rpx; }
.store-card-address { display: block; font-size: 24rpx; color: #b0b0b4; line-height: 1.55; }
.store-card-phone { display: block; margin-top: 12rpx; font-size: 24rpx; color: #888; }
.store-card-actions {
    margin-top: 28rpx;
    display: flex;
    flex-direction: column;
    gap: 16rpx;
}
.store-primary-btn {
    width: 100%;
    height: 80rpx;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
.store-primary-text {
    font-size: 30rpx;
    color: #1a1a1a;
    letter-spacing: 2rpx;
}
.store-secondary-row {
    display: flex;
    align-items: stretch;
    border-radius: 18rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.1);
    background: rgba(0, 0, 0, 0.28);
    overflow: hidden;
}
.store-secondary-item {
    flex: 1;
    height: 76rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10rpx;
}
.store-secondary-item:active {
    background: rgba(160, 28, 45, 0.12);
}
.store-secondary-divider {
    width: 1rpx;
    align-self: stretch;
    margin: 16rpx 0;
    background: rgba(255, 255, 255, 0.1);
}
.sec-icon {
    font-size: 28rpx;
    color: #a01c30;
    line-height: 1;
}
.store-secondary-item:last-child .sec-icon {
    font-size: 26rpx;
    color: #bbb;
}
.sec-text {
    font-size: 26rpx;
    color: #ccc;
    letter-spacing: 1rpx;
}

.footer {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8rpx 0 48rpx;
}
.footer-text { font-size: 22rpx; color: #444; margin-top: 8rpx; }

/* 弹窗 */
.privacy-mask,
.member-mask {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0, 0, 0, 0.75);
    z-index: 9999;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 60rpx;
}
.member-mask { z-index: 9998; padding: 0; }
.privacy-modal,
.member-modal {
    background: linear-gradient(160deg, #1f1a22, #121014);
    border: 1rpx solid rgba(255, 45, 106, 0.35);
    border-radius: 24rpx;
    padding: 40rpx;
    width: 100%;
}
.member-modal { width: 500rpx; text-align: center; padding: 50rpx; }
.privacy-title,
.member-modal-title {
    font-size: 32rpx;
    font-weight: bold;
    color: #ff6b9d;
    text-align: center;
    display: block;
    margin-bottom: 24rpx;
}
.privacy-content { max-height: 400rpx; margin-bottom: 30rpx; }
.privacy-content text { font-size: 26rpx; color: #999; line-height: 1.6; display: block; }
.privacy-p { margin-top: 16rpx; }
.privacy-link { margin-top: 20rpx; color: #c9788f !important; text-align: center; text-decoration: underline; }
.privacy-btns { display: flex; gap: 20rpx; }
.privacy-btn {
    flex: 1;
    height: 80rpx;
    border-radius: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 28rpx;
    font-weight: 600;
    margin: 0;
    padding: 0;
    line-height: 80rpx;
    border: none;
}
.privacy-btn::after { border: none; }
.privacy-btn.decline { background: #2a2a2c; color: #999; }
.privacy-btn.agree {
    background: linear-gradient(135deg, #b85c7a, #8f3d52);
    color: #f5f0f2;
}
.qrcode-placeholder {
    width: 300rpx;
    height: 300rpx;
    background: #fff;
    border-radius: 16rpx;
    margin: 0 auto 20rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
.qrcode-text { font-size: 32rpx; color: #8a8a8e; }
.qrcode-img {
    width: 360rpx;
    height: 360rpx;
    margin: 0 auto 20rpx;
    display: block;
    border-radius: 16rpx;
    background: #fff;
}
.member-modal-tip { font-size: 24rpx; color: #999; }
</style>
