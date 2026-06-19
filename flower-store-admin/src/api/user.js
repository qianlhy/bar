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

