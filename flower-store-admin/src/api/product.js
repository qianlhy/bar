import request from './request'

/**
 * 分页查询商品列表
 */
export function getProductPage(params) {
  return request({
    url: '/product/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID查询商品
 */
export function getProductById(id) {
  return request({
    url: `/product/${id}`,
    method: 'get'
  })
}

/**
 * 添加商品
 */
export function addProduct(data) {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}

/**
 * 更新商品
 */
export function updateProduct(data) {
  return request({
    url: '/product',
    method: 'put',
    data
  })
}

/**
 * 删除商品
 */
export function deleteProduct(id) {
  return request({
    url: `/product/${id}`,
    method: 'delete'
  })
}

