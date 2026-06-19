const { post } = require('../utils/request');

/**
 * 微信小程序登录
 * @param {string} code - 微信登录code
 * @param {string} phone - 手机号（可选）
 * @param {string} password - 密码（可选，用于密码登录）
 */
function wxLogin(code, phone, password) {
    const data = {
        code
    };
    if (phone) {
        data.phone = phone;
    }
    if (password) {
        data.password = password;
    }
    return post('/auth/wx/login', data);
}

/**
 * 手机号密码登录
 */
function phoneLogin(phone, password) {
    return post('/auth/phone/login', {
        phone,
        password
    });
}

/**
 * 手机号验证码登录
 */
function phoneCodeLogin(phone, code) {
    return post('/auth/phone/code-login', {
        phone,
        code
    });
}

/**
 * 用户注册
 */
function register(phone, password) {
    return post('/auth/register', {
        phone,
        password
    });
}

/**
 * 发送验证码
 */
function sendCode(phone) {
    return post('/auth/send-code', {
        phone
    });
}

/**
 * 退出登录
 */
function logout() {
    return post('/auth/logout');
}
module.exports = {
    wxLogin,
    phoneLogin,
    phoneCodeLogin,
    register,
    sendCode,
    logout
};
