<template>
  <div class="admin-books">
    <h2>图书管理</h2>

    <!-- 搜索 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索书名" clearable style="width: 200px" @keyup.enter="loadBooks" />
      <el-button @click="loadBooks">搜索</el-button>
    </div>

    <!-- 图书列表 -->
    <el-table :data="books" style="width: 100%">
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img :src="row.cover || '/default-cover.jpg'" style="width: 50px; height: 60px; object-fit: cover;">
        </template>
      </el-table-column>
      <el-table-column prop="title" label="书名" />
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          <span style="color: #e4393c;">¥{{ row.price }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 1"
            type="danger"
            size="small"
            @click="updateStatus(row.id, 0)">
            下架
          </el-button>
          <el-button
            v-if="row.status === 0"
            type="success"
            size="small"
            @click="updateStatus(row.id, 1)">
            上架
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
        @current-change="loadBooks"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const books = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')

onMounted(() => {
  loadBooks()
})

const loadBooks = async () => {
  const params = { page: page.value, size: 10 }
  if (keyword.value) params.keyword = keyword.value

  const res = await request.get('/api/admin/books', { params })
  if (res.data.code === 200) {
    books.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const updateStatus = async (id, status) => {
  await request.put(`/api/admin/books/${id}/status?status=${status}`)
  ElMessage.success('状态已更新')
  loadBooks()
}
</script>

<style scoped>
.admin-books {
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
