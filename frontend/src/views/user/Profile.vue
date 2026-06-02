<template>
  <div class="profile-page">
    <h2>个人中心</h2>
    <el-form :model="form" label-width="80px" style="max-width: 500px">
      <el-form-item label="用户名">
        <el-input v-model="form.username" disabled />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="form.nickname" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="form.phone" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="saveProfile">保存修改</el-button>
      </el-form-item>
    </el-form>

    <el-divider />

    <h3>修改密码</h3>
    <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px" style="max-width: 500px">
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/api'

const userStore = useUserStore()

const form = ref({
  username: '',
  nickname: '',
  phone: '',
  email: ''
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

onMounted(() => {
  form.value = { ...userStore.userInfo }
})

const saveProfile = async () => {
  await request.put('/api/user/profile', form.value)
  ElMessage.success('保存成功')
  userStore.userInfo = { ...userStore.userInfo, ...form.value }
  localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
}

const changePassword = async () => {
  await passwordFormRef.value.validate()
  await request.put('/api/user/password', passwordForm.value)
  ElMessage.success('密码修改成功')
  passwordForm.value = { oldPassword: '', newPassword: '' }
}
</script>

<style scoped>
.profile-page {
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
