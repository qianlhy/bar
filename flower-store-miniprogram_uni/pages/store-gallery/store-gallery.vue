<template>
    <view class="gallery-page poker-bg">
        <swiper
            v-if="images.length > 0"
            class="gallery-swiper"
            :indicator-dots="true"
            indicator-color="rgba(255,255,255,0.3)"
            indicator-active-color="#a01c30"
            :autoplay="true"
            :interval="4000"
            :circular="true"
        >
            <swiper-item v-for="(img, index) in images" :key="index">
                <image class="gallery-img" :src="img" mode="aspectFill" @tap="previewImage" :data-index="index"></image>
            </swiper-item>
        </swiper>
        <view class="gallery-empty" v-else>
            <text class="empty-title font-art">店内环境</text>
            <text class="empty-tip">暂无装修展示图片，敬请期待</text>
        </view>
        <view class="gallery-tip" v-if="images.length > 0">
            <text>点击图片可全屏预览</text>
        </view>
    </view>
</template>

<script>
const configApi = require('../../api/config');
export default {
    data() {
        return {
            images: []
        };
    },
    onLoad() {
        this.loadGallery();
    },
    methods: {
        loadGallery() {
            configApi.getPublicConfig().then((cfg) => {
                if (!cfg || !cfg.store_gallery) {
                    this.images = [];
                    return;
                }
                const raw = cfg.store_gallery.trim();
                if (raw.startsWith('[')) {
                    try {
                        this.images = JSON.parse(raw).filter(Boolean);
                    } catch (e) {
                        this.images = [];
                    }
                } else {
                    this.images = raw.split(',').map((s) => s.trim()).filter(Boolean);
                }
            }).catch(() => {});
        },
        previewImage(e) {
            const index = e.currentTarget.dataset.index || 0;
            uni.previewImage({
                current: this.images[index],
                urls: this.images
            });
        }
    }
};
</script>

<style>
.gallery-page {
    min-height: 100vh;
    padding: 24rpx 24rpx calc(40rpx + env(safe-area-inset-bottom));
    box-sizing: border-box;
}
.gallery-swiper {
    width: 100%;
    height: calc(100vh - 160rpx);
    border-radius: 20rpx;
    overflow: hidden;
    border: 1rpx solid rgba(255, 255, 255, 0.08);
}
.gallery-img {
    width: 100%;
    height: 100%;
    display: block;
}
.gallery-empty {
    margin-top: 200rpx;
    text-align: center;
}
.empty-title {
    font-size: 36rpx;
    color: #ddd;
    display: block;
}
.empty-tip {
    font-size: 26rpx;
    color: #666;
    margin-top: 20rpx;
    display: block;
}
.gallery-tip {
    text-align: center;
    margin-top: 24rpx;
}
.gallery-tip text {
    font-size: 22rpx;
    color: #555;
}
</style>
