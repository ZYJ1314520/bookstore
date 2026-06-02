<template>
  <div class="admin-users">
    <h2>用户管理</h2>

    <!-- 搜索 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/昵称/手机号" clearable style="width: 300px" @keyup.enter="loadUsers" />
      <el-button @click="loadUsers">搜索</el-button>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="nickname" label="昵称" width="120" />
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button
            :type="row.status === 1 ? 'danger' : 'success'"
            size="small"
            @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadUsers"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const users = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')

onMounted(() => {
  loadUsers()
})

const loadUsers = async () => {
  const params = { page: page.value, size: 10 }
  if (keyword.value) params.keyword = keyword.value

  const res = await request.get('/api/admin/users', { params })
  if (res.data.code === 200) {
    users.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const toggleStatus = async (user) => {
  const newStatus = user.status === 1 ? 0 : 1
  await request.put(`/api/admin/users/${user.id}/status?status=${newStatus}`)
  ElMessage.success('状态已更新')
  loadUsers()
}
</script>

<style scoped>
.admin-users {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.filter-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
