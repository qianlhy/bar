import request from './request'

/**
 * 分页查询订单列表
 */
export function getOrderPage(params) {
  return request({
    url: '/order/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询订单详情
 */
export function getOrderById(id) {
  return request({
    url: `/order/${id}`,
    method: 'get'
  })
}

/**
 * 更新订单状态
 */
export function updateOrderStatus(id, status) {
  return request({
    url: `/order/${id}/status`,
    method: 'put',
    params: { status }
  })
}

/**
 * 获取订单统计
 */
export function getOrderStatistics() {
  return request({
    url: '/order/statistics',
    method: 'get'
  })
}

