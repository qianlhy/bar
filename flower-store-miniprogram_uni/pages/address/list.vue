<template>
    <!-- pages/address/list.wxml -->
    <view class="address-container">
        <!-- 地址为空提示 -->
        <view class="empty-address" v-if="isEmpty">
            <image src="/static/images/icons/empty-address.png" class="empty-icon"></image>
            <text class="empty-text">暂无收货地址</text>
        </view>

        <!-- 地址列表 -->
        <view class="address-list" v-else>
            <view class="address-item" @tap="selectAddress" :data-id="item.id" v-for="(item, index) in addressList" :key="index">
                <!-- 地址信息 -->

                <view class="address-info">
                    <view class="name-phone">
                        <text class="name">{{ item.name }}</text>
                        <text class="phone">{{ item.phone }}</text>
                        <text class="default-tag" v-if="item.isDefault">默认</text>
                    </view>
                    <view class="address-detail">{{ item.province }}{{ item.city }}{{ item.district }}{{ item.address }}</view>
                </view>

                <!-- 地址操作 -->

                <view class="address-actions">
                    <view class="action-btn" @tap.stop.prevent="setDefault" :data-id="item.id" v-if="!item.isDefault">
                        <text class="btn-text">设为默认</text>
                    </view>
                    <view class="action-btn" @tap.stop.prevent="editAddress" :data-id="item.id">
                        <image src="/static/images/icons/edit.png" class="action-icon"></image>
                        <text class="btn-text">编辑</text>
                    </view>
                    <view class="action-btn" @tap.stop.prevent="deleteAddress" :data-id="item.id">
                        <image src="/static/images/icons/delete.png" class="action-icon"></image>
                        <text class="btn-text">删除</text>
                    </view>
                </view>
            </view>
        </view>

        <!-- 底部按钮 -->
        <view class="footer">
            <button class="add-address-btn" @tap="addAddress">+ 新增收货地址</button>
        </view>
    </view>
</template>

<script>
// pages/address/list.js
const app = getApp();
const addressApi = require('../../api/address');
export default {
    data() {
        return {
            addressList: [],
            isEmpty: true
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        this.getAddressList();
    },
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {
        this.getAddressList();
    },
    methods: {
        // 获取地址列表
        getAddressList: function () {
            addressApi
                .getAddressList()
                .then((data) => {
                    this.setData({
                        addressList: data,
                        isEmpty: data.length === 0
                    });
                })
                .catch((err) => {
                    console.error('获取地址列表失败', err);
                });
        },

        // 选择地址
        selectAddress: function (e) {
            const { index } = e.currentTarget.dataset;
            const address = this.addressList[index];

            // 存储选中的地址
            uni.setStorageSync('selectedAddress', address);

            // 返回上一页
            uni.navigateBack();
        },

        // 编辑地址
        editAddress: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.navigateTo({
                url: `/pages/address/edit?id=${id}`
            });
        },

        // 删除地址
        deleteAddress: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.showModal({
                title: '提示',
                content: '确定要删除该地址吗？',
                success: (res) => {
                    if (res.confirm) {
                        addressApi
                            .deleteAddress(id)
                            .then(() => {
                                uni.showToast({
                                    title: '删除成功',
                                    icon: 'success'
                                });
                                this.getAddressList();
                            })
                            .catch((err) => {
                                console.error('删除失败', err);
                            });
                    }
                }
            });
        },

        // 添加新地址
        addAddress: function () {
            uni.navigateTo({
                url: '/pages/address/edit'
            });
        },

        setDefault(e) {
            const id = e.currentTarget.dataset.id;
            const address = this.addressList.find((item) => String(item.id) === String(id));
            if (!address) return;
            addressApi.updateAddress({ ...address, isDefault: 1 }).then(() => {
                uni.showToast({ title: '已设为默认', icon: 'success' });
                this.getAddressList();
            });
        }
    }
};
</script>
<style>
.address-container {
    min-height: 100vh;
    background-color: var(--bg-page);
    padding-bottom: calc(160rpx + env(safe-area-inset-bottom));
}

/* 空地址提示 */
.empty-address {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding-top: 160rpx;
}

.empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 30rpx;
}

.empty-text {
    color: var(--text-muted);
    font-size: 28rpx;
}

/* 地址列表 */
.address-list {
    padding: 24rpx;
}

.address-item {
    background: var(--bg-card-gradient);
    border: 1rpx solid var(--border-subtle);
    border-radius: 20rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 10rpx 28rpx rgba(0, 0, 0, 0.22);
}

/* 地址信息 */
.address-info {
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid var(--border-subtle);
    margin-bottom: 20rpx;
}

.name-phone {
    display: flex;
    align-items: center;
    margin-bottom: 16rpx;
}

.name {
    font-size: 32rpx;
    font-weight: 600;
    color: var(--text-primary);
    margin-right: 20rpx;
}

.phone {
    font-size: 28rpx;
    color: var(--text-muted);
}

.default-tag {
    font-size: 22rpx;
    color: var(--gold-light);
    background-color: rgba(232, 197, 71, 0.12);
    border: 1rpx solid rgba(232, 197, 71, 0.35);
    padding: 4rpx 14rpx;
    border-radius: 6rpx;
    margin-left: 20rpx;
}

.address-detail {
    font-size: 28rpx;
    line-height: 1.5;
    color: var(--text-regular);
}

/* 地址操作 */
.address-actions {
    display: flex;
    justify-content: flex-end;
}

.action-btn {
    display: flex;
    align-items: center;
    padding: 0 20rpx;
    height: 60rpx;
}

.action-icon {
    width: 32rpx;
    height: 32rpx;
    margin-right: 8rpx;
}

.btn-text {
    font-size: 26rpx;
    color: var(--text-muted);
}

/* 底部按钮 */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(16, 16, 18, 0.96);
    border-top: 1rpx solid var(--border-subtle);
    padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
}

.add-address-btn {
    background: var(--gold-gradient);
    color: #171717;
    font-size: 30rpx;
    font-weight: 600;
    border: none;
    border-radius: 44rpx;
    box-shadow: 0 10rpx 26rpx rgba(232, 197, 71, 0.22);
}

.add-address-btn::after {
    border: none;
}
</style>
