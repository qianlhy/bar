import request from './request'

/**
 * 分页查询分类列表
 */
export function getCategoryPage(params) {
  return request({
    url: '/category/page',
    method: 'get',
    params
  })
}

/**
 * 获取所有分类列表
 */
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

/**
 * 根据ID查询分类
 */
export function getCategoryById(id) {
  return request({
    url: `/category/${id}`,
    method: 'get'
  })
}

/**
 * 添加分类
 */
export function addCategory(data) {
  return request({
    url: '/category',
    method: 'post',
    data
  })
}

/**
 * 更新分类
 */
export function updateCategory(data) {
  return request({
    url: '/category',
    method: 'put',
    data
  })
}

/**
 * 删除分类
 */
export function deleteCategory(id) {
  return request({
    url: `/category/${id}`,
    method: 'delete'
  })
}

