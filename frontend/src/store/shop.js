import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export const useShopStore = defineStore('shop', () => {
  const token = ref(localStorage.getItem('shopToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('shopUserInfo') || '{}'))
  const shopInfo = ref(JSON.parse(localStorage.getItem('shopInfo') || '{}'))

  // 商家登录
  async function login(username, password) {
    try {
      const res = await axios.post('/api/shop/auth/login', { username, password })
      if (res.data.code === 200) {
        token.value = res.data.data.token
        userInfo.value = res.data.data.userInfo
        shopInfo.value = res.data.data.shopInfo || {}
        localStorage.setItem('shopToken', token.value)
        localStorage.setItem('shopUserInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('shopInfo', JSON.stringify(shopInfo.value))
        localStorage.setItem('userRole', '2')
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

  // 商家注册
  async function register(data) {
    try {
      const res = await axios.post('/api/shop/auth/register', data)
      if (res.data.code === 200) {
        ElMessage.success('注册成功，请等待审核')
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
    shopInfo.value = {}
    localStorage.removeItem('shopToken')
    localStorage.removeItem('shopUserInfo')
    localStorage.removeItem('shopInfo')
    localStorage.removeItem('userRole')
  }

  return { token, userInfo, shopInfo, login, register, logout }
})
