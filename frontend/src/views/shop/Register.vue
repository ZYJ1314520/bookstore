<template>
  <div class="login-container">
    <div class="login-box">
      <p class="auth-kicker">Open a shop</p>
      <h2>创建商家账号</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" prefix-icon="Shop" />
        </el-form-item>
        <el-form-item prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" prefix-icon="Phone" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="店铺简介(可选)" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%">
            注册
          </el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/shop/login">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useShopStore } from '@/store/shop'

const router = useRouter()
const shopStore = useShopStore()

const formRef = ref()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  shopName: '',
  contactPhone: '',
  description: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }]
}

const handleRegister = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const success = await shopStore.register(form.value)
    if (success) {
      router.push('/shop/login')
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
  width: min(460px, 100%);
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
  text-align: center;
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
