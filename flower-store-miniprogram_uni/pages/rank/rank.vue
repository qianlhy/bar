<template>
    <view class="rank-page poker-bg">
        <scroll-view scroll-y class="rank-scroll">
            <view class="rank-hero">
                <text class="poker-deco left">♠</text>
                <text class="poker-deco right">♥</text>
                <view class="hero-title-row">
                    <text class="banner-title font-art">排行榜</text>
                </view>
                <text class="banner-sub font-art">SCORE RANK</text>
            </view>

            <view class="tab-bar">
                <view
                    v-for="(tab, index) in tabs"
                    :key="index"
                    :class="'tab-item font-art ' + (currentTab === tab.value ? 'active' : '')"
                    @tap="switchTab"
                    :data-value="tab.value"
                >{{ tab.label }}</view>
            </view>

            <view class="podium-stage" v-if="topThree.length > 0">
                <view class="podium-item second" v-if="topThree[1]">
                    <text class="crown-icon">♛</text>
                    <image class="podium-avatar" :src="topThree[1].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="pillar">
                        <view class="pillar-cap"></view>
                        <view class="pillar-body pillar-2">
                            <text class="pillar-num">2</text>
                        </view>
                        <view class="pillar-base"></view>
                    </view>
                    <text class="podium-name">{{ topThree[1].nickname }}</text>
                    <text class="podium-score font-art">{{ topThree[1].masterScore }}</text>
                </view>
                <view class="podium-item first" v-if="topThree[0]">
                    <text class="crown-icon gold">♛</text>
                    <image class="podium-avatar" :src="topThree[0].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="pillar">
                        <view class="pillar-cap gold-cap"></view>
                        <view class="pillar-body pillar-1">
                            <text class="pillar-num">1</text>
                        </view>
                        <view class="pillar-base"></view>
                    </view>
                    <text class="podium-name">{{ topThree[0].nickname }}</text>
                    <text class="podium-score font-art neon-gold">{{ topThree[0].masterScore }}</text>
                </view>
                <view class="podium-item third" v-if="topThree[2]">
                    <text class="crown-icon">♛</text>
                    <image class="podium-avatar" :src="topThree[2].avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    <view class="pillar">
                        <view class="pillar-cap bronze-cap"></view>
                        <view class="pillar-body pillar-3">
                            <text class="pillar-num">3</text>
                        </view>
                        <view class="pillar-base"></view>
                    </view>
                    <text class="podium-name">{{ topThree[2].nickname }}</text>
                    <text class="podium-score font-art">{{ topThree[2].masterScore }}</text>
                </view>
            </view>

            <view class="rank-list mesh-card">
                <view class="list-head">
                    <text class="col-rank font-art">排名</text>
                    <text class="col-avatar font-art">头像</text>
                    <text class="col-name font-art">昵称</text>
                    <text class="col-score font-art">大师分</text>
                </view>
                <view class="list-row" v-for="(item, index) in restList" :key="index">
                    <text class="col-rank">{{ index + 4 }}</text>
                    <view class="col-avatar">
                        <image class="row-avatar" :src="item.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
                    </view>
                    <text class="col-name">{{ item.nickname }}</text>
                    <text class="col-score font-art neon-gold">{{ item.masterScore }}</text>
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
.rank-page { height: 100vh; position: relative; }
.rank-scroll { height: 100vh; padding-bottom: 40rpx; }

.rank-hero { text-align: center; padding: 36rpx 30rpx 16rpx; position: relative; }
.poker-deco { position: absolute; font-size: 80rpx; opacity: 0.12; top: 30rpx; }
.poker-deco.left { left: 36rpx; color: #fff; transform: rotate(-12deg); }
.poker-deco.right { right: 36rpx; color: #a01c30; transform: rotate(10deg); }
.banner-title {
    font-size: 56rpx;
    font-weight: 900;
    color: #eee;
    letter-spacing: 8rpx;
    text-shadow: 0 4rpx 16rpx rgba(160, 28, 45, 0.4);
}
.banner-sub {
    font-size: 26rpx;
    color: #a01c30;
    margin-top: 8rpx;
    display: block;
    letter-spacing: 8rpx;
    transform: skewX(-6deg);
}

.tab-bar {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12rpx;
    padding: 16rpx 24rpx 24rpx;
}
.tab-item {
    padding: 14rpx 24rpx;
    font-size: 24rpx;
    color: #888;
    border-radius: 30rpx;
    background: rgba(255, 255, 255, 0.05);
    border: 1rpx solid rgba(255, 255, 255, 0.06);
}
.tab-item.active {
    background: linear-gradient(135deg, #a01c30, #6b1220);
    color: #f5ecee;
    font-weight: bold;
    box-shadow: 0 6rpx 20rpx rgba(100, 20, 30, 0.35);
    border-color: rgba(160, 28, 45, 0.3);
}

/* 立柱式领奖台 */
.podium-stage {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    gap: 16rpx;
    padding: 30rpx 20rpx 10rpx;
    perspective: 900rpx;
}
.podium-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    flex: 1;
    max-width: 220rpx;
}
.crown-icon { font-size: 28rpx; color: #888; margin-bottom: 6rpx; }
.crown-icon.gold { color: #c9a86a; font-size: 36rpx; }
.podium-avatar {
    width: 96rpx;
    height: 96rpx;
    border-radius: 50%;
    border: 3rpx solid rgba(255, 255, 255, 0.2);
    background: #222;
    position: relative;
    z-index: 3;
    margin-bottom: -6rpx;
}
.podium-item.first .podium-avatar {
    width: 116rpx;
    height: 116rpx;
    border-color: #a01c30;
    box-shadow: 0 0 24rpx rgba(160, 28, 45, 0.35);
}

.pillar { width: 100%; position: relative; transform-style: preserve-3d; }
.pillar-cap {
    width: 88%;
    height: 16rpx;
    margin: 0 auto;
    border-radius: 50%;
    background: linear-gradient(180deg, #888, #555);
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.4);
    position: relative;
    z-index: 2;
}
.gold-cap { background: linear-gradient(180deg, #c45a6a, #a01c30); }
.bronze-cap { background: linear-gradient(180deg, #a08050, #6a5030); }

.pillar-body {
    width: 78%;
    margin: -4rpx auto 0;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    border-left: 4rpx solid rgba(255, 255, 255, 0.08);
    border-right: 4rpx solid rgba(0, 0, 0, 0.3);
    box-shadow:
        inset 8rpx 0 16rpx rgba(255, 255, 255, 0.06),
        inset -8rpx 0 16rpx rgba(0, 0, 0, 0.2),
        0 16rpx 0 rgba(0, 0, 0, 0.35);
}
.pillar-1 { height: 160rpx; background: linear-gradient(90deg, #6b1220, #a01c30 40%, #8a1828 70%, #5a1018); }
.pillar-2 { height: 110rpx; background: linear-gradient(90deg, #4a4a4a, #7a7a7a 40%, #666 70%, #3a3a3a); }
.pillar-3 { height: 86rpx; background: linear-gradient(90deg, #5a4020, #8a6830 40%, #7a5828 70%, #4a3018); }

.pillar-num {
    font-size: 52rpx;
    font-weight: 900;
    color: rgba(255, 255, 255, 0.85);
    text-shadow: 0 4rpx 8rpx rgba(0, 0, 0, 0.5);
    font-family: 'DouyinSans', sans-serif;
}
.pillar-base {
    width: 95%;
    height: 12rpx;
    margin: 0 auto;
    border-radius: 50%;
    background: #111;
    box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.5);
}

.podium-name {
    font-size: 22rpx;
    color: #ccc;
    margin-top: 16rpx;
    max-width: 160rpx;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
}
.podium-score { font-size: 30rpx; color: #c9a86a; font-weight: bold; margin-top: 4rpx; }

.rank-list { margin: 24rpx 24rpx; overflow: hidden; padding: 0; }
.list-head {
    display: flex;
    align-items: center;
    padding: 24rpx 20rpx;
    background: rgba(160, 28, 45, 0.08);
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.06);
}
.list-head text { font-size: 24rpx; color: #888; }
.list-row {
    display: flex;
    align-items: center;
    padding: 20rpx;
    border-bottom: 1rpx solid rgba(255, 255, 255, 0.04);
}
.col-rank { width: 80rpx; text-align: center; font-size: 26rpx; color: #aaa; }
.col-avatar { width: 100rpx; display: flex; justify-content: center; }
.col-name { flex: 1; font-size: 26rpx; color: #ddd; padding-left: 10rpx; }
.col-score { width: 120rpx; text-align: center; font-size: 26rpx; font-weight: bold; }
.row-avatar { width: 56rpx; height: 56rpx; border-radius: 50%; background: #333; border: 2rpx solid rgba(160, 28, 45, 0.25); }
.empty { text-align: center; padding: 80rpx 0; color: #555; font-size: 26rpx; }
</style>
