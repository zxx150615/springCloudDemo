/**
 * Token 和用户信息管理工具
 */

const TOKEN_KEY = 'naco_token'
const USER_KEY = 'naco_user'

/**
 * 保存 token
 */
export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 获取 token
 */
export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

/**
 * 清除 token 和用户信息
 */
export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

/**
 * 保存用户信息
 */
export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

/**
 * 获取用户信息
 */
export function getUser() {
  const userStr = localStorage.getItem(USER_KEY)
  if (!userStr) {
    return null
  }
  try {
    return JSON.parse(userStr)
  } catch (e) {
    return null
  }
}

/**
 * 检查是否已登录
 */
export function isAuthenticated() {
  return !!getToken()
}
