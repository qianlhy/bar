<template>
    <view class="gallery-page poker-bg">
        <block v-if="images.length > 0">
            <view class="gallery-list">
                <view
                    v-for="(img, index) in images"
                    :key="index"
                    class="gallery-item g-tap"
                    @tap="previewImage"
                    :data-index="index"
                >
                    <image class="gallery-img" :src="img" mode="widthFix"></image>
                </view>
            </view>
            <view class="gallery-tip">
                <text>点击图片可全屏预览</text>
            </view>
        </block>
        <view class="gallery-empty" v-else>
            <text class="empty-title font-art">店内环境</text>
            <text class="empty-tip">暂无装修展示图片，敬请期待</text>
        </view>
    </view>
</template>

<script>
const configApi = require('../../api/config');

const DEFAULT_GALLERY = '/static/images/store/store-env-01.jpg';
const REMOTE_STORE_ENV = 'https://bar.twst.work/api/uploads/store/store-env-01.jpg';

function normalizeGallery(images) {
    return images.map((url) => {
        const text = String(url || '').trim();
        if (!text) return text;
        if (text === REMOTE_STORE_ENV || text.indexOf('/store/store-env-01.jpg') >= 0) {
            return DEFAULT_GALLERY;
        }
        return text;
    });
}

function parseGallery(raw) {
    if (!raw || !String(raw).trim()) return [];
    const text = String(raw).trim();
    if (text.startsWith('[')) {
        try {
            return JSON.parse(text).filter(Boolean);
        } catch (e) {
            return [];
        }
    }
    return text.split(',').map((s) => s.trim()).filter(Boolean);
}

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
                const images = normalizeGallery(parseGallery(cfg && cfg.store_gallery));
                this.images = images.length ? images : [DEFAULT_GALLERY];
            }).catch(() => {
                this.images = [DEFAULT_GALLERY];
            });
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
.gallery-list {
    box-sizing: border-box;
}
.gallery-item {
    border-radius: 20rpx;
    overflow: hidden;
    border: 1rpx solid rgba(255, 255, 255, 0.08);
    background: rgba(0, 0, 0, 0.2);
}
.gallery-item + .gallery-item {
    margin-top: 24rpx;
}
.gallery-img {
    width: 100%;
    display: block;
    vertical-align: top;
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
    padding-top: 24rpx;
}
.gallery-tip text {
    font-size: 22rpx;
    color: #555;
}
</style>
