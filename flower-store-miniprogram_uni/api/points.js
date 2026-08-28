const { request, get, post } = require('../utils/request');

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
        silent: true
    });
}

function getCheckinStatus() {
    return get('/points/checkin/status');
}

function checkin() {
    return post('/points/checkin');
}

module.exports = {
    previewPoints,
    getCheckinStatus,
    checkin
};
