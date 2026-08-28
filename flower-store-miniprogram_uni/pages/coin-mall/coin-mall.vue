<template>
    <view class="coin-page poker-bg">
        <view class="coin-header mesh-card-gold">
            <view class="coin-balance">
                <text class="balance-label font-art">我的 All In 币</text>
                <text class="balance-value font-art">{{ coins }}</text>
            </view>
            <view class="coin-records font-art" @tap="goRecords">兑换记录 ›</view>
        </view>

        <view class="coin-tip font-art">All In 币可用于兑换酒水、小食及专属周边</view>

        <scroll-view scroll-y class="coin-scroll">
            <view class="goods-grid">
                <view class="goods-item" v-for="(item, index) in goods" :key="index">
                    <image class="goods-img" :src="item.image" mode="aspectFill"></image>
                    <view class="goods-info">
                        <text class="goods-name">{{ item.name }}</text>
                        <text class="goods-stock">库存 {{ item.stock }}</text>
                        <view class="goods-bottom">
                            <text class="goods-coin">{{ item.coinPrice }}<text class="coin-unit">币</text></text>
                            <view
                                :class="'exchange-btn ' + (coins < item.coinPrice || item.stock <= 0 ? 'disabled' : '')"
                                @tap="onExchange"
                                :data-index="index"
                            >兑换</view>
                        </view>
                    </view>
                </view>
            </view>
            <view class="empty" v-if="!isLoading && goods.length === 0">
                <text>暂无可兑换商品</text>
            </view>
        </scroll-view>
    </view>
</template>

<script>
const coinApi = require('../../api/coin');
const userApi = require('../../api/user');
export default {
    data() {
        return {
            coins: 0,
            goods: [],
            isLoading: true
        };
    },
    onShow() {
        this.loadUser();
        this.loadGoods();
    },
    methods: {
        loadUser() {
            const token = uni.getStorageSync('token');
            if (!token) { this.coins = 0; return; }
            userApi.getUserInfo().then((data) => {
                this.coins = data.coins || 0;
            }).catch(() => {});
        },
        loadGoods() {
            this.isLoading = true;
            coinApi.getCoinProducts().then((list) => {
                this.goods = list || [];
                this.isLoading = false;
            }).catch(() => { this.isLoading = false; });
        },
        goRecords() {
            uni.navigateTo({ url: '/pages/coin-records/coin-records' });
        },
        onExchange(e) {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({ title: '请先登录', icon: 'none' });
                setTimeout(() => uni.navigateTo({ url: '/pages/login/login' }), 1500);
                return;
            }
            const item = this.goods[e.currentTarget.dataset.index];
            if (!item) return;
            if (item.stock <= 0) {
                uni.showToast({ title: '库存不足', icon: 'none' });
                return;
            }
            if (this.coins < item.coinPrice) {
                uni.showToast({ title: 'All In 币不足', icon: 'none' });
                return;
            }
            uni.showModal({
                title: '确认兑换',
                content: `确定花费 ${item.coinPrice} 个 All In 币兑换「${item.name}」吗？`,
                success: (res) => {
                    if (res.confirm) this.doExchange(item.id);
                }
            });
        },
        doExchange(productId) {
            coinApi.exchange(productId).then(() => {
                uni.showToast({ title: '兑换成功', icon: 'success' });
                this.loadUser();
                this.loadGoods();
            }).catch(() => {});
        }
    }
};
</script>

<style>
.coin-page { min-height: 100vh; }
.coin-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 40rpx 30rpx;
    background: linear-gradient(160deg, #1e1e20, #141416);
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.08);
}
.coin-balance { display: flex; flex-direction: column; }
.balance-label { font-size: 24rpx; color: #999; }
.balance-value { font-size: 72rpx; font-weight: 900; margin-top: 6rpx; color: #d4bc82; }
.coin-records { font-size: 24rpx; color: #aaa; }
.coin-tip { font-size: 22rpx; color: #777; padding: 20rpx 30rpx; }
.coin-scroll { height: calc(100vh - 220rpx); }
.goods-grid {
    display: flex;
    flex-wrap: wrap;
    padding: 0 20rpx;
    gap: 20rpx;
}
.goods-item {
    width: calc(50% - 10rpx);
    background: linear-gradient(160deg, #1f1a22, #141218);
    border-radius: 16rpx;
    overflow: hidden;
    margin-bottom: 20rpx;
    border: 1rpx solid rgba(255, 255, 255, 0.06);
    box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.35);
}
.goods-img { width: 100%; height: 280rpx; background: #222; }
.goods-info { padding: 16rpx 20rpx 20rpx; }
.goods-name {
    font-size: 28rpx;
    color: #fff;
    font-weight: bold;
    display: block;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.goods-stock { font-size: 22rpx; color: #777; display: block; margin: 8rpx 0; }
.goods-bottom {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 10rpx;
}
.goods-coin { font-size: 36rpx; color: #f7dc8a; font-weight: bold; }
.coin-unit { font-size: 22rpx; color: #c9a35a; margin-left: 4rpx; }
.exchange-btn {
    background: linear-gradient(135deg, #b85c7a, #8f3d52);
    color: #f5f0f2;
    font-size: 24rpx;
    font-weight: bold;
    padding: 10rpx 28rpx;
    border-radius: 30rpx;
    box-shadow: 0 4rpx 12rpx rgba(255, 45, 106, 0.35);
}
.exchange-btn.disabled { background: #444; color: #888; box-shadow: none; }
.empty { text-align: center; padding: 100rpx 0; color: #666; font-size: 26rpx; }
</style>
