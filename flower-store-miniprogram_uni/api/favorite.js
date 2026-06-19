const { get, post, del } = require('../utils/request');

/**
 * 获取收藏列表
 */
function getFavoriteList() {
    return get('/favorite/list');
}

/**
 * 检查是否已收藏
 */
function checkFavorite(productId) {
    return get('/favorite/check', {
        productId: productId
    });
}

/**
 * 添加收藏
 */
function addFavorite(productId) {
    return post('/favorite', {
        productId: productId
    });
}

/**
 * 取消收藏
 */
function removeFavorite(productId) {
    return del('/favorite', {
        productId: productId
    });
}
module.exports = {
    getFavoriteList,
    checkFavorite,
    addFavorite,
    removeFavorite
};
