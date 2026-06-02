<template>
  <div class="admin-shops">
    <h2>商家管理</h2>

    <!-- 状态筛选 -->
    <div class="filter-bar">
      <el-select v-model="statusFilter" placeholder="审核状态" clearable @change="loadShops">
        <el-option label="待审核" :value="0" />
        <el-option label="正常" :value="1" />
        <el-option label="已禁用" :value="2" />
        <el-option label="已拒绝" :value="3" />
      </el-select>
    </div>

    <!-- 商家列表 -->
    <el-table :data="shops" style="width: 100%">
      <el-table-column prop="shopName" label="店铺名" width="150" />
      <el-table-column prop="contactPhone" label="联系电话" width="120" />
      <el-table-column prop="contactEmail" label="邮箱" width="150" />
      <el-table-column prop="description" label="简介" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <template v-if="row.status === 0">
            <el-button type="success" size="small" @click="auditShop(row.id, 1)">通过</el-button>
            <el-button type="danger" size="small" @click="showRejectDialog(row.id)">拒绝</el-button>
          </template>
          <template v-else-if="row.status === 1">
            <el-button type="warning" size="small" @click="auditShop(row.id, 2)">禁用</el-button>
          </template>
          <template v-else-if="row.status === 2">
            <el-button type="success" size="small" @click="auditShop(row.id, 1)">启用</el-button>
          </template>
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
        @current-change="loadShops"
      />
    </div>

    <!-- 拒绝弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const shops = ref([])
const total = ref(0)
const page = ref(1)
const statusFilter = ref(null)

const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const rejectingShopId = ref(null)

onMounted(() => {
  loadShops()
})

const loadShops = async () => {
  const params = { page: page.value, size: 10 }
  if (statusFilter.value !== null) params.status = statusFilter.value

  const res = await request.get('/api/admin/shops', { params })
  if (res.data.code === 200) {
    shops.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const statusText = (status) => {
  const map = { 0: '待审核', 1: '正常', 2: '已禁用', 3: '已拒绝' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

const auditShop = async (shopId, status) => {
  await request.put(`/api/admin/shops/${shopId}/audit?status=${status}`)
  ElMessage.success('操作成功')
  loadShops()
}

const showRejectDialog = (shopId) => {
  rejectingShopId.value = shopId
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  if (!rejectReason.value) {
    ElMessage.warning('请输入拒绝原因')
    return
  }
  await request.put(`/api/admin/shops/${rejectingShopId.value}/audit?status=3&remark=${rejectReason.value}`)
  ElMessage.success('已拒绝')
  rejectDialogVisible.value = false
  loadShops()
}
</script>

<style scoped>
.admin-shops {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.filter-bar {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
