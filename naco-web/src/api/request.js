import axios from 'axios'
import { getToken, clearToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建 axios 实例
// 如果 VITE_API_BASE_URL 是相对路径（如 /api），则设置为空字符串，因为 url 已经包含了完整路径
// 如果是完整 URL（如 http://localhost:8080），则直接使用
const getBaseURL = () => {
  const envURL = import.meta.env.VITE_API_BASE_URL
  if (envURL) {
    // 如果是相对路径（以 / 开头），返回空字符串，因为 API 调用的 url 已经包含了 /api
    if (envURL.startsWith('/')) {
      return ''
    }
    // 如果是完整 URL，直接使用
    return envURL
  }
  // 未设置环境变量时，开发环境使用完整 URL，生产环境使用空字符串
  return import.meta.env.PROD ? '' : 'http://localhost:8080'
}

const request = axios.create({
  baseURL: getBaseURL(),
  timeout: 10000
})

// 请求拦截器：自动添加 token
request.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  (response) => {
    const res = response.data
    
    // 如果后端返回的是字符串（如 "认证失败"），直接返回
    if (typeof res === 'string') {
      return res
    }
    
    // 如果后端返回的是对象，检查 success 字段
    if (res && typeof res === 'object') {
      if (res.success === false) {
        // 优先使用 msg 字段，兼容 message 字段
        const errorMsg = res.msg || res.message || '请求失败'
        ElMessage.error(errorMsg)
        return Promise.reject(new Error(errorMsg))
      }
      return res
    }
    
    return response
  },
  (error) => {
    let errorMsg = '请求失败'
    
    // 处理 HTTP 响应错误
    if (error.response) {
      const { status, data } = error.response
      
      // 401 未授权，清除 token 并跳转登录
      if (status === 401) {
        clearToken()
        errorMsg = '登录已过期，请重新登录'
        ElMessage.error(errorMsg)
        router.push('/login')
        return Promise.reject(new Error(errorMsg))
      }
      
      // 从响应体中提取错误信息
      if (data) {
        // 优先使用 msg 字段，兼容 message 字段
        if (typeof data === 'object') {
          errorMsg = data.msg || data.message || getStatusMessage(status)
        } else if (typeof data === 'string') {
          errorMsg = data
        } else {
          errorMsg = getStatusMessage(status)
        }
      } else {
        errorMsg = getStatusMessage(status)
      }
    } else if (error.request) {
      // 请求已发出但没有收到响应
      errorMsg = '网络错误，请检查网络连接'
    } else {
      // 请求配置错误
      errorMsg = error.message || '请求配置错误'
    }
    
    ElMessage.error(errorMsg)
    return Promise.reject(new Error(errorMsg))
  }
)

// 根据 HTTP 状态码返回友好的错误提示
function getStatusMessage(status) {
  const statusMessages = {
    400: '请求参数错误',
    403: '没有权限访问',
    404: '请求的资源不存在',
    500: '服务器内部错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时'
  }
  return statusMessages[status] || `请求失败 (${status})`
}

export default request
