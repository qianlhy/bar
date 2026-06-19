const { get } = require('../utils/request');

/**
 * 获取排行榜
 * @param {String} type 榜单类型：month-本月，lastMonth-上月，quarter-本季度，lastQuarter-上季度
 */
function getRankList(type) {
    return get('/rank/list', { type: type || 'month' });
}

module.exports = {
    getRankList
};
