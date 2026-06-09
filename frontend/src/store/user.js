import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const cartCount = ref(0)

  // 用户登录
  async function login(username, password) {
    try {
      const res = await axios.post('/api/user/auth/login', { username, password })
      if (res.data.code === 200) {
        token.value = res.data.data.token
        userInfo.value = res.data.data.userInfo
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('userRole', '1')
        ElMessage.success('登录成功')
        return true
      }
      ElMessage.error(res.data.message || '登录失败')
      return false
    } catch (e) {
      ElMessage.error('登录失败：' + (e.response?.data?.message || e.message || '网络错误，请检查后端是否启动'))
      return false
    }
  }

  // 用户注册
  async function register(data) {
    try {
      const res = await axios.post('/api/user/auth/register', data)
      if (res.data.code === 200) {
        ElMessage.success('注册成功')
        return true
      }
      ElMessage.error(res.data.message || '注册失败')
      return false
    } catch (e) {
      ElMessage.error('注册失败：' + (e.response?.data?.message || e.message || '网络错误'))
      return false
    }
  }

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userRole')
    localStorage.removeItem('shopToken')
    localStorage.removeItem('shopUserInfo')
    localStorage.removeItem('shopInfo')
  }

  return { token, userInfo, cartCount, login, register, logout }
})
