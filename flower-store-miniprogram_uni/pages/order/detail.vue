<template>
    <!-- pages/order/detail.wxml -->
    <view class="order-detail-container">
        <!-- 加载中 -->
        <!-- 收货地址 -->
        <view class="address-section">
            <view class="section-title">收货地址</view>
            <view class="address-content">
                <view class="name-phone">
                    <text class="name">{{ orderInfo.order.receiverName }}</text>
                    <text class="phone">{{ orderInfo.order.receiverPhone }}</text>
                </view>
                <view class="address-detail">{{ orderInfo.order.province }}{{ orderInfo.order.city }}{{ orderInfo.order.district }}{{ orderInfo.order.address }}</view>
            </view>
        </view>

        <!-- 商品信息 -->
        <view class="goods-section">
            <view class="section-title">商品信息</view>
            <view class="goods-list">
                <view class="goods-item" v-for="(item, index) in orderInfo.items" :key="index">
                    <image class="goods-image" :src="item.productImage" mode="aspectFill"></image>

                    <view class="goods-info">
                        <view class="goods-name">{{ item.productName }}</view>
                        <view class="goods-price-count">
                            <text class="goods-price">¥{{ item.price }}</text>
                            <text class="goods-count">x{{ item.count }}</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 订单信息 -->
        <view class="order-info-section">
            <view class="section-title">订单信息</view>
            <view class="order-info-item">
                <text class="label">订单编号</text>
                <view class="value-copy">
                    <text class="value">{{ orderId }}</text>
                    <view class="copy-btn" @tap="copyOrderId">复制</view>
                </view>
            </view>
            <view class="order-info-item">
                <text class="label">创建时间</text>
                <text class="value">{{ orderInfo.order.createTime }}</text>
            </view>
            <view class="order-info-item">
                <text class="label">支付方式</text>
                <text class="value">{{ orderInfo.order.paymentMethod === 'online' ? '在线支付' : '货到付款' }}</text>
            </view>
            <view class="order-info-item" v-if="orderInfo.order.remark">
                <text class="label">订单备注</text>
                <text class="value">{{ orderInfo.order.remark }}</text>
            </view>
        </view>

        <!-- 金额信息 -->
        <view class="amount-section">
            <view class="amount-item">
                <text class="label">商品总额</text>
                <text class="value">¥{{ orderInfo.order.totalPrice }}</text>
            </view>
            <view class="amount-item">
                <text class="label">运费</text>
                <text class="value">¥{{ orderInfo.order.freight }}</text>
            </view>
            <view class="amount-item total">
                <text class="label">实付款</text>
                <text class="value">¥{{ orderInfo.order.actualPayment }}</text>
            </view>
        </view>

        <!-- 底部操作按钮 -->
        <view class="footer">
            <!-- 待付款 -->
            <block v-if="orderInfo.status === '待付款'">
                <button class="btn cancel" @tap="cancelOrder">取消订单</button>
                <button class="btn primary" @tap="goToPay">去支付</button>
            </block>

            <!-- 待发货 -->
            <block v-else-if="orderInfo.status === '待发货'">
                <button class="btn" @tap="contactService">联系客服</button>
            </block>

            <!-- 待收货 -->
            <block v-else-if="orderInfo.status === '待收货'">
                <button class="btn" @tap="checkLogistics">查看物流</button>
                <button class="btn primary" @tap="confirmReceipt">确认收货</button>
            </block>

            <!-- 已完成 -->
            <block v-else-if="orderInfo.status === '已完成'">
                <button class="btn" @tap="contactService">联系客服</button>
            </block>
        </view>
    </view>
</template>

<script>
const orderApi = require('../../api/order');
export default {
    data() {
        return {
            orderId: '',
            orderInfo: null,
            isLoading: true,
            receiverName: '',
            receiverPhone: '',
            province: '',
            city: '',
            district: '',
            address: '',
            createTime: '',
            paymentMethod: '',
            remark: '',
            totalPrice: '',
            freight: '',
            actualPayment: ''
        };
    },
    onLoad: function (options) {
        const { id } = options;
        if (id) {
            this.setData({
                orderId: id
            });
            this.getOrderDetail(id);
        } else {
            uni.showToast({
                title: '订单ID不存在',
                icon: 'none'
            });
            setTimeout(() => {
                uni.navigateBack();
            }, 1500);
        }
    },
    methods: {
        // 获取订单详情
        getOrderDetail: function (orderId) {
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
            orderApi
                .getOrderDetail(orderId)
                .then((orderInfo) => {
                    this.setData({
                        orderInfo,
                        isLoading: false
                    });
                })
                .catch((err) => {
                    console.error('获取订单详情失败', err);
                    this.setData({
                        isLoading: false
                    });
                    uni.showToast({
                        title: '订单不存在',
                        icon: 'none'
                    });
                    setTimeout(() => {
                        uni.navigateBack();
                    }, 1500);
                });
        },

        // 复制订单号
        copyOrderId: function () {
            uni.setClipboardData({
                data: this.orderInfo.orderNo,
                success: () => {
                    uni.showToast({
                        title: '订单号已复制',
                        icon: 'success'
                    });
                }
            });
        },

        // 取消订单
        cancelOrder: function () {
            const { orderId } = this;
            uni.showModal({
                title: '取消订单',
                content: '确定要取消该订单吗？',
                success: (res) => {
                    if (res.confirm) {
                        orderApi
                            .cancelOrder(orderId)
                            .then(() => {
                                uni.showToast({
                                    title: '订单已取消',
                                    icon: 'success'
                                });
                                // 刷新订单详情
                                this.getOrderDetail(orderId);
                            })
                            .catch((err) => {
                                console.error('取消订单失败', err);
                            });
                    }
                }
            });
        },

        // 去支付
        goToPay: function () {
            const { orderId, orderInfo } = this;
            uni.navigateTo({
                url: `/pages/order/payment?id=${orderId}&amount=${orderInfo.actualPayment}`
            });
        },

        // 确认收货
        confirmReceipt: function () {
            const { orderId } = this;
            uni.showModal({
                title: '确认收货',
                content: '确认已收到商品？',
                success: (res) => {
                    if (res.confirm) {
                        orderApi
                            .updateOrderStatus(orderId, 4)
                            .then(() => {
                                uni.showToast({
                                    title: '确认收货成功',
                                    icon: 'success'
                                });
                                // 刷新订单详情
                                this.getOrderDetail(orderId);
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

        // 联系客服
        contactService: function () {
            uni.showToast({
                title: '客服功能开发中',
                icon: 'none'
            });
        }
    }
};
</script>
<style>
/* pages/order/detail.wxss */
.order-detail-container {
    min-height: 100vh;
    background-color: #f5f5f5;
    padding-bottom: 120rpx;
}

/* 加载中 */
.loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.loading {
    width: 80rpx;
    height: 80rpx;
    border: 6rpx solid #f3f3f3;
    border-top: 6rpx solid #ff6b81;
    border-radius: 50%;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

/* 通用样式 */
.section-title {
    font-size: 30rpx;
    color: #333;
    font-weight: 500;
    padding-bottom: 20rpx;
}

/* 订单状态 */
.status-section {
    background-color: #ff6b81;
    color: #fff;
    padding: 40rpx 30rpx;
}

.status {
    font-size: 36rpx;
    font-weight: bold;
    margin-bottom: 10rpx;
}

.status-desc {
    font-size: 28rpx;
    opacity: 0.8;
}

/* 收货地址 */
.address-section {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 30rpx;
}

.address-content {
    padding-top: 10rpx;
}

.name-phone {
    margin-bottom: 10rpx;
}

.name {
    font-size: 30rpx;
    font-weight: 500;
    margin-right: 20rpx;
}

.phone {
    font-size: 28rpx;
    color: #666;
}

.address-detail {
    font-size: 28rpx;
    color: #333;
    line-height: 1.5;
}

/* 商品信息 */
.goods-section {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 30rpx;
}

.goods-list {
    padding-top: 10rpx;
}

.goods-item {
    display: flex;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #eee;
}

.goods-item:last-child {
    border-bottom: none;
}

.goods-image {
    width: 160rpx;
    height: 160rpx;
    border-radius: 8rpx;
    margin-right: 20rpx;
    background-color: #f9f9f9;
}

.goods-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.goods-name {
    font-size: 28rpx;
    color: #333;
    line-height: 1.4;
    margin-bottom: 20rpx;
}

.goods-price-count {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.goods-price {
    font-size: 30rpx;
    color: #ff6b81;
    font-weight: 500;
}

.goods-count {
    font-size: 28rpx;
    color: #999;
}

/* 订单信息 */
.order-info-section {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 30rpx;
}

.order-info-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15rpx 0;
    font-size: 28rpx;
}

.label {
    color: #666;
}

.value {
    color: #333;
}

.value-copy {
    display: flex;
    align-items: center;
}

.copy-btn {
    margin-left: 20rpx;
    color: #ff6b81;
    font-size: 26rpx;
}

/* 金额信息 */
.amount-section {
    background-color: #fff;
    margin-top: 20rpx;
    padding: 30rpx;
}

.amount-item {
    display: flex;
    justify-content: space-between;
    padding: 10rpx 0;
    font-size: 28rpx;
}

.amount-item.total {
    padding-top: 20rpx;
    margin-top: 10rpx;
    border-top: 1rpx solid #eee;
}

.amount-item.total .label,
.amount-item.total .value {
    font-size: 32rpx;
    font-weight: bold;
    color: #ff6b81;
}

/* 底部按钮 */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: #fff;
    padding: 20rpx 30rpx;
    display: flex;
    justify-content: flex-end;
    box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.btn {
    height: 80rpx;
    line-height: 80rpx;
    padding: 0 40rpx;
    margin-left: 20rpx;
    font-size: 28rpx;
    border-radius: 40rpx;
    background-color: #f5f5f5;
    color: #666;
}

.btn.primary {
    background-color: #ff6b81;
    color: #fff;
}

.btn.cancel {
    background-color: #fff;
    border: 1rpx solid #ddd;
}
</style>
