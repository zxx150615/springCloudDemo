import request from '../request'

/**
 * 获取订单列表
 */
export function getOrderList() {
  return request({
    url: '/api/order/list',
    method: 'get'
  })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id) {
  return request({
    url: `/api/order/${id}`,
    method: 'get'
  })
}
