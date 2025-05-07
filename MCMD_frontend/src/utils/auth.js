import axios from 'axios'
import config from '@/config'

// 设置axios默认配置
axios.defaults.baseURL = config.apiBaseUrl
axios.defaults.withCredentials = true // 允许跨域请求携带cookie

// 解析JWT令牌
function parseJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
    }).join(''))
    return JSON.parse(jsonPayload)
  } catch (e) {
    return null
  }
}

class Auth {
  constructor() {
    this.username = sessionStorage.getItem('username')
    this.refreshing = false
  }

  isLoggedIn() {
    // 只检查 username，因为令牌现在存储在cookie中
    return !!this.username
  }

  async login(username, password) {
    try {
      const response = await axios.post('/login', {
        username,
        password
      })

      if (response.data.message === 'Login successful') {
        this.username = username
        
        // 存储用户名
        sessionStorage.setItem('username', this.username)
        
        // 触发登录成功事件
        window.dispatchEvent(new CustomEvent('login-success'))
        
        return {
          success: true,
          message: 'Login successful'
        }
      }
      
      return {
        success: false,
        message: response.data.message
      }
    } catch (error) {
      console.error('Login error:', error)
      return {
        success: false,
        message: error.response?.data?.message || 'Login failed, please try again later'
      }
    }
  }

  async logout() {
    try {
      await axios.post('/logout')
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      this.username = null
      sessionStorage.removeItem('username')
      // 刷新页面以确保状态更新
      window.location.reload()
    }
  }

  getAuthHeader() {
    // 不再需要手动添加Authorization头，因为cookie会自动发送
    return {}
  }
  
  // 刷新令牌
  async refreshToken() {
    // 如果已经在刷新中，则返回
    if (this.refreshing) return
    
    try {
      this.refreshing = true
      
      await axios.post('/refresh-token')
      console.log('Token refreshed')
    } catch (error) {
      console.error('Token refresh failed:', error)
      
      // 如果刷新失败且是因为令牌无效，则登出
      if (error.response && error.response.status === 401) {
        this.logout()
      }
    } finally {
      this.refreshing = false
    }
  }
}

export const auth = new Auth()

// 添加请求拦截器
axios.interceptors.request.use(
  async config => {
    // 不再需要手动添加Authorization头，因为cookie会自动发送
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 添加响应拦截器
axios.interceptors.response.use(
  response => response,
  error => {
    // 401错误不再重定向到登录页面，允许未登录用户继续访问
    if (error.response && error.response.status === 401) {
      // 仅清除用户名，但不进行重定向
      sessionStorage.removeItem('username')
      auth.username = null
      console.log('Authentication error, but continuing as guest user')
    }
    
    return Promise.reject(error)
  }
)
