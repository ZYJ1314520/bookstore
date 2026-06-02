import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

export const useAdminStore = defineStore('admin', () => {
  const token = ref(localStorage.getItem('adminToken') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('adminUserInfo') || '{}'))

  // 管理员登录
  async function login(username, password) {
    try {
      const res = await axios.post('/api/admin/auth/login', { username, password })
      if (res.data.code === 200) {
        token.value = res.data.data.token
        userInfo.value = res.data.data.userInfo
        localStorage.setItem('adminToken', token.value)
        localStorage.setItem('adminUserInfo', JSON.stringify(userInfo.value))
        localStorage.setItem('userRole', '0')
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

  // 退出登录
  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('adminToken')
    localStorage.removeItem('adminUserInfo')
    localStorage.removeItem('userRole')
  }

  return { token, userInfo, login, logout }
})
