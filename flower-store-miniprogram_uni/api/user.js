const { get, put } = require('../utils/request');

/**
 * 获取用户信息
 */
function getUserInfo() {
    return get('/user/info');
}

/**
 * 更新用户信息
 */
function updateUserInfo(data) {
    return put('/user/update', data);
}
module.exports = {
    getUserInfo,
    updateUserInfo
};
