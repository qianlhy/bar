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
        <scroll-view :scroll-y="true" class="order-scroll" :enable-back-to-top="true" @scrolltolower="loadMoreOrders">
            <!-- 加载中 -->
            <view class="loading-container" v-if="isLoading">
                <view class="loading">
                    <image class="loading-icon" src="/static/images/icons/loading.png" mode="aspectFit"></image>
                    <text>加载中...</text>
                </view>
            </view>

            <!-- 空状态 -->
            <view class="empty-container" v-if="!isLoading && isEmpty">
                <image class="empty-icon" src="/static/images/icons/empty.png" mode="aspectFit"></image>
                <text>暂无订单</text>
            </view>

            <!-- 订单项 -->
            <view class="order-item" v-for="(item, index) in orderList" :key="index">
                <view class="order-header" @tap="goToOrderDetail" :data-id="item.id">
                    <view class="order-number">订单号：{{ item.id }}</view>
                    <view :class="'order-status ' + (item.statusText === '已取消' ? 'cancelled' : '')">{{ item.statusText }}</view>
                </view>

                <view class="order-goods" @tap="goToOrderDetail" :data-id="item.id">
                    <view class="goods-item" v-for="(goods, index1) in item.items" :key="index1">
                        <image class="goods-image" :src="goods.productImage" mode="aspectFill"></image>

                        <view class="goods-info">
                            <view class="goods-name text-ellipsis">{{ goods.productName }}</view>
                            <view class="goods-price">¥{{ goods.price }} x {{ goods.count }}</view>
                        </view>
                    </view>
                </view>

                <view class="order-footer">
                    <view class="order-total">
                        <text>共{{ item.items.length }}件商品</text>
                        <text>合计：¥{{ item.actualPayment }}</text>
                    </view>

                    <view class="order-actions">
                        <!-- 待付款 -->
                        <block v-if="item.statusText === '待付款'">
                            <view class="action-btn outline" @tap="cancelOrder" :data-id="item.id">取消订单</view>
                            <view class="action-btn primary" @tap="goToPay" :data-id="item.id" :data-amount="item.actualPayment">去支付</view>
                        </block>

                        <!-- 待发货 -->
                        <block v-else-if="item.statusText === '待发货'">
                            <view class="action-btn outline" @tap="checkLogistics">查看物流</view>
                        </block>

                        <!-- 待收货 -->
                        <block v-else-if="item.statusText === '待收货'">
                            <view class="action-btn outline" @tap="checkLogistics">查看物流</view>
                            <view class="action-btn primary" @tap="confirmReceipt" :data-id="item.id">确认收货</view>
                        </block>

                        <!-- 已完成 -->
                        <block v-else-if="item.statusText === '已完成'">
                            <view class="action-btn outline" @tap="checkLogistics">查看物流</view>
                        </block>

                        <!-- 已取消 -->
                        <block v-else-if="item.statusText === '已取消'">
                            <view class="action-btn outline" @tap="goToOrderDetail" :data-id="item.id">订单详情</view>
                        </block>
                    </view>
                </view>
            </view>
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
                    name: '待发货'
                },
                {
                    id: 3,
                    name: '待收货'
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
                待发货: 2,
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
                    this.setData({
                        orderList: data,
                        isLoading: false,
                        isEmpty: data.length === 0
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

        // 查看物流
        checkLogistics: function () {
            uni.showToast({
                title: '物流查询功能开发中',
                icon: 'none'
            });
        },

        loadMoreOrders() {
            console.log('占位：函数 loadMoreOrders 未声明');
        }
    }
};
</script>
<style>
/* pages/order/list.wxss */
.order-list-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f5f5f5;
}

/* 标签栏样式 */
.tabs {
    display: flex;
    background-color: #fff;
    border-bottom: 1rpx solid #eee;
    position: sticky;
    top: 0;
    z-index: 10;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    font-size: 28rpx;
    position: relative;
    color: #666;
}

.tab-item.active {
    color: var(--primary-color, #ff6b81);
    font-weight: bold;
}

.tab-line {
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 40rpx;
    height: 4rpx;
    background-color: var(--primary-color, #ff6b81);
    border-radius: 2rpx;
}

/* 订单列表样式 */
.order-scroll {
    flex: 1;
    height: 0;
}

/* 订单项样式 */
.order-item {
    margin-bottom: 20rpx;
    background-color: #fff;
    border-radius: 12rpx;
    overflow: hidden;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20rpx 30rpx;
    border-bottom: 1rpx solid #f5f5f5;
}

.order-number {
    font-size: 26rpx;
    color: #666;
}

.order-status {
    font-size: 26rpx;
    color: var(--primary-color, #ff6b81);
    font-weight: bold;
}

.order-status.cancelled {
    color: #999;
}

.order-goods {
    padding: 20rpx 30rpx;
}

.goods-item {
    display: flex;
    margin-bottom: 20rpx;
}

.goods-item:last-child {
    margin-bottom: 0;
}

.goods-image {
    width: 120rpx;
    height: 120rpx;
    border-radius: 8rpx;
    background-color: #f9f9f9;
}

.goods-info {
    flex: 1;
    margin-left: 20rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.goods-name {
    font-size: 28rpx;
    line-height: 1.4;
}

.text-ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.goods-price {
    font-size: 26rpx;
    color: #666;
}

.order-footer {
    padding: 20rpx 30rpx;
    border-top: 1rpx solid #f5f5f5;
}

.order-total {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20rpx;
    font-size: 26rpx;
    color: #666;
}

.order-actions {
    display: flex;
    justify-content: flex-end;
}

.action-btn {
    padding: 10rpx 30rpx;
    font-size: 26rpx;
    border-radius: 30rpx;
    margin-left: 20rpx;
}

.action-btn.outline {
    color: #666;
    border: 1rpx solid #ddd;
    background-color: #fff;
}

.action-btn.primary {
    color: #fff;
    background-color: var(--primary-color, #ff6b81);
    border: 1rpx solid var(--primary-color, #ff6b81);
}

/* 加载中样式 */
.loading-container {
    padding: 30rpx;
    display: flex;
    justify-content: center;
}

.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.loading-icon {
    width: 60rpx;
    height: 60rpx;
    margin-bottom: 10rpx;
}

.loading text {
    font-size: 24rpx;
    color: #999;
}

/* 空状态样式 */
.empty-container {
    padding: 100rpx 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    color: #999;
}

.empty-icon {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: 20rpx;
}
</style>
