<template>
    <view class="order-detail-container">
        <!-- 订单状态 -->
        <view :class="'status-section status-' + statusTone">
            <text class="status-text">{{ statusText || '订单详情' }}</text>
            <text class="status-desc">{{ statusDesc }}</text>
        </view>

        <!-- 就餐信息 -->
        <view class="card">
            <view class="section-head">就餐信息</view>
            <view class="store-row">
                <view class="store-copy">
                    <text class="store-name">梭哈酒馆 · 南京店</text>
                    <text class="store-address">南京市浦口区江浦街道</text>
                </view>
                <view class="instore-tag">{{ tableNoText }}</view>
            </view>
        </view>

        <!-- 商品信息 -->
        <view class="card">
            <view class="section-head">商品清单</view>
            <view class="goods-list">
                <view class="goods-item" v-for="(item, index) in goodsList" :key="index">
                    <image class="goods-image" :src="item.productImage || '/static/allIn.jpg'" mode="aspectFill"></image>

                    <view class="goods-info">
                        <view class="goods-name">{{ item.productName }}</view>
                        <view class="goods-spec" v-if="item.specText">{{ item.specText }}</view>
                        <view class="goods-price-count">
                            <text class="goods-price">¥{{ money(item.price) }}</text>
                            <text class="goods-count">x{{ item.count }}</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <!-- 订单信息 -->
        <view class="card">
            <view class="section-head">订单信息</view>
            <view class="order-info-item">
                <text class="label">订单编号</text>
                <view class="value-copy">
                    <text class="value">{{ order.orderNo || orderId }}</text>
                    <view class="copy-btn" @tap="copyOrderId">复制</view>
                </view>
            </view>
            <view class="order-info-item">
                <text class="label">下单时间</text>
                <text class="value">{{ order.createTime }}</text>
            </view>
            <view class="order-info-item">
                <text class="label">支付方式</text>
                <text class="value">微信支付</text>
            </view>
            <view class="order-info-item" v-if="order.remark">
                <text class="label">订单备注</text>
                <text class="value">{{ order.remark }}</text>
            </view>
        </view>

        <!-- 金额信息 -->
        <view class="card">
            <view class="amount-item">
                <text class="label">商品金额</text>
                <text class="value">¥{{ money(order.totalPrice) }}</text>
            </view>
            <view class="amount-item">
                <text class="label">服务费</text>
                <text class="value">¥0.00</text>
            </view>
            <view class="amount-item" v-if="order.pointsUsed > 0">
                <text class="label">积分抵扣</text>
                <text class="value discount">-¥{{ money(order.pointsAmount) }}（{{ order.pointsUsed }}分）</text>
            </view>
            <view class="divider"></view>
            <view class="amount-item total">
                <text class="label">实付款</text>
                <text class="value">¥{{ money(order.actualPayment) }}</text>
            </view>
        </view>

        <!-- 底部操作按钮 -->
        <view class="footer" v-if="statusText === '待付款'">
            <button class="btn" @tap="cancelOrder">取消订单</button>
            <button class="btn primary" @tap="goToPay">去支付</button>
        </view>
    </view>
</template>

<script>
const orderApi = require('../../api/order');
export default {
    data() {
        return {
            orderId: '',
            orderInfo: { order: {}, items: [] },
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
    computed: {
        order() {
            return (this.orderInfo && this.orderInfo.order) || {};
        },
        goodsList() {
            return (this.orderInfo && this.orderInfo.items) || [];
        },
        statusText() {
            const map = {
                1: '待付款',
                2: '待出品',
                3: '出品中',
                4: '已完成',
                5: '已取消'
            };
            return map[this.order.status] || '';
        },
        statusDesc() {
            const map = {
                1: '请在15分钟内完成支付，超时订单自动取消',
                2: '已支付成功，吧台正在为您安排',
                3: '酒品正在调制，请稍候',
                4: '感谢惠顾，欢迎下次光临',
                5: '该订单已取消'
            };
            return map[this.order.status] || '';
        },
        statusTone() {
            const map = { 1: 'pending', 2: 'active', 3: 'active', 4: 'done', 5: 'closed' };
            return map[this.order.status] || 'done';
        },
        tableNoText() {
            const no = this.order.tableNo || this.order.receiverName;
            return no ? `${no} 桌` : '店内';
        }
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
        money: function (val) {
            return Number(val || 0).toFixed(2);
        },

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
                data: this.order.orderNo || String(this.orderId),
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
                url: `/pages/order/payment?id=${orderId}&amount=${orderInfo.order.actualPayment}`
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

        // 查看物流（店内点单无物流，保留空实现避免误调用）
        checkLogistics: function () {},

        // 联系客服入口已移除，避免审核看到「开发中」
        contactService: function () {}
        }
};
</script>
<style>
.order-detail-container {
    min-height: 100vh;
    box-sizing: border-box;
    background: var(--bg-page);
    padding: 24rpx 24rpx calc(160rpx + env(safe-area-inset-bottom));
}

/* 订单状态 */
.status-section {
    display: flex;
    flex-direction: column;
    margin-bottom: 24rpx;
    padding: 46rpx 36rpx;
    border-radius: 24rpx;
    border: 1rpx solid var(--border-gold);
    background:
        radial-gradient(circle at 88% 0, rgba(232, 197, 71, 0.14), transparent 55%),
        var(--bg-card-gradient);
}

.status-text {
    color: var(--text-primary);
    font-size: 40rpx;
    font-weight: 700;
    letter-spacing: 1rpx;
}

.status-desc {
    margin-top: 14rpx;
    color: var(--text-muted);
    font-size: 25rpx;
}

.status-pending .status-text {
    color: var(--gold);
}

.status-closed .status-text {
    color: var(--text-muted);
}

/* 门店信息 */
.store-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.store-copy {
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.store-name {
    color: var(--text-primary);
    font-size: 29rpx;
    font-weight: 600;
}

.store-address {
    margin-top: 8rpx;
    color: var(--text-faint);
    font-size: 23rpx;
}

.instore-tag {
    flex-shrink: 0;
    margin-left: 20rpx;
    padding: 8rpx 22rpx;
    border-radius: 24rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.36);
    background: rgba(232, 197, 71, 0.1);
    color: var(--gold-light);
    font-size: 23rpx;
}

/* 商品信息 */
.goods-item {
    display: flex;
    padding: 24rpx 0;
    border-bottom: 1rpx solid var(--border-subtle);
}

.goods-item:last-child {
    border-bottom: none;
    padding-bottom: 0;
}

.goods-image {
    width: 150rpx;
    height: 150rpx;
    border-radius: 14rpx;
    margin-right: 22rpx;
    background-color: var(--bg-sunken);
    flex-shrink: 0;
}

.goods-info {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.goods-name {
    color: var(--text-primary);
    font-size: 28rpx;
    line-height: 1.4;
}

.goods-spec {
    color: var(--text-muted);
    font-size: 23rpx;
}

.goods-price-count {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.goods-price {
    color: var(--gold);
    font-size: 30rpx;
    font-weight: 600;
}

.goods-count {
    color: var(--text-faint);
    font-size: 26rpx;
}

/* 订单信息 */
.order-info-item,
.amount-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 14rpx 0;
    font-size: 27rpx;
}

.label {
    color: var(--text-muted);
}

.value {
    color: var(--text-regular);
    text-align: right;
}

.value.discount {
    color: var(--gold-light);
}

.value-copy {
    display: flex;
    align-items: center;
}

.copy-btn {
    margin-left: 18rpx;
    padding: 4rpx 18rpx;
    border-radius: 20rpx;
    border: 1rpx solid rgba(232, 197, 71, 0.4);
    color: var(--gold);
    font-size: 22rpx;
}

/* 金额信息 */
.amount-item.total .label {
    color: var(--text-primary);
    font-size: 30rpx;
    font-weight: 600;
}

.amount-item.total .value {
    color: var(--gold);
    font-size: 38rpx;
    font-weight: 700;
}

/* 底部按钮 */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 20rpx 24rpx calc(20rpx + env(safe-area-inset-bottom));
    background: rgba(16, 16, 18, 0.96);
    border-top: 1rpx solid var(--border-subtle);
}

.btn {
    height: 82rpx;
    line-height: 82rpx;
    padding: 0 48rpx;
    margin-left: 20rpx;
    font-size: 28rpx;
    font-weight: 600;
    border-radius: 41rpx;
    border: none;
    background: var(--bg-elevated);
    color: var(--text-regular);
}

.btn::after {
    border: none;
}

.btn.primary {
    background: var(--gold-gradient);
    color: #171717;
    box-shadow: 0 10rpx 26rpx rgba(232, 197, 71, 0.24);
}
</style>
