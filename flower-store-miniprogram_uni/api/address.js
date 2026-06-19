const { get, post, put, del } = require('../utils/request');

/**
 * 获取地址列表
 */
function getAddressList() {
    return get('/address/list');
}

/**
 * 根据ID获取地址
 */
function getAddressById(id) {
    return get('/address/' + id);
}

/**
 * 获取默认地址
 */
function getDefaultAddress() {
    return get('/address/default');
}

/**
 * 添加地址
 */
function addAddress(data) {
    return post('/address', data);
}

/**
 * 更新地址
 */
function updateAddress(data) {
    return put('/address', data);
}

/**
 * 删除地址
 */
function deleteAddress(id) {
    return del('/address/' + id);
}
module.exports = {
    getAddressList,
    getAddressById,
    getDefaultAddress,
    addAddress,
    updateAddress,
    deleteAddress
};
