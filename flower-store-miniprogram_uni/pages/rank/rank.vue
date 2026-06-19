<template>
    <view class="rank-page">
        <scroll-view scroll-y class="rank-scroll">
            <view class="rank-banner">
                <text class="banner-title">排行榜</text>
                <text class="banner-sub">SCORE RANK · 27POKER BAR</text>
            </view>

            <view class="tab-bar">
                <view
                    v-for="(tab, index) in tabs"
                    :key="index"
                    :class="'tab-item ' + (currentTab === tab.value ? 'active' : '')"
                    @tap="switchTab"
                    :data-value="tab.value"
                >{{ tab.label }}</view>
            </view>

            <view class="podium" v-if="topThree.length > 0">
                <view class="podium-item second" v-if="topThree[1]">
                    <image class="podium-avatar" :src="topThree[1].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="podium-rank rank2">2</view>
                    <text class="podium-name">{{ topThree[1].nickname }}</text>
                    <text class="podium-score">{{ topThree[1].masterScore }}</text>
                </view>
                <view class="podium-item first" v-if="topThree[0]">
                    <text class="crown">👑</text>
                    <image class="podium-avatar" :src="topThree[0].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="podium-rank rank1">1</view>
                    <text class="podium-name">{{ topThree[0].nickname }}</text>
                    <text class="podium-score">{{ topThree[0].masterScore }}</text>
                </view>
                <view class="podium-item third" v-if="topThree[2]">
                    <image class="podium-avatar" :src="topThree[2].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="podium-rank rank3">3</view>
                    <text class="podium-name">{{ topThree[2].nickname }}</text>
                    <text class="podium-score">{{ topThree[2].masterScore }}</text>
                </view>
            </view>

            <view class="rank-list">
                <view class="list-head">
                    <text class="col-rank">排名</text>
                    <text class="col-avatar">头像</text>
                    <text class="col-name">昵称</text>
                    <text class="col-score">大师分</text>
                </view>
                <view class="list-row" v-for="(item, index) in restList" :key="index">
                    <text class="col-rank">{{ index + 4 }}</text>
                    <view class="col-avatar">
                        <image class="row-avatar" :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    </view>
                    <text class="col-name">{{ item.nickname }}</text>
                    <text class="col-score">{{ item.masterScore }}</text>
                </view>
                <view class="empty" v-if="!isLoading && rankList.length === 0">
                    <text>暂无排行数据</text>
                </view>
            </view>
        </scroll-view>
    </view>
</template>

<script>
const rankApi = require('../../api/rank');
export default {
    data() {
        return {
            tabs: [
                { label: '本月榜', value: 'month' },
                { label: '上月榜', value: 'lastMonth' },
                { label: '本季度榜', value: 'quarter' },
                { label: '上季度榜', value: 'lastQuarter' }
            ],
            currentTab: 'month',
            rankList: [],
            isLoading: true
        };
    },
    computed: {
        topThree() {
            return this.rankList.slice(0, 3);
        },
        restList() {
            return this.rankList.slice(3);
        }
    },
    onLoad() {
        this.loadRank();
    },
    methods: {
        loadRank() {
            this.isLoading = true;
            rankApi.getRankList(this.currentTab).then((list) => {
                this.rankList = list || [];
                this.isLoading = false;
            }).catch(() => {
                this.isLoading = false;
            });
        },
        switchTab(e) {
            const value = e.currentTarget.dataset.value;
            if (value === this.currentTab) return;
            this.currentTab = value;
            this.loadRank();
        }
    }
};
</script>

<style>
.rank-page {
    height: 100vh;
    background: #0a0a0a;
    background-image: radial-gradient(ellipse at 50% 0%, #1f1f1f 0%, #0a0a0a 60%);
}
.rank-scroll { height: 100vh; padding-bottom: 40rpx; }
.rank-banner { text-align: center; padding: 40rpx 0 20rpx; }
.banner-title {
    font-size: 56rpx;
    font-weight: 900;
    color: #fff;
    letter-spacing: 6rpx;
    display: block;
    text-shadow: 0 4rpx 12rpx rgba(196,30,58,0.5);
}
.banner-sub { font-size: 22rpx; color: #888; margin-top: 8rpx; }
.tab-bar {
    display: flex;
    justify-content: center;
    gap: 12rpx;
    padding: 20rpx 30rpx;
}
.tab-item {
    padding: 12rpx 24rpx;
    font-size: 24rpx;
    color: #999;
    border-radius: 30rpx;
    background: #1c1c1e;
}
.tab-item.active {
    background: #c41e3a;
    color: #fff;
    font-weight: bold;
}
.podium {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    gap: 24rpx;
    padding: 40rpx 30rpx 30rpx;
}
.podium-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
}
.podium-item.first { margin-bottom: 30rpx; }
.crown { font-size: 40rpx; margin-bottom: 6rpx; }
.podium-avatar {
    width: 110rpx;
    height: 110rpx;
    border-radius: 50%;
    border: 4rpx solid #c41e3a;
    background: #333;
}
.podium-item.second .podium-avatar { border-color: #c0c0c0; }
.podium-item.third .podium-avatar { border-color: #cd7f32; }
.podium-rank {
    width: 44rpx;
    height: 44rpx;
    border-radius: 50%;
    color: #fff;
    font-size: 26rpx;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: -22rpx;
    border: 3rpx solid #0a0a0a;
}
.rank1 { background: #e8c547; color: #000; }
.rank2 { background: #c0c0c0; color: #000; }
.rank3 { background: #cd7f32; }
.podium-name {
    font-size: 24rpx;
    color: #fff;
    margin-top: 10rpx;
    max-width: 160rpx;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.podium-score { font-size: 28rpx; color: #e8c547; font-weight: bold; margin-top: 4rpx; }
.rank-list {
    margin: 10rpx 30rpx;
    background: #141416;
    border-radius: 16rpx;
    overflow: hidden;
}
.list-head {
    display: flex;
    align-items: center;
    padding: 24rpx 20rpx;
    background: #1c1c1e;
}
.list-head text { font-size: 24rpx; color: #888; }
.list-row {
    display: flex;
    align-items: center;
    padding: 20rpx;
    border-bottom: 1rpx solid #1f1f1f;
}
.col-rank { width: 80rpx; text-align: center; font-size: 26rpx; color: #ccc; }
.col-avatar { width: 100rpx; display: flex; justify-content: center; }
.col-name { flex: 1; font-size: 26rpx; color: #fff; padding-left: 10rpx; }
.col-score { width: 120rpx; text-align: center; font-size: 26rpx; color: #e8c547; font-weight: bold; }
.row-avatar { width: 56rpx; height: 56rpx; border-radius: 50%; background: #333; }
.empty { text-align: center; padding: 80rpx 0; color: #666; font-size: 26rpx; }
</style>
