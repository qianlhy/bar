import request from './request'

/**
 * All In币商品分页（管理端）
 */
export const getCoinProductPage = (params) => {
  return request.get('/coin/admin/products/page', { params })
}

export const addCoinProduct = (data) => {
  return request.post('/coin/admin/products', data)
}

export const updateCoinProduct = (data) => {
  return request.put('/coin/admin/products', data)
}

export const deleteCoinProduct = (id) => {
  return request.delete(`/coin/admin/products/${id}`)
}

/**
 * All In币兑换记录分页
 */
export const getCoinExchangePage = (params) => {
  return request.get('/coin/admin/exchanges/page', { params })
}
