<template>
    <!-- pages/address/edit.wxml -->
    <view class="address-edit-container">
        <view class="form-container">
            <!-- 收货人 -->
            <view class="form-item">
                <view class="form-label">收货人</view>
                <input class="form-input" placeholder="请输入收货人姓名" :value="name" @input="inputName" />
            </view>

            <!-- 手机号码 -->
            <view class="form-item">
                <view class="form-label">手机号码</view>
                <input class="form-input" type="number" placeholder="请输入手机号码" maxlength="11" :value="phone" @input="inputPhone" />
            </view>

            <!-- 所在地区 -->
            <view class="form-item">
                <view class="form-label">所在地区</view>
                <region-picker @change="bindRegionChange" :value="region" class="region-picker">
                    <view class="picker-content">
                        <text v-if="region[0] && region[1] && region[2]">{{ region[0] }} {{ region[1] }} {{ region[2] }}</text>
                        <text v-else class="placeholder">请选择所在地区</text>
                        <text class="arrow">></text>
                    </view>
                </region-picker>
            </view>

            <!-- 详细地址 -->
            <view class="form-item">
                <view class="form-label">详细地址</view>
                <textarea class="form-textarea" placeholder="请输入详细地址，如街道、门牌号、小区、楼栋号、单元室等" :value="address" @input="inputAddress"></textarea>
            </view>

            <!-- 设为默认地址 -->
            <view class="form-item default-item" @tap="toggleDefault">
                <view class="form-label">设为默认地址</view>
                <view class="switch-box">
                    <view :class="'switch ' + (isDefault ? 'on' : 'off')">
                        <view class="switch-handle"></view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 保存按钮 -->
        <view class="footer">
            <button :class="'save-btn ' + (isLoading ? 'loading' : '')" @tap="saveAddress" :disabled="isLoading">
                {{ isLoading ? '保存中...' : '保存' }}
            </button>
        </view>
    </view>
</template>

<script>
// pages/address/edit.js
const app = getApp();
const addressApi = require('../../api/address');
export default {
    data() {
        return {
            id: '',
            name: '',
            phone: '',
            province: '',
            city: '',
            district: '',
            address: '',
            isDefault: false,
            isEdit: false,
            region: ['', '', ''],
            isLoading: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        const { id } = options;
        if (id) {
            // 编辑模式
            this.setData({
                isEdit: true,
                id
            });
            this.getAddressDetail(id);
        }
    },
    methods: {
        // 获取地址详情
        getAddressDetail: function (id) {
            addressApi
                .getAddressById(id)
                .then((address) => {
                    this.setData({
                        name: address.name,
                        phone: address.phone,
                        province: address.province,
                        city: address.city,
                        district: address.district,
                        address: address.address,
                        isDefault: address.isDefault === 1,
                        region: [address.province, address.city, address.district]
                    });
                })
                .catch((err) => {
                    console.error('获取地址详情失败', err);
                });
        },

        // 输入姓名
        inputName: function (e) {
            this.setData({
                name: e.detail.value
            });
        },

        // 输入手机号
        inputPhone: function (e) {
            this.setData({
                phone: e.detail.value
            });
        },

        // 选择地区
        bindRegionChange: function (e) {
            const region = e.detail.value;
            this.setData({
                region,
                province: region[0],
                city: region[1],
                district: region[2]
            });
        },

        // 输入详细地址
        inputAddress: function (e) {
            this.setData({
                address: e.detail.value
            });
        },

        // 切换默认地址
        toggleDefault: function () {
            this.setData({
                isDefault: !this.isDefault
            });
        },

        // 保存地址
        saveAddress: function () {
            const { name, phone, province, city, district, address, isDefault, isEdit, id } = this;

            // 表单校验
            if (!name) {
                uni.showToast({
                    title: '请输入收货人姓名',
                    icon: 'none'
                });
                return;
            }
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
            if (!province || !city || !district) {
                uni.showToast({
                    title: '请选择所在地区',
                    icon: 'none'
                });
                return;
            }
            if (!address) {
                uni.showToast({
                    title: '请输入详细地址',
                    icon: 'none'
                });
                return;
            }

            // 显示加载状态
            this.setData({
                isLoading: true
            });

            // 构建地址对象
            const addressData = {
                name,
                phone,
                province,
                city,
                district,
                address,
                isDefault: isDefault ? 1 : 0
            };
            if (isEdit) {
                // 编辑模式
                addressData.id = id;
                addressApi
                    .updateAddress(addressData)
                    .then(() => {
                        this.setData({
                            isLoading: false
                        });
                        uni.showToast({
                            title: '保存成功',
                            icon: 'success'
                        });
                        setTimeout(() => {
                            uni.navigateBack();
                        }, 1500);
                    })
                    .catch((err) => {
                        console.error('更新地址失败', err);
                        this.setData({
                            isLoading: false
                        });
                    });
            } else {
                // 新增模式
                addressApi
                    .addAddress(addressData)
                    .then(() => {
                        this.setData({
                            isLoading: false
                        });
                        uni.showToast({
                            title: '保存成功',
                            icon: 'success'
                        });
                        setTimeout(() => {
                            uni.navigateBack();
                        }, 1500);
                    })
                    .catch((err) => {
                        console.error('添加地址失败', err);
                        this.setData({
                            isLoading: false
                        });
                    });
            }
        }
    }
};
</script>
<style>
/* pages/address/edit.wxss */
.address-edit-container {
    min-height: 100vh;
    background-color: #f7f7f7;
    padding-bottom: 120rpx;
}

/* 表单容器 */
.form-container {
    background-color: #fff;
    padding: 0 30rpx;
}

/* 表单项 */
.form-item {
    padding: 30rpx 0;
    border-bottom: 1px solid #f5f5f5;
}

.form-label {
    font-size: 28rpx;
    color: #333;
    margin-bottom: 20rpx;
}

.form-input {
    width: 100%;
    height: 80rpx;
    font-size: 28rpx;
    color: #333;
}

.form-textarea {
    width: 100%;
    height: 160rpx;
    font-size: 28rpx;
    color: #333;
}

/* 地区选择器 */
.region-picker {
    width: 100%;
}

.picker-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 80rpx;
    font-size: 28rpx;
    color: #333;
}

.placeholder {
    color: #999;
}

.arrow {
    color: #999;
    font-size: 28rpx;
}

/* 默认地址开关 */
.default-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-right: 10rpx;
}

.default-item .form-label {
    margin-bottom: 0;
}

.switch-box {
    display: flex;
    align-items: center;
}

.switch {
    position: relative;
    width: 100rpx;
    height: 60rpx;
    border-radius: 30rpx;
    background-color: #ccc;
    transition: all 0.3s;
}

.switch.on {
    background-color: var(--primary-color);
}

.switch-handle {
    position: absolute;
    top: 5rpx;
    left: 5rpx;
    width: 50rpx;
    height: 50rpx;
    border-radius: 50%;
    background-color: #fff;
    transition: all 0.3s;
}

.switch.on .switch-handle {
    transform: translateX(40rpx);
}

/* 底部按钮 */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    padding: 20rpx;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.save-btn {
    background-color: var(--primary-color);
    color: #fff;
    font-size: 30rpx;
    border-radius: 40rpx;
}

.save-btn.loading {
    opacity: 0.8;
}
</style>
