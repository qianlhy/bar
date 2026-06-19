const { get, post, put, del } = require('../utils/request');

/**
 * 获取购物车列表
 */
function getCartList() {
    return get('/cart/list');
}

/**
 * 添加到购物车
 */
function addToCart(productId, count) {
    return post('/cart', {
        productId: productId,
        count: count
    });
}

/**
 * 更新购物车商品数量
 */
function updateCartCount(id, count) {
    return put('/cart/' + id + '?count=' + count, null);
}

/**
 * 删除购物车商品
 */
function deleteCartItem(id) {
    return del('/cart/' + id);
}

/**
 * 清空购物车
 */
function clearCart() {
    return del('/cart/clear');
}
module.exports = {
    getCartList,
    addToCart,
    updateCartCount,
    deleteCartItem,
    clearCart
};
