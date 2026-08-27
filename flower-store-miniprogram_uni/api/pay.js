const { get, post } = require('../utils/request');

/**
 * 商品订单支付
 * @param {Number} orderId 订单ID
 * @param {Object} data { payMethod: 'wechat'|'coins'|'mixed' }
 */
function payOrder(orderId, data = {}) {
    return post('/pay/order/' + orderId, data);
}

/**
 * 会员充值发起微信支付，返回调起支付所需参数
 * @param {Number} rechargeOrderId 充值订单ID
 */
function payRecharge(rechargeOrderId) {
    return post('/pay/recharge/' + rechargeOrderId);
}

/**
 * 主动查询支付结果（回调延迟时的兜底）
 * @param {String} outTradeNo 商户订单号
 */
function queryPayResult(outTradeNo) {
    return get('/pay/query', { outTradeNo });
}

/**
 * 调起微信支付
 * @param {Object} params 后端返回的支付参数
 */
function requestPayment(params) {
    return new Promise((resolve, reject) => {
        uni.requestPayment({
            provider: 'wxpay',
            timeStamp: params.timeStamp,
            nonceStr: params.nonceStr,
            package: params.package,
            signType: params.signType || 'RSA',
            paySign: params.paySign,
            success: resolve,
            fail: reject
        });
    });
}

module.exports = {
    payOrder,
    payRecharge,
    queryPayResult,
    requestPayment
};
