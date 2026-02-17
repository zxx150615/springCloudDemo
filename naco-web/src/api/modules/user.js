import request from '../request'

/**
 * 获取用户列表
 */
export function getUserList() {
  return request({
    url: '/api/user/list',
    method: 'get'
  })
}

/**
 * 管理员创建用户
 */
export function createUser(data) {
  return request({
    url: '/api/admin/user',
    method: 'post',
    data
  })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id) {
  return request({
    url: `/api/user/${id}`,
    method: 'get'
  })
}
