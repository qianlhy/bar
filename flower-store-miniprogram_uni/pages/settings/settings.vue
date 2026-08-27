<template>
    <!-- pages/settings/settings.wxml -->
    <view class="settings-container">
        <!-- 用户信息编辑区域 -->
        <view class="user-profile">
            <view class="profile-header">个人资料</view>

            <!-- 非编辑模式 -->
            <view class="profile-content" v-if="!editMode">
                <view class="profile-item avatar-item">
                    <text class="item-label">头像</text>
                    <view class="item-content">
                        <image class="user-avatar" :src="avatarUrl" mode="aspectFill"></image>
                    </view>
                    <view class="item-action" @tap="enterEditMode">
                        <text>编辑</text>
                        <text class="arrow">></text>
                    </view>
                </view>

                <view class="profile-item">
                    <text class="item-label">昵称</text>
                    <view class="item-content">{{ nickName || '未设置' }}</view>
                </view>
            </view>

            <!-- 编辑模式 -->
            <view class="profile-edit" v-if="editMode">
                <view class="edit-item">
                    <text class="edit-label">头像</text>
                    <view class="edit-avatar-wrapper">
                        <image class="edit-preview-avatar" :src="avatarUrl" mode="aspectFill"></image>
                        <button class="avatar-button" open-type="chooseAvatar" @chooseavatar="chooseAvatar"></button>
                    </view>
                </view>

                <view class="edit-item">
                    <text class="edit-label">昵称</text>
                    <input class="edit-input" type="nickname" :value="nickName" @input="inputNickName" @change="onNicknameChange" placeholder="请输入昵称" maxlength="20" />
                </view>

                <view class="edit-actions">
                    <view class="btn cancel" @tap="cancelEdit">取消</view>
                    <view class="btn save" @tap="saveUserInfo">保存</view>
                </view>
            </view>

            <!-- 未登录状态 -->
            <view class="not-login" v-if="!isLogin && !editMode">
                <button class="login-btn" @tap="goToLogin">登录/注册</button>
            </view>
        </view>

        <!-- 系统设置区域 -->
        <view class="system-settings">
            <view class="settings-header">系统设置</view>

            <view class="settings-item" @tap="clearCache">
                <text class="item-label">清除缓存</text>
                <view class="item-content">{{ cacheSize }}</view>
                <text class="arrow">></text>
            </view>

            <view class="settings-item" @tap="aboutUs">
                <text class="item-label">关于我们</text>
                <text class="arrow">></text>
            </view>

            <view class="settings-item">
                <text class="item-label">当前版本</text>
                <view class="item-content">{{ version }}</view>
            </view>
        </view>

        <!-- 隐私政策和用户协议 -->
        <view class="agreement">
            <navigator url="/pages/agreement/agreement?type=privacy" class="agreement-link">隐私政策</navigator>
            <text class="divider">|</text>
            <navigator url="/pages/agreement/agreement?type=user" class="agreement-link">用户协议</navigator>
        </view>

        <!-- 退出登录 -->
        <view class="logout-wrapper" v-if="isLogin">
            <view class="logout-btn" @tap="logout">退出登录</view>
        </view>
    </view>
</template>

<script>
// pages/settings/settings.js
const app = getApp();
const userApi = require('../../api/user');
export default {
    data() {
        return {
            userInfo: null,
            isLogin: false,
            editMode: false,
            nickName: '',
            avatarUrl: '',
            cacheSize: '0KB',
            version: '1.0.0'
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        this.checkLoginStatus();
        this.getStorageInfo();
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {},
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.checkLoginStatus();
    },
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
        // 检查登录状态
        checkLoginStatus: function () {
            const userInfo = uni.getStorageSync('userInfo');
            const isLogin = !!uni.getStorageSync('token');
            if (userInfo && isLogin) {
                this.setData({
                    userInfo,
                    isLogin,
                    nickName: userInfo.nickname || userInfo.nickName || '微信用户',
                    avatarUrl: userInfo.avatar || userInfo.avatarUrl || '/static/images/icons/default-avatar.png'
                });
            } else {
                this.setData({
                    userInfo: null,
                    isLogin: false,
                    nickName: '',
                    avatarUrl: '/static/images/icons/default-avatar.png'
                });
            }
        },

        // 获取缓存大小
        getStorageInfo: function () {
            uni.getStorageInfo({
                success: (res) => {
                    // 将字节转换为KB或MB
                    let size = res.currentSize;
                    let sizeStr = '';
                    if (size < 1024) {
                        sizeStr = size + 'KB';
                    } else {
                        sizeStr = (size / 1024).toFixed(2) + 'MB';
                    }
                    this.setData({
                        cacheSize: sizeStr
                    });
                }
            });
        },

        // 进入编辑模式
        enterEditMode: function () {
            if (!this.isLogin) {
                uni.navigateTo({
                    url: '/pages/login/login'
                });
                return;
            }

            // 确保编辑模式下使用当前用户的信息
            const userInfo = uni.getStorageSync('userInfo');
            this.setData({
                editMode: true,
                nickName: userInfo.nickname || userInfo.nickName || '微信用户',
                avatarUrl: userInfo.avatar || userInfo.avatarUrl || '/static/images/icons/default-avatar.png'
            });
        },

        // 取消编辑
        cancelEdit: function () {
            const userInfo = this.userInfo;
            this.setData({
                editMode: false,
                nickName: userInfo.nickname || userInfo.nickName || '微信用户',
                avatarUrl: userInfo.avatar || userInfo.avatarUrl || '/static/images/icons/default-avatar.png'
            });
        },

        // 输入昵称
        inputNickName: function (e) {
            this.setData({
                nickName: e.detail.value
            });
        },

        // 从昵称输入框获取昵称
        onNicknameChange: function (e) {
            this.setData({
                nickName: e.detail.value
            });
        },

        // 选择头像
        chooseAvatar: function (e) {
            // 通过开放能力按钮获取头像
            const avatarUrl = e.detail.avatarUrl;
            // 上传到服务器
            uni.showLoading({
                title: '上传中...',
                mask: true
            });
            uni.uploadFile({
                url: 'https://bar.twst.work/api/file/upload',
                filePath: avatarUrl,
                name: 'file',
                header: {
                    Authorization: uni.getStorageSync('token')
                },
                success: (uploadRes) => {
                    uni.hideLoading();
                    const data = JSON.parse(uploadRes.data);
                    if (data.code === 200) {
                        this.setData({
                            avatarUrl: data.data
                        });
                        uni.showToast({
                            title: '上传成功',
                            icon: 'success'
                        });
                    } else {
                        uni.showToast({
                            title: data.message || '上传失败',
                            icon: 'none'
                        });
                    }
                },
                fail: (err) => {
                    uni.hideLoading();
                    console.error('上传失败', err);
                    uni.showToast({
                        title: '上传失败',
                        icon: 'none'
                    });
                }
            });
        },

        // 上传头像
        uploadAvatar: function () {
            uni.chooseImage({
                count: 1,
                sizeType: ['compressed'],
                sourceType: ['album', 'camera'],
                success: (res) => {
                    const tempFilePath = res.tempFilePaths[0];
                    uni.showLoading({
                        title: '上传中...',
                        mask: true
                    });

                    // 上传到服务器
                    uni.uploadFile({
                        url: 'https://bar.twst.work/api/file/upload',
                        filePath: tempFilePath,
                        name: 'file',
                        header: {
                            Authorization: uni.getStorageSync('token')
                        },
                        success: (uploadRes) => {
                            uni.hideLoading();
                            const data = JSON.parse(uploadRes.data);
                            if (data.code === 200) {
                                this.setData({
                                    avatarUrl: data.data
                                });
                                uni.showToast({
                                    title: '上传成功',
                                    icon: 'success'
                                });
                            } else {
                                uni.showToast({
                                    title: data.message || '上传失败',
                                    icon: 'none'
                                });
                            }
                        },
                        fail: (err) => {
                            uni.hideLoading();
                            console.error('上传失败', err);
                            uni.showToast({
                                title: '上传失败',
                                icon: 'none'
                            });
                        }
                    });
                }
            });
        },

        // 使用微信头像和昵称
        useWechatProfile: function () {
            // 获取微信用户信息
            uni.getUserProfile({
                desc: '用于完善用户资料',
                success: (res) => {
                    const userInfo = res.userInfo;
                    this.setData({
                        nickName: userInfo.nickName,
                        avatarUrl: userInfo.avatarUrl
                    });
                },
                fail: (err) => {
                    uni.showToast({
                        title: '获取用户信息失败',
                        icon: 'none'
                    });
                }
            });
        },

        // 保存用户信息
        saveUserInfo: function () {
            if (!this.nickName.trim()) {
                uni.showToast({
                    title: '昵称不能为空',
                    icon: 'none'
                });
                return;
            }

            // 检查登录状态
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({
                    title: '请先登录',
                    icon: 'none'
                });
                return;
            }
            uni.showLoading({
                title: '保存中...',
                mask: true
            });

            // 调用后端接口更新用户信息
            userApi
                .updateUserInfo({
                    nickname: this.nickName,
                    avatar: this.avatarUrl
                })
                .then(() => {
                    uni.hideLoading();

                    // 更新本地缓存的用户信息
                    const userInfo = {
                        ...this.userInfo,
                        nickname: this.nickName,
                        avatar: this.avatarUrl,
                        nickName: this.nickName,
                        avatarUrl: this.avatarUrl
                    };
                    uni.setStorageSync('userInfo', userInfo);
                    this.setData({
                        userInfo,
                        editMode: false
                    });
                    uni.showToast({
                        title: '保存成功',
                        icon: 'success'
                    });
                })
                .catch((err) => {
                    uni.hideLoading();
                    console.error('保存失败', err);
                    uni.showToast({
                        title: err.message || '保存失败',
                        icon: 'none'
                    });
                });
        },

        // 清除缓存
        clearCache: function () {
            uni.showModal({
                title: '提示',
                content: '确定要清除缓存吗？',
                success: (res) => {
                    if (res.confirm) {
                        // 保留用户信息和登录状态
                        const userInfo = uni.getStorageSync('userInfo');
                        const token = uni.getStorageSync('token');

                        // 清空缓存
                        uni.clearStorageSync();

                        // 恢复用户信息和登录状态
                        if (userInfo && token) {
                            uni.setStorageSync('userInfo', userInfo);
                            uni.setStorageSync('isLoggedIn', true);
                            uni.setStorageSync('token', token);
                        }

                        // 重新初始化数据
                        app.globalData.initMockData();

                        // 更新缓存大小显示
                        this.getStorageInfo();
                        uni.showToast({
                            title: '缓存已清除',
                            icon: 'success'
                        });
                    }
                }
            });
        },

        // 关于我们
        aboutUs: function () {
            uni.showModal({
                title: '关于梭哈酒馆',
                content: '梭哈酒馆 · 南京店\nALL IN TAVERN\n当前版本 ' + this.version,
                showCancel: false
            });
        },

        // 前往登录
        goToLogin: function () {
            uni.navigateTo({
                url: '/pages/login/login'
            });
        },

        // 退出登录
        logout: function () {
            uni.showModal({
                title: '退出登录',
                content: '确定要退出当前账号吗？',
                success: (res) => {
                    if (!res.confirm) {
                        return;
                    }
                    uni.removeStorageSync('token');
                    uni.removeStorageSync('cartSpecs');
                    if (app && app.globalData && typeof app.globalData.logout === 'function') {
                        app.globalData.logout();
                    } else {
                        uni.removeStorageSync('userInfo');
                        uni.removeStorageSync('isLoggedIn');
                    }
                    this.checkLoginStatus();
                    uni.showToast({ title: '已退出登录', icon: 'success' });
                    setTimeout(() => {
                        uni.switchTab({ url: '/pages/index/index' });
                    }, 1200);
                }
            });
        }
    }
};
</script>
<style>
.settings-container {
    min-height: 100vh;
    box-sizing: border-box;
    background: var(--bg-page);
    padding: 24rpx 24rpx calc(60rpx + env(safe-area-inset-bottom));
}

/* 退出登录 */
.logout-wrapper {
    padding: 30rpx 0 20rpx;
}

.logout-btn {
    background: var(--bg-card);
    border: 1rpx solid rgba(196, 30, 58, 0.35);
    color: #f0616f;
    font-size: 30rpx;
    font-weight: 600;
    text-align: center;
    padding: 28rpx 0;
    border-radius: 20rpx;
}

/* 个人资料样式 */
.user-profile,
.system-settings {
    margin-bottom: 24rpx;
    padding: 0 28rpx;
    border: 1rpx solid var(--border-subtle);
    border-radius: 22rpx;
    background: var(--bg-card-gradient);
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.24);
}

.profile-header,
.settings-header {
    position: relative;
    padding: 30rpx 0 26rpx 22rpx;
    font-size: 30rpx;
    font-weight: 600;
    color: var(--text-primary);
    border-bottom: 1rpx solid var(--border-subtle);
}

.profile-header::before,
.settings-header::before {
    content: '';
    position: absolute;
    left: 0;
    top: 34rpx;
    width: 6rpx;
    height: 28rpx;
    border-radius: 4rpx;
    background: linear-gradient(180deg, var(--gold-light), var(--gold-dark));
}

.profile-content {
    padding: 0;
}

.profile-item {
    display: flex;
    align-items: center;
    padding: 28rpx 0;
    border-bottom: 1rpx solid var(--border-subtle);
}

.profile-item:last-child {
    border-bottom: none;
}

.avatar-item {
    align-items: center;
}

.item-label {
    width: 140rpx;
    font-size: 29rpx;
    color: var(--text-regular);
}

.item-content {
    flex: 1;
    font-size: 29rpx;
    color: var(--text-muted);
}

.user-avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
    background-color: var(--bg-sunken);
    border: 2rpx solid rgba(232, 197, 71, 0.3);
}

.item-action {
    display: flex;
    align-items: center;
    font-size: 27rpx;
    color: var(--gold);
}

.arrow {
    margin-left: 10rpx;
    color: var(--text-faint);
}

/* 个人资料编辑样式 */
.profile-edit {
    padding: 30rpx 0;
}

.edit-item {
    margin-bottom: 30rpx;
}

.edit-label {
    display: block;
    font-size: 29rpx;
    color: var(--text-regular);
    margin-bottom: 20rpx;
}

/* 新的头像选择样式 */
.edit-avatar-wrapper {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    overflow: hidden;
}

.edit-preview-avatar {
    width: 100%;
    height: 100%;
    border-radius: 50%;
}

.avatar-button {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    padding: 0;
    margin: 0;
    background: transparent;
    border: none;
    outline: none;
}

.avatar-button::after {
    border: none;
}

.avatar-mask {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.4);
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 26rpx;
}

/* 原有的头像样式（可保留兼容） */
.edit-avatar {
    position: relative;
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    overflow: hidden;
}

.edit-input {
    width: 100%;
    box-sizing: border-box;
    height: 88rpx;
    border: 1rpx solid var(--border-subtle);
    border-radius: 14rpx;
    padding: 0 24rpx;
    font-size: 28rpx;
    color: var(--text-primary);
    background-color: var(--bg-sunken);
}

.edit-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 40rpx;
}

.btn {
    width: 180rpx;
    height: 84rpx;
    font-size: 28rpx;
    font-weight: 600;
    margin: 0;
    padding: 0;
    line-height: 84rpx;
    text-align: center;
    border-radius: 42rpx;
}

.cancel {
    background-color: var(--bg-elevated);
    color: var(--text-regular);
    margin-right: 20rpx;
}

.save {
    background: var(--gold-gradient);
    color: #171717;
    box-shadow: 0 8rpx 22rpx rgba(232, 197, 71, 0.22);
}

/* 未登录状态 */
.not-login {
    padding: 30rpx 0;
    display: flex;
    justify-content: center;
}

.login-btn {
    width: 80%;
    height: 88rpx;
    font-size: 30rpx;
    font-weight: 600;
    color: #171717;
    background: var(--gold-gradient);
    border: none;
    border-radius: 44rpx;
    line-height: 88rpx;
    box-shadow: 0 8rpx 22rpx rgba(232, 197, 71, 0.22);
}

.login-btn::after {
    border: none;
}

.settings-item {
    display: flex;
    align-items: center;
    padding: 30rpx 0;
    border-bottom: 1rpx solid var(--border-subtle);
}

.settings-item:last-child {
    border-bottom: none;
}

.settings-item .item-label {
    width: auto;
    flex: 1;
}

.settings-item .item-content {
    flex: none;
    margin-right: 12rpx;
    font-size: 27rpx;
    color: var(--text-faint);
}

/* 协议样式 */
.agreement {
    display: flex;
    justify-content: center;
    padding: 20rpx 0;
    font-size: 25rpx;
    color: var(--text-faint);
}

.agreement-link {
    color: var(--text-muted);
}

.divider {
    margin: 0 20rpx;
    height: auto;
    background: none;
}
</style>
