const { get } = require('../utils/request');

/**
 * 获取分类列表
 */
function getCategoryList() {
    return get('/category/list');
}
module.exports = {
    getCategoryList
};
