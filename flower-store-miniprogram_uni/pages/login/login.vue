<template>
    <!-- pages/login/login.wxml -->
    <view class="login-container">
        <view class="login-header">
            <view class="logo-area">
                <image class="logo-img" src="/static/allIn.jpg" mode="aspectFit"></image>
            </view>
            <text class="title">欢迎来到 梭哈酒馆</text>
        </view>

        <!-- 完善头像昵称 -->
        <view class="profile-setup" v-if="showProfile">
            <text class="profile-title">完善个人资料</text>
            <text class="profile-tip">使用微信头像昵称，快速完成注册</text>

            <button class="avatar-picker" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
                <image class="avatar-preview" :src="profileAvatar || '/static/images/icons/default-avatar.png'" mode="aspectFill"></image>
                <text class="avatar-hint">{{ avatarUploading ? '上传中...' : '点击选择头像' }}</text>
            </button>

            <view class="profile-item">
                <text class="profile-label">昵称</text>
                <input class="profile-input" type="nickname" placeholder="点击获取微信昵称" :value="profileNickname" @input="onNicknameInput" @change="onNicknameInput" />
            </view>

            <button class="login-btn" @tap="saveProfile" :disabled="avatarUploading">完成</button>
            <view class="profile-skip" @tap="skipProfile">跳过，稍后再说</view>
        </view>

        <view class="login-form" v-if="!showProfile">
            <!-- 微信登录（主入口） -->
            <button :class="'wx-login-btn primary ' + (isLoading ? 'loading' : '')" @tap="wxLogin" :disabled="isLoading">
                <image class="wx-icon" src="/static/images/icons/wechat.png" mode="aspectFit"></image>
                {{ isLoading ? '登录中...' : '微信一键登录' }}
            </button>

            <!-- 用户协议 -->
            <view class="agreement">
                <view :class="'checkbox ' + (isAgree ? 'checked' : '')" @tap="toggleAgree"></view>
                <text class="agreement-text">我已阅读并同意</text>
                <text class="agreement-link" @tap="goToUserAgreement">《用户协议》</text>
                <text class="agreement-text">和</text>
                <text class="agreement-link" @tap="goToPrivacyPolicy">《隐私政策》</text>
            </view>

            <view class="phone-login-entry" @tap="togglePhoneLogin">
                {{ usePhoneLogin ? '返回微信登录' : '使用手机号登录' }}
            </view>
        </view>

        <view class="login-form phone-form" v-if="!showProfile && usePhoneLogin">
            <!-- 手机号输入 -->
            <view class="form-item">
                <view class="input-label">手机号</view>
                <input class="input" type="number" placeholder="请输入手机号" maxlength="11" :value="phone" @input="inputPhone" />
            </view>

            <!-- 密码/验证码输入 -->
            <view class="form-item">
                <view class="input-label">{{ isPasswordLogin ? '密码' : '验证码' }}</view>
                <view class="input-wrapper">
                    <input
                        :class="'input ' + (!isPasswordLogin ? 'input-with-btn' : '')"
                        :type="isPasswordLogin ? 'password' : 'number'"
                        :placeholder="isPasswordLogin ? '请输入密码' : '请输入验证码'"
                        :maxlength="isPasswordLogin ? '20' : '6'"
                        :value="password"
                        @input="inputPassword"
                    />
                    <view v-if="!isPasswordLogin" :class="'verify-code-btn ' + (countdown > 0 ? 'disabled' : '')" @tap.stop.prevent="getVerifyCode">
                        {{ countdown > 0 ? countdown + 's' : '获取验证码' }}
                    </view>
                </view>
            </view>

            <!-- 登录方式切换 -->
            <view class="login-type-switch" @tap="switchLoginType">
                <text>{{ isPasswordLogin ? '验证码登录' : '密码登录' }}</text>
            </view>

            <!-- 登录按钮 -->
            <button :class="'login-btn ' + (isLoading ? 'loading' : '')" @tap="passwordLogin" :disabled="isLoading">
                {{ isLoading ? '登录中...' : '登 录' }}
            </button>
        </view>
    </view>
</template>

<script>
// pages/login/login.js
const app = getApp();
const authApi = require('../../api/auth');
const userApi = require('../../api/user');
const { BASE_URL } = require('../../utils/request');
export default {
    data() {
        return {
            phone: '',
            password: '',
            isPasswordLogin: true,
            isAgree: false,
            countdown: 0,
            isLoading: false,
            usePhoneLogin: false,
            // 完善资料步骤
            showProfile: false,
            profileNickname: '',
            profileAvatar: '',
            avatarUploading: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {},
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {},
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {},
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {},
    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {},
    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {},
    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {},
    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {},
    methods: {
        toggleAgree: function () {
            this.setData({
                isAgree: !this.isAgree
            });
        },

        // 切换登录方式
        switchLoginType: function () {
            this.setData({
                isPasswordLogin: !this.isPasswordLogin
            });
        },

        // 输入手机号
        inputPhone: function (e) {
            this.setData({
                phone: e.detail.value
            });
        },

        // 输入密码
        inputPassword: function (e) {
            this.setData({
                password: e.detail.value
            });
        },

        // 获取验证码
        getVerifyCode: function () {
            const { phone } = this;
            if (!phone) {
                uni.showToast({
                    title: '请输入手机号',
                    icon: 'none'
                });
                return;
            }
            if (!/^1\d{10}$/.test(phone)) {
                uni.showToast({
                    title: '手机号格式不正确',
                    icon: 'none'
                });
                return;
            }
            if (this.countdown > 0) {
                return;
            }

            // 调用后端发送验证码接口
            authApi
                .sendCode(phone)
                .then((res) => {
                    // 开始倒计时
                    this.setData({
                        countdown: 60
                    });
                    const timer = setInterval(() => {
                        const countdown = this.countdown - 1;
                        if (countdown <= 0) {
                            clearInterval(timer);
                            this.setData({
                                countdown: 0
                            });
                        } else {
                            this.setData({
                                countdown
                            });
                        }
                    }, 1000);

                    // 测试环境显示验证码（生产环境删除）
                    if (res.code) {
                        uni.showModal({
                            title: '验证码',
                            content: `您的验证码是：${res.code}`,
                            showCancel: false
                        });
                    } else {
                        uni.showToast({
                            title: '验证码已发送',
                            icon: 'success'
                        });
                    }
                })
                .catch((err) => {
                    console.error('发送验证码失败', err);
                    uni.showToast({
                        title: err.message || '发送验证码失败',
                        icon: 'none'
                    });
                });
        },

        // 密码登录 or 验证码登录
        passwordLogin: function () {
            const { phone, password, isPasswordLogin, isAgree } = this;

            // 验证手机号和密码/验证码
            if (!phone) {
                uni.showToast({
                    title: '请输入手机号',
                    icon: 'none'
                });
                return;
            }
            if (!password) {
                uni.showToast({
                    title: `请输入${isPasswordLogin ? '密码' : '验证码'}`,
                    icon: 'none'
                });
                return;
            }
            if (!/^1\d{10}$/.test(phone)) {
                uni.showToast({
                    title: '手机号格式不正确',
                    icon: 'none'
                });
                return;
            }
            if (!isAgree) {
                uni.showToast({
                    title: '请同意用户协议',
                    icon: 'none'
                });
                return;
            }

            // 显示加载状态
            this.setData({
                isLoading: true
            });

            // 根据登录方式调用不同的API
            const loginApi = isPasswordLogin ? authApi.phoneLogin(phone, password) : authApi.phoneCodeLogin(phone, password);
            loginApi
                .then((data) => {
                    // 保存token
                    uni.setStorageSync('token', data.token);

                    // 保存用户信息
                    const userInfo = {
                        nickName: data.nickname || '用户',
                        avatarUrl: data.avatar || '/static/images/icons/default-avatar.png',
                        phone: phone
                    };
                    app.globalData.login(userInfo);

                    // 隐藏加载状态
                    this.setData({
                        isLoading: false
                    });

                    // 提示登录成功
                    uni.showToast({
                        title: '登录成功',
                        icon: 'success'
                    });

                    // 延迟返回上一页或跳转到首页
                    setTimeout(() => {
                        const pages = getCurrentPages();
                        if (pages.length > 1) {
                            uni.navigateBack();
                        } else {
                            uni.switchTab({
                                url: '/pages/index/index'
                            });
                        }
                    }, 1500);
                })
                .catch((err) => {
                    console.error('登录失败', err);
                    this.setData({
                        isLoading: false
                    });
                    uni.showToast({
                        title: err.message || '登录失败',
                        icon: 'none'
                    });
                });
        },

        // 切换手机号登录
        togglePhoneLogin: function () {
            this.setData({ usePhoneLogin: !this.usePhoneLogin });
        },

        // 微信一键登录：先用 code 换取登录态，再按需补全头像昵称
        wxLogin: function () {
            if (!this.isAgree) {
                uni.showToast({ title: '请先同意用户协议', icon: 'none' });
                return;
            }

            this.setData({ isLoading: true });

            uni.login({
                provider: 'weixin',
                success: (res) => {
                    if (!res.code) {
                        this.setData({ isLoading: false });
                        uni.showToast({ title: '获取登录凭证失败', icon: 'none' });
                        return;
                    }
                    authApi
                        .wxLogin(res.code)
                        .then((data) => {
                            uni.setStorageSync('token', data.token);
                            const user = data.userInfo || {};
                            app.globalData.login({
                                nickName: user.nickname || '微信用户',
                                avatarUrl: user.avatar || '/static/images/icons/default-avatar.png',
                                phone: user.phone || ''
                            });
                            this.setData({ isLoading: false });

                            // 老用户已有头像昵称则直接进入，新用户引导补全资料
                            if (user.nickname && user.avatar) {
                                uni.showToast({ title: '登录成功', icon: 'success' });
                                setTimeout(() => this.backToPrevPage(), 1200);
                            } else {
                                this.setData({
                                    showProfile: true,
                                    profileNickname: user.nickname || '',
                                    profileAvatar: user.avatar || ''
                                });
                            }
                        })
                        .catch((err) => {
                            console.error('微信登录失败', err);
                            this.setData({ isLoading: false });
                            uni.showToast({ title: (err && err.message) || '登录失败，请重试', icon: 'none' });
                        });
                },
                fail: () => {
                    this.setData({ isLoading: false });
                    uni.showToast({ title: '微信登录失败', icon: 'none' });
                }
            });
        },

        // 选择微信头像并上传
        onChooseAvatar: function (e) {
            const tempPath = e.detail.avatarUrl;
            if (!tempPath) {
                return;
            }
            this.setData({ avatarUploading: true });
            uni.uploadFile({
                url: BASE_URL + '/file/upload',
                filePath: tempPath,
                name: 'file',
                header: { Authorization: uni.getStorageSync('token') },
                success: (uploadRes) => {
                    this.setData({ avatarUploading: false });
                    try {
                        const result = JSON.parse(uploadRes.data);
                        if (result.code === 200 && result.data) {
                            this.setData({ profileAvatar: result.data });
                        } else {
                            uni.showToast({ title: result.message || '头像上传失败', icon: 'none' });
                        }
                    } catch (err) {
                        uni.showToast({ title: '头像上传失败', icon: 'none' });
                    }
                },
                fail: () => {
                    this.setData({ avatarUploading: false });
                    uni.showToast({ title: '头像上传失败', icon: 'none' });
                }
            });
        },

        onNicknameInput: function (e) {
            this.setData({ profileNickname: e.detail.value });
        },

        // 保存头像昵称
        saveProfile: function () {
            const nickname = (this.profileNickname || '').trim();
            if (!nickname) {
                uni.showToast({ title: '请填写昵称', icon: 'none' });
                return;
            }
            userApi
                .updateUserInfo({ nickname, avatar: this.profileAvatar || '' })
                .then(() => {
                    const current = uni.getStorageSync('userInfo') || {};
                    app.globalData.login({
                        ...current,
                        nickName: nickname,
                        avatarUrl: this.profileAvatar || current.avatarUrl || '/static/images/icons/default-avatar.png'
                    });
                    uni.showToast({ title: '登录成功', icon: 'success' });
                    setTimeout(() => this.backToPrevPage(), 1200);
                })
                .catch((err) => {
                    console.error('保存资料失败', err);
                    uni.showToast({ title: '保存失败，请重试', icon: 'none' });
                });
        },

        // 跳过补全，直接进入
        skipProfile: function () {
            this.backToPrevPage();
        },

        backToPrevPage: function () {
            const pages = getCurrentPages();
            if (pages.length > 1) {
                uni.navigateBack();
            } else {
                uni.switchTab({ url: '/pages/index/index' });
            }
        },

        // 跳转到注册页面
        goToRegister: function () {
            uni.navigateTo({
                url: '/pages/register/register'
            });
        },

        // 查看协议
        viewAgreement: function () {
            uni.showModal({
                title: '用户协议',
                content: '这是用户协议内容...',
                showCancel: false
            });
        },

        goToUserAgreement() {
            console.log('占位：函数 goToUserAgreement 未声明');
        },

        goToPrivacyPolicy() {
            console.log('占位：函数 goToPrivacyPolicy 未声明');
        }
    }
};
</script>
<style>
/* pages/login/login.wxss */
.login-container {
    min-height: 100vh;
    padding: 0 40rpx;
    background-color: #000;
    position: relative;
}
.login-container::before {
    content: '';
    position: absolute;
    top: 0; left: 0; right: 0;
    height: 500rpx;
    background: radial-gradient(ellipse at 50% 0%, #1a1a1a 0%, #000 70%);
    z-index: 0;
}
.login-header {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 100rpx 0 60rpx;
    position: relative;
    z-index: 1;
}
.logo-area { text-align: center; margin-bottom: 30rpx; }
.logo-img { width: 240rpx; height: 240rpx; border-radius: 24rpx; }
.title {
    font-size: 32rpx;
    font-weight: bold;
    color: #fff;
}
.login-form {
    padding: 40rpx;
    background-color: #1c1c1e;
    border-radius: 30rpx;
    position: relative;
    z-index: 1;
}
.input-label { font-size: 28rpx; color: #999; margin-bottom: 16rpx; }
.form-item { position: relative; margin-bottom: 40rpx; }
.input-wrapper {
    position: relative;
    display: flex;
    align-items: center;
    border-bottom: 1px solid #333;
}

.input {
    flex: 1;
    height: 90rpx;
    font-size: 32rpx;
    color: #fff;
    background-color: transparent;
    border: none;
}

.input-with-btn {
    padding-right: 140rpx;
}

.verify-code-btn {
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-50%);
    font-size: 28rpx;
    color: #e8c547;
    padding: 10rpx 20rpx;
    white-space: nowrap;
    background-color: transparent;
    z-index: 10;
}

.verify-code-btn.disabled {
    color: #999;
}

/* 登录方式切换 */
.login-type-switch {
    text-align: right;
    font-size: 28rpx;
    color: var(--primary-color);
    margin-bottom: 40rpx;
}

/* 用户协议 */
.agreement {
    display: flex;
    align-items: center;
    margin-bottom: 40rpx;
    flex-wrap: wrap;
}

.checkbox {
    width: 36rpx;
    height: 36rpx;
    border: 1px solid #ddd;
    border-radius: 50%;
    margin-right: 16rpx;
    position: relative;
    box-sizing: border-box;
}

.checkbox.checked {
    border: none;
    background-color: var(--primary-color);
}

.checkbox.checked::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -60%) rotate(-45deg);
    width: 16rpx;
    height: 8rpx;
    border-left: 2px solid #fff;
    border-bottom: 2px solid #fff;
}

.agreement-text {
    font-size: 24rpx;
    color: #999;
}

.agreement-link {
    font-size: 24rpx;
    color: var(--primary-color);
}

/* 登录按钮 */
.login-btn {
    width: 100%;
    height: 90rpx;
    background-color: #e8c547;
    color: #000;
    font-size: 32rpx;
    font-weight: bold;
    border-radius: 45rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 30rpx;
    border: none;
}
.wx-login-btn {
    width: 100%;
    height: 90rpx;
    background-color: #2c2c2e;
    color: #fff;
    font-size: 32rpx;
    border-radius: 45rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 40rpx;
    border: none;
}

.login-btn.loading {
    opacity: 0.8;
}

/* 微信登录按钮 */
.wx-login-btn {
    width: 100%;
    height: 90rpx;
    background-color: #2c2c2e;
    color: #fff;
    font-size: 32rpx;
    border-radius: 45rpx;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-bottom: 40rpx;
    border: none;
}

.wx-login-btn.loading {
    opacity: 0.8;
}

/* 微信登录主入口 */
.wx-login-btn.primary {
    background: linear-gradient(135deg, #f7dc8a 0%, #c99a3a 100%);
    color: #1a1a1a;
    font-weight: bold;
    margin-bottom: 30rpx;
    box-shadow: 0 8rpx 24rpx rgba(232, 197, 71, 0.28);
}

.wx-icon {
    width: 48rpx;
    height: 48rpx;
    margin-right: 16rpx;
}

.phone-form {
    margin-top: 24rpx;
}

/* 手机号登录入口 */
.phone-login-entry {
    text-align: center;
    font-size: 26rpx;
    color: #8a8a8e;
    margin-bottom: 6rpx;
}

/* 完善头像昵称 */
.profile-setup {
    padding: 50rpx 40rpx 40rpx;
    background-color: #1c1c1e;
    border-radius: 30rpx;
    position: relative;
    z-index: 1;
    text-align: center;
}

.profile-title {
    display: block;
    font-size: 36rpx;
    font-weight: bold;
    color: #f3d780;
    letter-spacing: 2rpx;
}

.profile-tip {
    display: block;
    font-size: 24rpx;
    color: #8a8a8e;
    margin-top: 12rpx;
}

.avatar-picker {
    width: 180rpx;
    height: auto;
    margin: 40rpx auto 30rpx;
    background: transparent;
    border: none;
    padding: 0;
    line-height: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.avatar-picker::after {
    border: none;
}

.avatar-preview {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    border: 3rpx solid #c99a3a;
    background-color: #2c2c2e;
}

.avatar-hint {
    font-size: 24rpx;
    color: #8a8a8e;
    margin-top: 16rpx;
}

.profile-item {
    display: flex;
    align-items: center;
    border-bottom: 1px solid #333;
    margin: 20rpx 0 50rpx;
}

.profile-label {
    font-size: 28rpx;
    color: #999;
    width: 100rpx;
    text-align: left;
}

.profile-input {
    flex: 1;
    height: 90rpx;
    font-size: 30rpx;
    color: #fff;
    text-align: left;
}

.profile-skip {
    font-size: 26rpx;
    color: #8a8a8e;
    margin-top: 10rpx;
}

/* 注册链接 */
.register-link {
    text-align: center;
    font-size: 28rpx;
    color: #999;
}

.register-text {
    color: var(--primary-color);
    margin-left: 10rpx;
}
</style>
