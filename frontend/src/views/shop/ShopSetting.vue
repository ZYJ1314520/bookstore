<template>
  <div class="shop-setting">
    <h2>店铺设置</h2>
    <el-form :model="form" label-width="100px" style="max-width: 600px">
      <el-form-item label="店铺名称">
        <el-input v-model="form.shopName" />
      </el-form-item>
      <el-form-item label="联系电话">
        <el-input v-model="form.contactPhone" />
      </el-form-item>
      <el-form-item label="联系邮箱">
        <el-input v-model="form.contactEmail" />
      </el-form-item>
      <el-form-item label="店铺简介">
        <el-input v-model="form.description" type="textarea" :rows="4" />
      </el-form-item>
      <el-form-item label="店铺状态">
        <el-tag :type="statusType">{{ statusText }}</el-tag>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <h3>修改密码</h3>
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 600px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="changePassword">修改密码</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { useShopStore } from '@/store/shop'
import request from '@/api'

const shopStore = useShopStore()

const form = ref({
  shopName: '',
  contactPhone: '',
  contactEmail: '',
  description: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: ''
})

const passwordFormRef = ref()

const passwordRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const statusText = computed(() => {
  const map = { 0: '审核中', 1: '正常', 2: '已禁用', 3: '已拒绝' }
  return map[shopStore.shopInfo.status] || '未知'
})

const statusType = computed(() => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'danger' }
  return map[shopStore.shopInfo.status] || 'info'
})

onMounted(() => {
  form.value = { ...shopStore.shopInfo }
})

const saveProfile = async () => {
  await request.put('/api/shop/profile', form.value)
  ElMessage.success('保存成功')
  shopStore.shopInfo = { ...shopStore.shopInfo, ...form.value }
  localStorage.setItem('shopInfo', JSON.stringify(shopStore.shopInfo))
}

const changePassword = async () => {
  await passwordFormRef.value.validate()
  await request.put('/api/shop/password', passwordForm.value)
  ElMessage.success('密码修改成功')
  passwordForm.value = { oldPassword: '', newPassword: '' }
}
</script>

<style scoped>
.shop-setting {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
h3 {
  margin-bottom: 20px;
}
</style>
