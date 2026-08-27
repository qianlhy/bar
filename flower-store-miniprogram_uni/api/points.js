const { request } = require('../utils/request');

/**
 * 积分抵扣预览
 * @param {number} amount 订单金额
 * @param {number} points 希望使用的积分（可选）
 */
function previewPoints(amount, points) {
    const params = { amount };
    if (points != null) {
        params.points = points;
    }
    return request({
        url: '/points/preview',
        method: 'GET',
        data: params,
        // 积分是可选权益，接口异常时确认订单页应继续正常支付
        silent: true
    });
}

module.exports = {
    previewPoints
};
