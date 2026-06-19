// 事件总线，用于页面间通信
const eventBus = {
    // 存储事件和回调的映射
    events: {},
    // 监听事件
    on: function (eventName, callback) {
        if (!this.events[eventName]) {
            this.events[eventName] = [];
        }
        this.events[eventName].push(callback);
    },
    // 触发事件
    emit: function (eventName, data) {
        const callbacks = this.events[eventName];
        if (callbacks) {
            callbacks.forEach((callback) => {
                callback(data);
            });
        }
    },
    // 移除事件监听
    off: function (eventName, callback) {
        const callbacks = this.events[eventName];
        if (callbacks) {
            if (callback) {
                // 移除特定回调
                const index = callbacks.indexOf(callback);
                if (index !== -1) {
                    callbacks.splice(index, 1);
                }
            } else {
                // 移除该事件的所有回调
                delete this.events[eventName];
            }
        }
    }
};
module.exports = eventBus;
