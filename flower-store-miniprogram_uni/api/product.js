const { get } = require('../utils/request');

/**
 * 获取商品列表
 */
function getProductList(categoryId) {
    if (!categoryId) {
        return get('/product/list');
    }
    return get('/product/list', {
        categoryId: categoryId
    });
}

/**
 * 根据分类ID获取商品列表
 */
function getProductsByCategory(categoryId) {
    return get('/product/category/' + categoryId);
}

/**
 * 根据ID获取商品详情
 */
function getProductById(id) {
    return get('/product/' + id);
}

/**
 * 获取热门商品
 */
function getHotProducts(limit) {
    return get('/product/hot', {
        limit: limit || 6
    });
}

/**
 * 获取新品商品
 */
function getNewProducts(limit) {
    return get('/product/new', {
        limit: limit || 6
    });
}

/**
 * 搜索商品
 */
function searchProducts(keyword) {
    return get('/product/search', {
        keyword: keyword
    });
}
module.exports = {
    getProductList,
    getProductsByCategory,
    getProductById,
    getHotProducts,
    getNewProducts,
    searchProducts
};
