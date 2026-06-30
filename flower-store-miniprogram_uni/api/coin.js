const { get, post } = require('../utils/request');

/**
 * 获取All In币商品列表
 */
function getCoinProducts() {
    return get('/coin/products');
}

/**
 * All In币兑换商品
 * @param {Number} productId All In币商品ID
 */
function exchange(productId) {
    return post('/coin/exchange', { productId });
}

/**
 * 获取All In币兑换记录
 */
function getExchangeRecords() {
    return get('/coin/records');
}

module.exports = {
    getCoinProducts,
    exchange,
    getExchangeRecords
};
