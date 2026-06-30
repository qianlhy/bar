<template>
    <view class="records-page">
        <scroll-view scroll-y class="records-scroll">
            <view class="record-item" v-for="(item, index) in records" :key="index">
                <view class="record-left">
                    <text class="record-name">{{ item.productName }}</text>
                    <text class="record-time">{{ item.createTime }}</text>
                </view>
                <text class="record-coin">-{{ item.coinPrice }} 币</text>
            </view>
            <view class="empty" v-if="!isLoading && records.length === 0">
                <text>暂无兑换记录</text>
            </view>
        </scroll-view>
    </view>
</template>

<script>
const coinApi = require('../../api/coin');
export default {
    data() {
        return {
            records: [],
            isLoading: true
        };
    },
    onShow() {
        this.loadRecords();
    },
    methods: {
        loadRecords() {
            const token = uni.getStorageSync('token');
            if (!token) {
                this.isLoading = false;
                uni.showToast({ title: '请先登录', icon: 'none' });
                return;
            }
            this.isLoading = true;
            coinApi.getExchangeRecords().then((list) => {
                this.records = list || [];
                this.isLoading = false;
            }).catch(() => { this.isLoading = false; });
        }
    }
};
</script>

<style>
.records-page {
    min-height: 100vh;
    background: #0a0a0a;
}
.records-scroll { height: 100vh; }
.record-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin: 20rpx 30rpx;
    padding: 28rpx 24rpx;
    background: #1c1c1e;
    border-radius: 16rpx;
}
.record-left { display: flex; flex-direction: column; }
.record-name { font-size: 28rpx; color: #fff; }
.record-time { font-size: 22rpx; color: #888; margin-top: 8rpx; }
.record-coin { font-size: 30rpx; color: #e8c547; font-weight: bold; }
.empty { text-align: center; color: #666; padding: 80rpx 0; font-size: 26rpx; }
</style>
