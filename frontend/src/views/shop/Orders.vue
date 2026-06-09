<template>
  <div class="shop-orders">
    <h2>订单管理</h2>

    <!-- 状态筛选 -->
    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>

    <!-- 订单列表 -->
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" width="180" />
      <el-table-column prop="receiverName" label="收件人" width="100" />
      <el-table-column prop="receiverPhone" label="联系电话" width="120" />
      <el-table-column label="金额" width="100">
        <template #default="{ row }">
          <span style="color: #e4393c;">¥{{ row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" width="180">
        <template #default="{ row }">
          {{ row.createTime || row.payTime || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="primary" size="small" @click="shipOrder(row.id)">
            发货
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
        @current-change="loadOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const activeTab = ref('all')
const orders = ref([])
const total = ref(0)
const page = ref(1)

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  const params = { page: page.value, size: 10 }
  if (activeTab.value !== 'all') {
    params.status = parseInt(activeTab.value)
  }
  const res = await request.get('/api/shop/orders', { params })
  if (res.data.code === 200) {
    orders.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const statusText = (status) => {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

const shipOrder = async (orderId) => {
  await ElMessageBox.confirm('确认发货？', '提示')
  await request.put(`/api/shop/orders/${orderId}/ship`)
  ElMessage.success('发货成功')
  loadOrders()
}
</script>

<style scoped>
.shop-orders {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
