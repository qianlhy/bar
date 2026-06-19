const { get, post, put } = require('../utils/request');

/**
 * 获取订单列表
 */
function getOrderList(status) {
    // 过滤掉null和undefined的status参数
    const params = {};
    if (status !== null && status !== undefined && status !== 'null') {
        params.status = status;
    }
    return get('/order/list', params);
}

/**
 * 根据ID获取订单详情
 */
function getOrderById(id) {
    return get('/order/' + id);
}

/**
 * 创建订单
 */
function createOrder(data) {
    return post('/order', data);
}

/**
 * 更新订单状态
 */
function updateOrderStatus(id, status) {
    return put('/order/' + id + '/status?status=' + status);
}

/**
 * 获取订单统计（按状态）
 */
function getOrderStatistics() {
    return get('/order/statistics');
}

/**
 * 取消订单
 */
function cancelOrder(id) {
    return put('/order/' + id + '/cancel');
}
module.exports = {
    getOrderList,
    getOrderById,
    getOrderDetail: getOrderById,
    // 别名
    createOrder,
    updateOrderStatus,
    cancelOrder,
    getOrderStatistics
};
