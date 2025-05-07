import axios from 'axios'
import config from '@/config'
import router from '@/router'
import { auth } from './auth'

// 创建axios实例
const api = axios.create({
  baseURL: config.apiBaseUrl,
  withCredentials: true // 允许跨域请求携带cookie
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 不再需要手动添加Authorization头，因为cookie会自动发送
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => response.data,
  error => {
    // 允许未登录用户继续使用功能
    return Promise.reject(error)
  }
)

export const searchApi = {
  getCount() {
    return api.get('/data-count')
  },
  
  searchMaterials(params) {
    return api.get('/materials/search', { params })
  },
  
  getMaterialDetail(id) {
    return api.get(`/materials/${id}`)
  }
}

export const materialApi = {
  list() {
    return api.get('/materials')
  },
  
  create(data) {
    return api.post('/materials', data)
  },
  
  update(id, data) {
    return api.put(`/materials/${id}`, data)
  },
  
  delete(id) {
    return api.delete(`/materials/${id}`)
  }
}

export const authApi = {
  login(credentials) {
    return api.post('/login', credentials)
  },
  
  register(userData) {
    return api.post('/user/register', userData)
  },
  
  logout() {
    return api.post('/logout')
  },
  
  getUser() {
    return api.get('/user/info')
  },
  
  refreshToken() {
    return api.post('/refresh-token')
  }
}

export default api
