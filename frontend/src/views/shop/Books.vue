<template>
  <div class="shop-books">
    <div class="page-header">
      <h2>图书管理</h2>
      <el-button type="primary" @click="$router.push('/shop/book/add')">新增图书</el-button>
    </div>

    <!-- 搜索筛选 -->
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索书名" clearable style="width: 200px" @keyup.enter="loadBooks" />
      <el-select v-model="statusFilter" placeholder="状态" clearable style="width: 120px" @change="loadBooks">
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-button @click="loadBooks">搜索</el-button>
    </div>

    <!-- 图书列表 -->
    <el-table :data="books" style="width: 100%">
      <el-table-column label="封面" width="80">
        <template #default="{ row }">
          <img :src="imgUrl(row.cover) || '/default-cover.jpg'" style="width: 50px; height: 60px; object-fit: cover;">
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
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button link @click="$router.push('/shop/book/edit/' + row.id)">编辑</el-button>
          <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button link type="danger" @click="deleteBook(row.id)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request, { imgUrl } from '@/api'

const books = ref([])
const total = ref(0)
const page = ref(1)
const keyword = ref('')
const statusFilter = ref(null)

onMounted(() => {
  loadBooks()
})

const loadBooks = async () => {
  const params = { page: page.value, size: 10 }
  if (keyword.value) params.keyword = keyword.value
  if (statusFilter.value !== null) params.status = statusFilter.value

  const res = await request.get('/api/shop/books', { params })
  if (res.data.code === 200) {
    books.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const toggleStatus = async (book) => {
  const newStatus = book.status === 1 ? 0 : 1
  await request.put(`/api/shop/books/${book.id}/status?status=${newStatus}`)
  ElMessage.success('状态已更新')
  loadBooks()
}

const deleteBook = async (id) => {
  await ElMessageBox.confirm('确认删除该图书？', '提示')
  await request.delete(`/api/shop/books/${id}`)
  ElMessage.success('删除成功')
  loadBooks()
}
</script>

<style scoped>
.shop-books {
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
