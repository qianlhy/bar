import request from './request'

/** 手动录入积分 */
export const addPoints = (data) => {
  return request.post('/points/admin/add', data)
}

/** 积分流水 */
export const getPointsLogs = (params) => {
  return request.get('/points/admin/logs', { params })
}

/** 锁定用户 */
export const lockUser = (userId) => {
  return request.post(`/points/admin/lock/user/${userId}`)
}

/** 释放用户锁 */
export const unlockUser = (userId) => {
  return request.post(`/points/admin/unlock/user/${userId}`)
}

/** 锁定订单 */
export const lockOrder = (orderId) => {
  return request.post(`/points/admin/lock/order/${orderId}`)
}

/** 释放订单锁 */
export const unlockOrder = (orderId) => {
  return request.post(`/points/admin/unlock/order/${orderId}`)
}

/** 锁状态 */
export const getLockStatus = (lockKey) => {
  return request.get('/points/admin/lock/status', { params: { lockKey } })
}
