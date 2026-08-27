const STORAGE_KEY = 'adultDrinkingConfirmed';

function ensureAdultDrinking() {
    if (uni.getStorageSync(STORAGE_KEY) === true) {
        return Promise.resolve(true);
    }

    return new Promise((resolve, reject) => {
        uni.showModal({
            title: '成年饮酒提示',
            content: '酒类商品仅面向已满18周岁的成年人。请确认您已满18周岁，并承诺理性饮酒。',
            // 微信小程序 confirmText / cancelText 最长 4 个汉字，超长会导致弹窗失败并被当成拒绝。
            confirmText: '确认',
            cancelText: '取消',
            success: (res) => {
                if (res.confirm) {
                    uni.setStorageSync(STORAGE_KEY, true);
                    resolve(true);
                    return;
                }
                reject(new Error('adult-confirmation-declined'));
            },
            fail: (err) => {
                console.error('adult drinking modal failed', err);
                reject(err);
            }
        });
    });
}

module.exports = {
    ensureAdultDrinking
};
