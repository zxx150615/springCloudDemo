import request from '../request'

/**
 * 获取商家列表
 */
export function getMerchantList() {
  return request({
    url: '/api/merchant/list',
    method: 'get'
  })
}

/**
 * 分页获取商家列表
 */
export function getMerchantPage(params) {
  return request({
    url: '/api/merchant/page',
    method: 'get',
    params
  })
}

/**
 * 获取商家详情
 */
export function getMerchantDetail(id) {
  return request({
    url: `/api/merchant/${id}`,
    method: 'get'
  })
}

/**
 * 创建商家
 */
export function createMerchant(data) {
  return request({
    url: '/api/merchant',
    method: 'post',
    data
  })
}

/**
 * 更新商家
 */
export function updateMerchant(id, data) {
  return request({
    url: `/api/merchant/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商家
 */
export function deleteMerchant(id) {
  return request({
    url: `/api/merchant/${id}`,
    method: 'delete'
  })
}
