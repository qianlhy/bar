import request from './request'

/**
 * 获取全部系统配置
 */
export const getAllConfig = () => {
  return request.get('/config/all')
}

/**
 * 保存系统配置
 */
export const saveConfig = (data) => {
  return request.post('/config/save', data)
}
