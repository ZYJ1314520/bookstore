<template>
  <div class="address-page">
    <div class="page-header">
      <h2>收货地址</h2>
      <el-button type="primary" @click="showDialog()">新增地址</el-button>
    </div>

    <div class="address-list">
      <div v-for="addr in addresses" :key="addr.id" class="address-card">
        <div class="address-info">
          <p><strong>{{ addr.receiverName }}</strong> {{ addr.phone }}</p>
          <p>{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</p>
          <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
        </div>
        <div class="address-actions">
          <el-button link @click="showDialog(addr)">编辑</el-button>
          <el-button link type="danger" @click="deleteAddress(addr.id)">删除</el-button>
          <el-button v-if="addr.isDefault !== 1" link @click="setDefault(addr.id)">设为默认</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="addresses.length === 0" description="暂无收货地址" />

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑地址' : '新增地址'" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="收件人" prop="receiverName">
          <el-input v-model="form.receiverName" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="省">
          <el-input v-model="form.province" />
        </el-form-item>
        <el-form-item label="市">
          <el-input v-model="form.city" />
        </el-form-item>
        <el-form-item label="区">
          <el-input v-model="form.district" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="form.detailAddress" type="textarea" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const addresses = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = ref({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const rules = {
  receiverName: [{ required: true, message: '请输入收件人姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

onMounted(() => {
  loadAddresses()
})

const loadAddresses = async () => {
  const res = await request.get('/api/user/addresses')
  if (res.data.code === 200) {
    addresses.value = res.data.data
  }
}

const showDialog = (addr = null) => {
  if (addr) {
    editingId.value = addr.id
    form.value = { ...addr }
  } else {
    editingId.value = null
    form.value = {
      receiverName: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detailAddress: '',
      isDefault: 0
    }
  }
  dialogVisible.value = true
}

const saveAddress = async () => {
  await formRef.value.validate()
  if (editingId.value) {
    await request.put(`/api/user/addresses/${editingId.value}`, form.value)
    ElMessage.success('更新成功')
  } else {
    await request.post('/api/user/addresses', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadAddresses()
}

const deleteAddress = async (id) => {
  await ElMessageBox.confirm('确认删除该地址？', '提示')
  await request.delete(`/api/user/addresses/${id}`)
  ElMessage.success('删除成功')
  loadAddresses()
}

const setDefault = async (id) => {
  await request.put(`/api/user/addresses/${id}/default`)
  ElMessage.success('设置成功')
  loadAddresses()
}
</script>

<style scoped>
.address-page {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.address-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
}
.address-info p {
  margin-bottom: 8px;
}
.address-actions {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
</style>
