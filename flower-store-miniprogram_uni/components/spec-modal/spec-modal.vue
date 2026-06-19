<template>
    <view class="spec-mask" v-if="visible" @tap="onClose">
        <view class="spec-modal" @tap.stop>
            <view class="spec-header">
                <image class="spec-img" :src="product.image" mode="aspectFill"></image>
                <view class="spec-title-wrap">
                    <text class="spec-name">{{ product.name }}</text>
                </view>
                <view class="spec-close" @tap="onClose">⌄</view>
            </view>

            <scroll-view scroll-y class="spec-body">
                <view class="spec-row">
                    <text class="spec-label">数量</text>
                    <view class="qty-control">
                        <view class="qty-btn" @tap="decrease">−</view>
                        <text class="qty-num">{{ count }}</text>
                        <view class="qty-btn" @tap="increase">＋</view>
                    </view>
                </view>

                <view class="spec-group" v-for="(group, gIndex) in specGroups" :key="gIndex">
                    <text class="spec-label">{{ group.name }}</text>
                    <view class="spec-options">
                        <view
                            v-for="(opt, oIndex) in group.options"
                            :key="oIndex"
                            :class="'spec-option ' + (selected[gIndex] === oIndex ? 'active' : '')"
                            @tap="selectOption(gIndex, oIndex)"
                        >{{ opt.label }}</view>
                    </view>
                </view>
            </scroll-view>

            <view class="spec-footer">
                <view class="spec-price-wrap">
                    <text class="spec-price">¥ {{ totalPrice }}</text>
                    <text class="spec-selected">{{ selectedText }}</text>
                </view>
                <view class="spec-confirm" @tap="onConfirm">
                    <text class="confirm-icon">🛒</text>
                    <text>加入购物车</text>
                </view>
            </view>
        </view>
    </view>
</template>

<script>
export default {
    name: 'spec-modal',
    props: {
        visible: { type: Boolean, default: false },
        product: { type: Object, default: () => ({}) }
    },
    data() {
        return {
            count: 1,
            selected: {}
        };
    },
    computed: {
        specGroups() {
            const raw = this.product && this.product.specs;
            if (!raw) return [];
            try {
                const parsed = typeof raw === 'string' ? JSON.parse(raw) : raw;
                return Array.isArray(parsed) ? parsed : [];
            } catch (e) {
                return [];
            }
        },
        unitPrice() {
            let price = Number(this.product.price) || 0;
            this.specGroups.forEach((group, gIndex) => {
                const oIndex = this.selected[gIndex];
                if (oIndex != null && group.options[oIndex] && group.options[oIndex].price != null) {
                    price = Number(group.options[oIndex].price);
                }
            });
            return price;
        },
        totalPrice() {
            return (this.unitPrice * this.count).toFixed(2);
        },
        selectedText() {
            const parts = [];
            this.specGroups.forEach((group, gIndex) => {
                const oIndex = this.selected[gIndex];
                if (oIndex != null && group.options[oIndex]) {
                    parts.push(group.options[oIndex].label);
                }
            });
            return parts.join(' / ');
        }
    },
    watch: {
        visible(val) {
            if (val) this.resetSelection();
        }
    },
    methods: {
        resetSelection() {
            this.count = 1;
            const selected = {};
            this.specGroups.forEach((group, gIndex) => {
                if (group.options && group.options.length > 0) {
                    selected[gIndex] = 0;
                }
            });
            this.selected = selected;
        },
        selectOption(gIndex, oIndex) {
            this.$set(this.selected, gIndex, oIndex);
        },
        increase() {
            this.count++;
        },
        decrease() {
            if (this.count > 1) this.count--;
        },
        onClose() {
            this.$emit('close');
        },
        onConfirm() {
            this.$emit('confirm', {
                product: this.product,
                count: this.count,
                specText: this.selectedText,
                unitPrice: this.unitPrice
            });
        }
    }
};
</script>

<style>
.spec-mask {
    position: fixed;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0, 0, 0, 0.6);
    z-index: 9999;
    display: flex;
    align-items: flex-end;
}
.spec-modal {
    width: 100%;
    background: #fff;
    border-radius: 24rpx 24rpx 0 0;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    position: relative;
}
.spec-header {
    display: flex;
    align-items: center;
    padding: 30rpx;
    position: relative;
}
.spec-img {
    width: 140rpx;
    height: 140rpx;
    border-radius: 16rpx;
    background: #f0f0f0;
    margin-top: -50rpx;
    border: 4rpx solid #fff;
    box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.15);
}
.spec-title-wrap { flex: 1; margin-left: 24rpx; }
.spec-name { font-size: 34rpx; font-weight: bold; color: #333; }
.spec-close {
    width: 48rpx;
    height: 48rpx;
    border-radius: 50%;
    background: #f0f0f0;
    color: #999;
    font-size: 32rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
}
.spec-body {
    flex: 1;
    padding: 10rpx 30rpx;
    max-height: 50vh;
}
.spec-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 24rpx 0;
}
.spec-label {
    font-size: 28rpx;
    color: #333;
    display: block;
    margin-bottom: 20rpx;
}
.qty-control { display: flex; align-items: center; }
.qty-btn {
    width: 56rpx;
    height: 56rpx;
    border-radius: 50%;
    background: #333;
    color: #fff;
    font-size: 36rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    line-height: 1;
}
.qty-num { font-size: 30rpx; color: #333; min-width: 70rpx; text-align: center; }
.spec-group { padding: 16rpx 0; }
.spec-options { display: flex; flex-wrap: wrap; gap: 20rpx; }
.spec-option {
    padding: 16rpx 40rpx;
    background: #f5f5f5;
    border-radius: 12rpx;
    font-size: 28rpx;
    color: #666;
    border: 2rpx solid transparent;
}
.spec-option.active {
    background: #fff;
    color: #333;
    border-color: #333;
    font-weight: bold;
}
.spec-footer {
    display: flex;
    align-items: center;
    padding: 24rpx 30rpx;
    border-top: 1rpx solid #f0f0f0;
    padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
}
.spec-price-wrap { flex: 1; }
.spec-price { font-size: 40rpx; color: #333; font-weight: bold; display: block; }
.spec-selected { font-size: 22rpx; color: #999; }
.spec-confirm {
    background: #1a1a1a;
    color: #fff;
    padding: 24rpx 48rpx;
    border-radius: 48rpx;
    font-size: 30rpx;
    display: flex;
    align-items: center;
    gap: 10rpx;
}
.confirm-icon { font-size: 32rpx; }
</style>
