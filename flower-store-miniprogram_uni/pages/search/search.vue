<template>
    <!-- pages/search/search.wxml -->
    <view class="search-container">
        <!-- 搜索框 -->
        <view class="search-header">
            <view class="search-input-box">
                <icon class="search-icon" type="search" size="16"></icon>
                <input class="search-input" placeholder="搜索商品" :value="keyword" @input="inputKeyword" confirm-type="search" @confirm="onSearch" :focus="true" />
                <view class="clear-icon" v-if="keyword" @tap="clearKeyword">
                    <icon type="clear" size="16"></icon>
                </view>
            </view>
            <view class="search-btn" @tap="onSearch">搜索</view>
        </view>

        <!-- 搜索历史和热门搜索 -->
        <view class="search-history" v-if="showHistory">
            <!-- 搜索历史 -->
            <view class="history-section" v-if="searchHistory.length > 0">
                <view class="section-header">
                    <text class="section-title">搜索历史</text>
                    <view class="clear-history" @tap="clearSearchHistory">
                        <icon type="clear" size="16"></icon>
                    </view>
                </view>
                <view class="history-list">
                    <view class="history-item" @tap="tapSearchItem" :data-keyword="item" v-for="(item, index) in searchHistory" :key="index">
                        {{ item }}
                    </view>
                </view>
            </view>

            <!-- 热门搜索 -->
            <view class="hot-section">
                <view class="section-header">
                    <text class="section-title">热门搜索</text>
                </view>
                <view class="hot-list">
                    <view class="hot-item" @tap="tapSearchItem" :data-keyword="item" v-for="(item, index) in hotSearches" :key="index">
                        {{ item }}
                    </view>
                </view>
            </view>
        </view>

        <!-- 搜索结果 -->
        <view class="search-results" v-if="isSearching">
            <!-- 加载中 -->
            <view class="loading" v-if="isLoading">
                <image class="loading-icon" src="/static/images/icons/loading.png" mode="aspectFit"></image>
                <text>搜索中...</text>
            </view>

            <!-- 空结果 -->
            <view class="empty-result" v-else-if="isEmpty">
                <image class="empty-icon" src="/static/images/icons/empty.png" mode="aspectFit"></image>
                <view class="empty-tip">未找到相关商品</view>
                <view class="suggestion">换个关键词试试吧</view>
            </view>

            <!-- 结果列表 -->
            <view class="result-list" v-else>
                <view class="result-item" @tap="viewProductDetail" :data-id="item.id" v-for="(item, index) in searchResults" :key="index">
                    <image class="product-image" :src="item.image" mode="aspectFill"></image>

                    <view class="product-info">
                        <view class="product-name">{{ item.name }}</view>
                        <view class="product-desc">{{ item.description }}</view>
                        <view class="product-price-box">
                            <text class="price">¥{{ item.price }}</text>
                            <text class="price-original" v-if="item.originalPrice">¥{{ item.originalPrice }}</text>
                        </view>
                        <view class="product-sales">已售{{ item.sales }}件</view>
                    </view>

                    <view class="add-cart-btn" @tap.stop.prevent="addToCart" :data-id="item.id">+</view>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
// pages/search/search.js
const productApi = require('../../api/product');
const cartApi = require('../../api/cart');
export default {
    data() {
        return {
            keyword: '',
            searchHistory: [],
            hotSearches: ['玫瑰', '康乃馨', '百合', '向日葵', '郁金香'],
            searchResults: [],
            showHistory: true,
            isSearching: false,
            isEmpty: false,
            isLoading: false
        };
    }
    /**
     * 生命周期函数--监听页面加载
     */,
    onLoad(options) {
        // 如果有传入的关键词参数，直接搜索
        if (options.keyword) {
            this.setData({
                keyword: options.keyword
            });
            this.search();
        }

        // 获取搜索历史
        this.getSearchHistory();
    },
    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady() {},
    /**
     * 生命周期函数--监听页面显示
     */
    onShow() {},
    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide() {},
    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload() {},
    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh() {},
    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom() {},
    /**
     * 用户点击右上角分享
     */
    onShareAppMessage() {},
    methods: {
        /**
         * 获取搜索历史
         */
        getSearchHistory: function () {
            const searchHistory = uni.getStorageSync('searchHistory') || [];
            this.setData({
                searchHistory
            });
        },

        /**
         * 保存搜索历史
         */
        saveSearchHistory: function (keyword) {
            if (!keyword.trim()) {
                return;
            }
            let searchHistory = uni.getStorageSync('searchHistory') || [];

            // 如果已存在相同关键词，先删除旧的
            searchHistory = searchHistory.filter((item) => item !== keyword);

            // 将新关键词添加到头部
            searchHistory.unshift(keyword);

            // 只保留最近10条搜索记录
            if (searchHistory.length > 10) {
                searchHistory = searchHistory.slice(0, 10);
            }

            // 更新本地存储和页面数据
            uni.setStorageSync('searchHistory', searchHistory);
            this.setData({
                searchHistory
            });
        },

        /**
         * 清空搜索历史
         */
        clearSearchHistory: function () {
            uni.showModal({
                title: '提示',
                content: '确定要清空搜索历史吗？',
                success: (res) => {
                    if (res.confirm) {
                        uni.removeStorageSync('searchHistory');
                        this.setData({
                            searchHistory: []
                        });
                    }
                }
            });
        },

        /**
         * 输入搜索关键词
         */
        inputKeyword: function (e) {
            this.setData({
                keyword: e.detail.value
            });

            // 如果输入框为空，显示历史记录
            if (!e.detail.value.trim()) {
                this.setData({
                    showHistory: true,
                    searchResults: [],
                    isEmpty: false
                });
            }
        },

        /**
         * 执行搜索
         */
        search: function () {
            const { keyword } = this;
            if (!keyword.trim()) {
                return;
            }

            // 保存搜索历史
            this.saveSearchHistory(keyword);

            // 显示加载状态
            this.setData({
                isLoading: true,
                showHistory: false,
                isSearching: true
            });

            // 调用搜索接口
            productApi
                .searchProducts(keyword)
                .then((data) => {
                    // 处理每个商品的images字段
                    const products = data.map((product) => {
                        if (product.images && typeof product.images === 'string') {
                            product.images = product.images.split(',').filter((img) => img.trim());
                        }
                        if (!Array.isArray(product.images) || product.images.length === 0) {
                            product.images = [product.image];
                        }
                        return product;
                    });
                    this.setData({
                        searchResults: products,
                        isLoading: false,
                        isEmpty: products.length === 0
                    });
                })
                .catch((err) => {
                    console.error('搜索失败', err);
                    this.setData({
                        isLoading: false,
                        isEmpty: true
                    });
                });
        },

        /**
         * 点击搜索按钮
         */
        onSearch: function () {
            this.search();
        },

        /**
         * 点击清除按钮
         */
        clearKeyword: function () {
            this.setData({
                keyword: '',
                showHistory: true,
                searchResults: [],
                isEmpty: false,
                isSearching: false
            });
        },

        /**
         * 点击历史记录或热门搜索项
         */
        tapSearchItem: function (e) {
            const { keyword } = e.currentTarget.dataset;
            this.setData({
                keyword
            });
            this.search();
        },

        /**
         * 查看商品详情
         */
        viewProductDetail: function (e) {
            const { id } = e.currentTarget.dataset;
            uni.navigateTo({
                url: `/pages/product/detail?id=${id}`
            });
        },

        /**
         * 添加到购物车
         */
        addToCart: function (e) {
            const { id } = e.currentTarget.dataset;
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
            cartApi
                .addToCart(id, 1)
                .then(() => {
                    uni.showToast({
                        title: '已加入购物车',
                        icon: 'success'
                    });
                })
                .catch((err) => {
                    console.error('添加购物车失败', err);
                });
        }
    }
};
</script>
<style>
/* pages/search/search.wxss */
.search-container {
    min-height: 100vh;
    background-color: #f7f7f7;
}

/* 搜索框样式 */
.search-header {
    display: flex;
    align-items: center;
    padding: 20rpx;
    background-color: #fff;
    position: sticky;
    top: 0;
    z-index: 10;
}

.search-input-box {
    flex: 1;
    height: 70rpx;
    background-color: #f5f5f5;
    border-radius: 35rpx;
    display: flex;
    align-items: center;
    padding: 0 20rpx;
    margin-right: 20rpx;
}

.search-icon {
    margin-right: 10rpx;
    color: #999;
}

.search-input {
    flex: 1;
    height: 100%;
    font-size: 28rpx;
}

.clear-icon {
    padding: 10rpx;
}

.search-btn {
    width: 100rpx;
    height: 70rpx;
    line-height: 70rpx;
    text-align: center;
    font-size: 28rpx;
    color: var(--primary-color, #ff6b81);
}

/* 搜索历史和热门搜索样式 */
.search-history {
    padding: 20rpx;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20rpx;
}

.section-title {
    font-size: 28rpx;
    color: #333;
    font-weight: bold;
}

.clear-history {
    padding: 10rpx;
}

.history-list,
.hot-list {
    display: flex;
    flex-wrap: wrap;
}

.history-item,
.hot-item {
    padding: 10rpx 20rpx;
    background-color: #f5f5f5;
    border-radius: 30rpx;
    margin: 0 20rpx 20rpx 0;
    font-size: 24rpx;
    color: #666;
}

.hot-item {
    background-color: #fff;
    border: 1rpx solid #eee;
}

.hot-section {
    margin-top: 30rpx;
}

/* 搜索结果样式 */
.search-results {
    padding: 20rpx;
}

/* 加载中样式 */
.loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 100rpx 0;
}

.loading-icon {
    width: 80rpx;
    height: 80rpx;
    margin-bottom: 20rpx;
}

.loading text {
    font-size: 28rpx;
    color: #999;
}

/* 空结果样式 */
.empty-result {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 100rpx 0;
}

.empty-icon {
    width: 150rpx;
    height: 150rpx;
    margin-bottom: 30rpx;
}

.empty-tip {
    font-size: 30rpx;
    color: #666;
    margin-bottom: 10rpx;
}

.suggestion {
    font-size: 26rpx;
    color: #999;
}

/* 结果列表样式 */
.result-list {
    padding-bottom: 30rpx;
}

.result-item {
    display: flex;
    padding: 20rpx;
    background-color: #fff;
    border-radius: 12rpx;
    margin-bottom: 20rpx;
    position: relative;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.product-image {
    width: 180rpx;
    height: 180rpx;
    border-radius: 8rpx;
    background-color: #f9f9f9;
    margin-right: 20rpx;
}

.product-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.product-name {
    font-size: 28rpx;
    font-weight: bold;
    line-height: 1.4;
    margin-bottom: 10rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}

.product-desc {
    font-size: 24rpx;
    color: #999;
    line-height: 1.4;
    margin-bottom: 10rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
}

.product-price-box {
    display: flex;
    align-items: baseline;
    margin-bottom: 10rpx;
}

.price {
    color: var(--price-color, #ff6b81);
    font-weight: bold;
    font-size: 32rpx;
}

.price-original {
    color: #999;
    text-decoration: line-through;
    font-size: 24rpx;
    margin-left: 10rpx;
}

.product-sales {
    font-size: 22rpx;
    color: #999;
}

.add-cart-btn {
    position: absolute;
    right: 20rpx;
    bottom: 20rpx;
    width: 60rpx;
    height: 60rpx;
    border-radius: 50%;
    background-color: var(--primary-color, #ff6b81);
    color: #fff;
    font-size: 40rpx;
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
