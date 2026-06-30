const { get, post } = require('../utils/request');

/**
 * 获取充值套餐列表
 */
function getRechargePackages() {
    return get('/recharge/packages');
}

/**
 * 创建充值订单（微信支付能力预留）
 * @param {Number} packageId 套餐ID
 */
function createRecharge(packageId) {
    return post('/recharge/create', { packageId });
}

/**
 * 我的充值记录
 */
function getMyRecharge() {
    return get('/recharge/my');
}

module.exports = {
    getRechargePackages,
    createRecharge,
    getMyRecharge
};
