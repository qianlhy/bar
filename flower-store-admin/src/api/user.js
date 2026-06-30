import request from './request'

/**
 * 获取用户分页列表
 */
export const getUserPage = (params) => {
  return request.get('/admin/user/page', { params })
}

/**
 * 更新用户状态
 */
export const updateUserStatus = (id, status) => {
  return request.put(`/admin/user/status/${id}`, { status })
}

/**
 * 调整用户资产（余额 / All In币 / 大师分 / 优惠券）
 */
export const updateUserAssets = (id, data) => {
  return request.put(`/admin/user/assets/${id}`, data)
}

/**
 * 大师分排行榜分页
 */
export const getRankPage = (params) => {
  return request.get('/admin/user/rank', { params })
}

