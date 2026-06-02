<template>
  <div class="login-container">
    <div class="login-box">
      <p class="auth-kicker">Admin console</p>
      <h2>登录管理后台</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/login">用户登录</router-link>
        <router-link to="/shop/login">商家登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminStore } from '@/store/admin'

const router = useRouter()
const adminStore = useAdminStore()

const formRef = ref()
const loading = ref(false)
const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const success = await adminStore.login(form.value.username, form.value.password)
    if (success) {
      router.push('/admin')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background:
    linear-gradient(90deg, rgba(17,17,17,0.04) 1px, transparent 1px),
    linear-gradient(0deg, rgba(17,17,17,0.04) 1px, transparent 1px),
    var(--app-bg);
  background-size: 44px 44px;
  padding: 24px;
}
.login-box {
  width: min(420px, 100%);
  padding: 38px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  box-shadow: var(--app-shadow);
}
.auth-kicker {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 10px;
  text-transform: uppercase;
}
.login-box h2 {
  margin-bottom: 28px;
  color: var(--app-text);
  font-size: 30px;
  font-weight: 640;
}
.links {
  display: flex;
  justify-content: center;
  gap: 14px;
  flex-wrap: wrap;
  margin-top: 18px;
}
.links a {
  color: var(--app-text-muted);
  text-decoration: none;
  font-size: 14px;
}
.links a:hover {
  color: var(--app-text);
}
</style>
