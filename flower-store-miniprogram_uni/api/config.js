const { get } = require('../utils/request');

/**
 * 获取公开系统配置（门店信息、WiFi 等）
 */
function getPublicConfig() {
    return get('/config/public');
}

module.exports = {
    getPublicConfig
};
