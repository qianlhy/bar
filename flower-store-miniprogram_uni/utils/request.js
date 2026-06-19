/**
 * 网络请求工具
 */

const BASE_URL = 'http://localhost:8080/api'; // 后端接口地址，请根据实际情况修改

/**
 * 发送HTTP请求
 */
function request(options) {
    return new Promise((resolve, reject) => {
        const token = uni.getStorageSync('token') || '';
        uni.request({
            url: BASE_URL + options.url,
            method: options.method || 'GET',
            data: options.data || {},
            header: {
                'Content-Type': 'application/json',
                Authorization: token
            },
            success: (res) => {
                if (res.statusCode === 200) {
                    if (res.data.code === 200) {
                        resolve(res.data.data);
                    } else {
                        uni.showToast({
                            title: res.data.message || '请求失败',
                            icon: 'none'
                        });
                        reject(new Error(res.data.message));
                    }
                } else if (res.statusCode === 401) {
                    uni.showToast({
                        title: '登录已过期，请重新登录',
                        icon: 'none'
                    });
                    uni.removeStorageSync('token');
                    uni.removeStorageSync('userInfo');
                    reject(new Error('登录已过期'));
                } else {
                    uni.showToast({
                        title: '网络错误',
                        icon: 'none'
                    });
                    reject(new Error('网络错误'));
                }
            },
            fail: (err) => {
                uni.showToast({
                    title: '网络连接失败',
                    icon: 'none'
                });
                reject(err);
            }
        });
    });
}

/**
 * GET请求
 */
function get(url, data) {
    return request({
        url: url,
        method: 'GET',
        data: data
    });
}

/**
 * POST请求
 */
function post(url, data) {
    return request({
        url: url,
        method: 'POST',
        data: data
    });
}

/**
 * PUT请求
 */
function put(url, data) {
    return request({
        url: url,
        method: 'PUT',
        data: data
    });
}

/**
 * DELETE请求
 */
function del(url, params) {
    // 将参数拼接到URL中
    if (params) {
        const queryString = Object.keys(params)
            .map((key) => `${key}=${params[key]}`)
            .join('&');
        url = url + (url.includes('?') ? '&' : '?') + queryString;
    }
    return request({
        url: url,
        method: 'DELETE'
    });
}
module.exports = {
    request,
    get,
    post,
    put,
    del
};
