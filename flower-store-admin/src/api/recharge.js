import request from './request'

/**
 * 充值套餐分页
 */
export const getRechargePackagePage = (params) => {
  return request.get('/recharge/admin/packages/page', { params })
}

export const addRechargePackage = (data) => {
  return request.post('/recharge/admin/packages', data)
}

export const updateRechargePackage = (data) => {
  return request.put('/recharge/admin/packages', data)
}

export const deleteRechargePackage = (id) => {
  return request.delete(`/recharge/admin/packages/${id}`)
}

/**
 * 充值订单分页
 */
export const getRechargeOrderPage = (params) => {
  return request.get('/recharge/admin/orders/page', { params })
}

/**
 * 确认到账
 */
export const confirmRechargeOrder = (id) => {
  return request.put(`/recharge/admin/orders/${id}/confirm`)
}
