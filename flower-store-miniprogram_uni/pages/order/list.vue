<template>
    <!-- pages/order/list.wxml -->
    <view class="order-list-container">
        <!-- 标签栏 -->
        <view class="tabs">
            <view :class="'tab-item ' + (activeTab === item.id ? 'active' : '')" :data-id="item.id" @tap="switchTab" v-for="(item, index) in tabs" :key="index">
                <text>{{ item.name }}</text>

                <view class="tab-line" v-if="activeTab === item.id"></view>
            </view>
        </view>

        <!-- 订单列表 -->
        <scroll-view :scroll-y="true" class="order-scroll" :enable-back-to-top="true">
            <!-- 加载中 -->
            <view class="loading-container" v-if="isLoading">
                <view class="loading-dot"></view>
                <text class="loading-text">加载中...</text>
            </view>

            <!-- 空状态 -->
            <view class="empty-state" v-if="!isLoading && isEmpty">
                <text class="empty-state-icon">暂无</text>
                <text class="empty-state-text">暂无订单</text>
                <text class="empty-state-tip">去点一杯，开启今晚的局</text>
            </view>

            <!-- 订单项 -->
            <view class="order-item" v-for="(item, index) in orderList" :key="index">
                <view class="order-header" @tap="goToOrderDetail" :data-id="item.id">
                    <view class="order-number">订单号 {{ item.orderNo || item.id }}</view>
                    <view :class="'order-status ' + statusClass(item.statusText)">{{ item.statusText }}</view>
                </view>

                <view class="order-goods" @tap="goToOrderDetail" :data-id="item.id">
                    <view class="goods-item" v-for="(goods, index1) in item.items" :key="index1">
                        <image class="goods-image" :src="goods.productImage || '/static/allIn.jpg'" mode="aspectFill"></image>

                        <view class="goods-info">
                            <view class="goods-name text-ellipsis">{{ goods.productName }}</view>
                            <view class="goods-price">¥{{ money(goods.price) }} × {{ goods.count }}</view>
                        </view>
                    </view>
                </view>

                <view class="order-footer">
                    <view class="order-total">
                        <text class="total-count">共 {{ item.items.length }} 件</text>
                        <view class="total-amount">
                            <text class="total-label">合计</text>
                            <text class="total-price">¥{{ money(item.actualPayment) }}</text>
                        </view>
                    </view>

                    <view class="order-actions">
                        <block v-if="item.statusText === '待付款'">
                            <view class="action-btn outline" @tap="cancelOrder" :data-id="item.id">取消订单</view>
                            <view class="action-btn primary" @tap="goToPay" :data-id="item.id" :data-amount="item.actualPayment">去支付</view>
                        </block>

                        <block v-else>
                            <view class="action-btn outline" @tap="goToOrderDetail" :data-id="item.id">订单详情</view>
                        </block>
                    </view>
                </view>
            </view>

            <view class="scroll-bottom-space"></view>
        </scroll-view>
    </view>
</template>

<script>
// pages/order/list.js
const orderApi = require('../../api/order');
export default {
    data() {
        return {
            activeTab: 0,

            tabs: [
                {
                    id: 0,
                    name: '全部'
                },
                {
                    id: 1,
                    name: '待付款'
                },
                {
                    id: 2,
                    name: '待出品'
                },
                {
                    id: 3,
                    name: '出品中'
                },
                {
                    id: 4,
                    name: '已完成'
                },
                {
                    id: 5,
                    name: '已取消'
                }
            ],

            statusMap: {
                0: null,
                // 全部
                1: 1,
                // 待付款
                2: 2,
                // 待发货
                3: 3,
                // 已发货（待收货）
                4: 4,
                // 已完成
                5: 5 // 已取消
            },

            orderList: [],
            isLoading: false,
            isEmpty: false,

            goods: {
                productImage: '',
                productName: '',
                price: '',
                count: ''
            }
        };
    },
    onLoad(options) {
        // 如果从外部传入了状态参数，则切换到对应标签
        if (options.status) {
            const statusMap = {
                待付款: 1,
                待出品: 2,
                待发货: 2,
                出品中: 3,
                待收货: 3,
                已完成: 4,
                已取消: 5
            };
            const statusIndex = statusMap[options.status] || 0;
            this.setData({
                activeTab: statusIndex
            });
        }

        // 加载订单列表
        this.loadOrderList();
    },
    onShow() {
        // 页面显示时重新加载订单列表
        this.loadOrderList();
    },
    // 下拉刷新
    onPullDownRefresh() {
        this.loadOrderList();
        uni.stopPullDownRefresh();
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {},
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {},
    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {},
    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {},
    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {},
    methods: {
        // 订单状态码转文案
        formatStatus: function (status) {
            const map = {
                1: '待付款',
                2: '待出品',
                3: '出品中',
                4: '已完成',
                5: '已取消'
            };
            return map[status] || '';
        },

        statusClass: function (statusText) {
            if (statusText === '已取消') return 'cancelled';
            if (statusText === '已完成') return 'done';
            return '';
        },

        money: function (val) {
            return Number(val || 0).toFixed(2);
        },

        // 加载订单列表
        loadOrderList: function () {
            const token = uni.getStorageSync('token');
            if (!token) {
                uni.showToast({
                    title: '请先登录',
                    icon: 'none'
                });
                setTimeout(() => {
                    uni.navigateTo({
                        url: '/pages/login/login'
                    });
                }, 1500);
                return;
            }
            this.setData({
                isLoading: true
            });
            const { activeTab, statusMap } = this;
            const status = statusMap[activeTab];
            orderApi
                .getOrderList(status)
                .then((data) => {
                    const list = (data || []).map((item) => ({
                        ...item,
                        statusText: this.formatStatus(item.status)
                    }));
                    this.setData({
                        orderList: list,
                        isLoading: false,
                        isEmpty: list.length === 0
                    });
                })
                .catch((err) => {
                    console.error('获取订单列表失败', err);
                    this.setData({
                        isLoading: false,
                        isEmpty: true
                    });
                });
        },

        // 切换标签
        switchTab: function (e) {
            const { id } = e.currentTarget.dataset;
            if (id !== this.activeTab) {
                this.setData(
                    {
                        activeTab: id
                    },
                    () => {
                        this.loadOrderList();
                    }
                );
            }
        },

        // 跳转到订单详情
        goToOrderDetail: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.navigateTo({
                url: `/pages/order/detail?id=${id}`
            });
        },

        // 取消订单
        cancelOrder: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.showModal({
                title: '取消订单',
                content: '确定要取消该订单吗？',
                success: (res) => {
                    if (res.confirm) {
                        orderApi
                            .cancelOrder(id)
                            .then(() => {
                                uni.showToast({
                                    title: '订单已取消',
                                    icon: 'success'
                                });
                                // 重新加载订单列表
                                this.loadOrderList();
                            })
                            .catch((err) => {
                                console.error('取消订单失败', err);
                            });
                    }
                }
            });
        },

        // 去支付
        goToPay: function (e) {
            const { id, amount } = e.currentTarget.dataset;
            uni.navigateTo({
                url: `/pages/order/payment?id=${id}&amount=${amount}`
            });
        },

        // 确认收货
        confirmReceipt: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.showModal({
                title: '确认收货',
                content: '确认已收到商品？',
                success: (res) => {
                    if (res.confirm) {
                        orderApi
                            .updateOrderStatus(id, 4)
                            .then(() => {
                                uni.showToast({
                                    title: '确认收货成功',
                                    icon: 'success'
                                });
                                // 重新加载订单列表
                                this.loadOrderList();
                            })
                            .catch((err) => {
                                console.error('确认收货失败', err);
                            });
                    }
                }
            });
        },

        // 查看物流（店内点单无物流）
        checkLogistics: function () {}
        }
};
</script>
<style>
.order-list-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background: var(--bg-page);
}

/* 标签栏样式 */
.tabs {
    display: flex;
    position: sticky;
    top: 0;
    z-index: 10;
    background: rgba(16, 16, 18, 0.98);
    border-bottom: 1rpx solid var(--border-subtle);
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 26rpx 0;
    font-size: 27rpx;
    position: relative;
    color: var(--text-muted);
}

.tab-item.active {
    color: var(--gold);
    font-weight: 600;
}

.tab-line {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 44rpx;
    height: 5rpx;
    background: linear-gradient(90deg, var(--gold-light), var(--gold-dark));
    border-radius: 3rpx;
}

/* 订单列表样式 */
.order-scroll {
    flex: 1;
    height: 0;
    padding: 24rpx;
    box-sizing: border-box;
}

.scroll-bottom-space {
    height: calc(40rpx + env(safe-area-inset-bottom));
}

/* 订单项样式 */
.order-item {
    margin-bottom: 24rpx;
    overflow: hidden;
    border: 1rpx solid var(--border-subtle);
    border-radius: 22rpx;
    background: var(--bg-card-gradient);
    box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.24);
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 24rpx 28rpx;
    border-bottom: 1rpx solid var(--border-subtle);
}

.order-number {
    font-size: 24rpx;
    color: var(--text-faint);
}

.order-status {
    font-size: 25rpx;
    color: var(--gold);
    font-weight: 600;
}

.order-status.cancelled,
.order-status.done {
    color: var(--text-muted);
}

.order-goods {
    padding: 24rpx 28rpx;
}

.goods-item {
    display: flex;
    align-items: center;
    margin-bottom: 22rpx;
}

.goods-item:last-child {
    margin-bottom: 0;
}

.goods-image {
    width: 120rpx;
    height: 120rpx;
    border-radius: 14rpx;
    background-color: var(--bg-sunken);
    flex-shrink: 0;
}

.goods-info {
    flex: 1;
    min-width: 0;
    margin-left: 22rpx;
    display: flex;
    flex-direction: column;
}

.goods-name {
    color: var(--text-primary);
    font-size: 28rpx;
    line-height: 1.4;
}

.text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.goods-price {
    margin-top: 12rpx;
    color: var(--text-muted);
    font-size: 25rpx;
}

.order-footer {
    padding: 22rpx 28rpx;
    border-top: 1rpx solid var(--border-subtle);
}

.order-total {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    margin-bottom: 22rpx;
}

.total-count {
    color: var(--text-faint);
    font-size: 24rpx;
}

.total-label {
    color: var(--text-muted);
    font-size: 24rpx;
    margin-right: 10rpx;
}

.total-price {
    color: var(--gold);
    font-size: 34rpx;
    font-weight: 700;
}

.order-actions {
    display: flex;
    justify-content: flex-end;
}

.action-btn {
    padding: 12rpx 34rpx;
    font-size: 26rpx;
    border-radius: 32rpx;
    margin-left: 20rpx;
}

.action-btn.outline {
    color: var(--text-regular);
    border: 1rpx solid var(--border-subtle);
    background-color: var(--bg-elevated);
}

.action-btn.primary {
    color: #171717;
    font-weight: 600;
    background: var(--gold-gradient);
    box-shadow: 0 8rpx 20rpx rgba(232, 197, 71, 0.2);
}

/* 加载中样式 */
.loading-container {
    padding: 60rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.loading-dot {
    width: 48rpx;
    height: 48rpx;
    border: 4rpx solid rgba(232, 197, 71, 0.2);
    border-top-color: var(--gold);
    border-radius: 50%;
    animation: spin 0.9s linear infinite;
}

@keyframes spin {
    to {
        transform: rotate(360deg);
    }
}

.loading-text {
    margin-top: 18rpx;
    color: var(--text-faint);
    font-size: 24rpx;
}
</style>
