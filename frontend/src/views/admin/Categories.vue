<template>
  <div class="admin-categories">
    <div class="page-header">
      <h2>分类管理</h2>
      <el-button type="primary" @click="showDialog()">新增分类</el-button>
    </div>

    <!-- 分类列表 -->
    <el-table :data="categories" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button link @click="showDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteCategory(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="400px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const categories = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()

const form = ref({
  name: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

onMounted(() => {
  loadCategories()
})

const loadCategories = async () => {
  const res = await request.get('/api/admin/categories')
  if (res.data.code === 200) {
    categories.value = res.data.data
  }
}

const showDialog = (cat = null) => {
  if (cat) {
    editingId.value = cat.id
    form.value = { ...cat }
  } else {
    editingId.value = null
    form.value = { name: '', sort: 0 }
  }
  dialogVisible.value = true
}

const saveCategory = async () => {
  await formRef.value.validate()
  if (editingId.value) {
    await request.put(`/api/admin/categories/${editingId.value}`, form.value)
    ElMessage.success('更新成功')
  } else {
    await request.post('/api/admin/categories', form.value)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  loadCategories()
}

const deleteCategory = async (id) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示')
  const res = await request.delete(`/api/admin/categories/${id}`)
  if (res.data.code === 200) {
    ElMessage.success('删除成功')
    loadCategories()
  } else {
    ElMessage.error(res.data.message || '删除失败')
  }
}
</script>

<style scoped>
.admin-categories {
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
</style>
