import request from './request'

/**
 * 管理员登录
 */
export function login(data) {
  return request({
    url: '/auth/admin/login',
    method: 'post',
    data
  })
}

