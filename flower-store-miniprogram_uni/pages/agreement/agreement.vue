<template>
    <view class="agreement-page">
        <view class="hero">
            <text class="kicker">ALL IN TAVERN</text>
            <text class="title">{{ title }}</text>
            <text class="date">更新日期：2026年8月14日</text>
        </view>
        <view class="content">
            <block v-for="(section, index) in sections" :key="index">
                <text class="section-title">{{ section.title }}</text>
                <text class="paragraph">{{ section.content }}</text>
            </block>
        </view>
    </view>
</template>

<script>
export default {
    data() {
        return {
            type: 'user'
        };
    },
    computed: {
        title() {
            return this.type === 'privacy' ? '隐私政策' : '用户协议';
        },
        sections() {
            if (this.type === 'privacy') {
                return [
                    { title: '一、信息收集', content: '为完成微信登录，我们会处理微信登录凭证对应的用户标识。仅当您主动选择头像或填写昵称时，我们才会处理您提交的头像图片和昵称；下单及支付时会处理履约所必需的商品、订单和支付结果信息。' },
                    { title: '二、信息使用', content: '相关信息仅用于身份识别、订单创建与履约、支付结果确认、积分核算、客户服务和安全保障，不会用于与本服务无关的用途。' },
                    { title: '三、权限说明', content: '头像图片和昵称仅在您主动使用资料设置等对应功能时处理。本小程序不申请位置权限。您可以拒绝提供非必要信息，商品浏览不受影响。' },
                    { title: '四、信息保护', content: '我们采取访问控制、传输加密和权限隔离等措施保护数据。法律法规另有规定或取得您授权的情形除外。' },
                    { title: '五、官方指引', content: '微信小程序官方《用户隐私保护指引》是隐私规则的正式入口，实际收集项以微信公众平台审核并展示的内容为准。' },
                    { title: '六、联系我们', content: '如需查询、更正或删除个人信息，请联系梭哈酒馆南京店工作人员处理。' }
                ];
            }
            return [
                { title: '一、服务说明', content: '本小程序提供梭哈酒馆南京店的商品浏览、店内点单、订单支付、会员积分及相关服务。' },
                { title: '二、账号使用', content: '您应使用本人微信账号登录，并妥善保管账号。因违规使用或转借账号产生的责任由使用者承担。' },
                { title: '三、订单与支付', content: '请在提交订单前确认商品、规格、数量、桌台及金额。支付结果以微信支付和系统订单记录为准。' },
                { title: '四、积分规则', content: '积分按门店公布规则获取和使用，不可兑现或转让。系统会按运营规则进行抵扣额度限制和周期性清理。' },
                { title: '五、其他', content: '如遇商品售罄、系统维护或不可抗力，门店将及时协商处理。继续使用本服务即表示您已阅读并同意本协议。' }
            ];
        }
    },
    onLoad(options) {
        this.type = options.type === 'privacy' ? 'privacy' : 'user';
        uni.setNavigationBarTitle({ title: this.title });
    }
};
</script>

<style>
.agreement-page {
    min-height: 100vh;
    box-sizing: border-box;
    padding: 32rpx 24rpx calc(52rpx + env(safe-area-inset-bottom));
    background: radial-gradient(circle at 100% 0, rgba(232, 197, 71, 0.09), transparent 34%), var(--bg-page);
}

.hero,
.content {
    padding: 34rpx 30rpx;
    border: 1rpx solid var(--border-gold);
    border-radius: 24rpx;
    background: var(--bg-card-gradient);
}

.hero {
    display: flex;
    flex-direction: column;
    margin-bottom: 20rpx;
}

.kicker {
    color: var(--gold);
    font-size: 20rpx;
    letter-spacing: 4rpx;
}

.title {
    margin: 12rpx 0 10rpx;
    color: var(--text-primary);
    font-size: 42rpx;
    font-weight: 700;
}

.date {
    color: var(--text-muted);
    font-size: 23rpx;
}

.section-title,
.paragraph {
    display: block;
}

.section-title {
    margin: 24rpx 0 10rpx;
    color: var(--gold-light);
    font-size: 28rpx;
    font-weight: 600;
}

.section-title:first-child {
    margin-top: 0;
}

.paragraph {
    color: var(--text-regular);
    font-size: 26rpx;
    line-height: 1.9;
    text-align: justify;
}
</style>
