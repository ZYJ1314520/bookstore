import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const url = config.url || ''
    let token = ''
    if (url.includes('/api/user')) {
      token = localStorage.getItem('token') || localStorage.getItem('shopToken') || ''
    } else if (url.includes('/api/shop')) {
      token = localStorage.getItem('shopToken')
    } else if (url.includes('/api/admin')) {
      token = localStorage.getItem('adminToken')
    }
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器 - 返回完整response，页面用 res.data.code / res.data.data 访问
request.interceptors.response.use(
  response => response,
  error => {
    ElMessage.error(error.response?.data?.message || error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
