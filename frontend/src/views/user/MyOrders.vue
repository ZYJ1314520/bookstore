<template>
  <div class="my-orders">
    <h2>我的订单</h2>

    <!-- 状态筛选 -->
    <el-tabs v-model="activeTab" @tab-change="loadOrders">
      <el-tab-pane label="全部" name="all" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane label="待发货" name="1" />
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
    </el-tabs>

    <!-- 订单列表 -->
    <div class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-status">{{ statusText(order.status) }}</span>
        </div>
        <div class="order-info">
          <p>收货人：{{ order.receiverName }} {{ order.receiverPhone }}</p>
          <p>地址：{{ order.receiverAddress }}</p>
          <p class="order-amount">
            合计：<span class="price">¥{{ order.totalAmount }}</span>
          </p>
        </div>
        <div class="order-actions">
          <el-button @click="$router.push('/my/order/' + order.id)">查看详情</el-button>
          <el-button v-if="order.status === 0" type="primary" @click="payOrder(order.id)">去支付</el-button>
          <el-button v-if="order.status === 2" type="success" @click="receiveOrder(order.id)">确认收货</el-button>
          <el-button v-if="order.status === 0" type="danger" @click="cancelOrder(order.id)">取消订单</el-button>
          <el-button v-if="order.status === 3" type="warning" @click="goReview(order)">去评价</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="orders.length === 0" description="暂无订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const router = useRouter()

const activeTab = ref('all')
const orders = ref([])

onMounted(() => {
  loadOrders()
})

const loadOrders = async () => {
  const params = { page: 1, size: 20 }
  if (activeTab.value !== 'all') {
    params.status = parseInt(activeTab.value)
  }
  const res = await request.get('/api/user/orders', { params })
  if (res.data.code === 200) {
    orders.value = res.data.data.records
  }
}

const statusText = (status) => {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const payOrder = async (orderId) => {
  await ElMessageBox.confirm('确认支付？', '提示')
  await request.put(`/api/user/orders/${orderId}/pay`)
  ElMessage.success('支付成功')
  loadOrders()
}

const receiveOrder = async (orderId) => {
  await ElMessageBox.confirm('确认已收到货物？', '提示')
  await request.put(`/api/user/orders/${orderId}/receive`)
  ElMessage.success('已确认收货')
  loadOrders()
}

const cancelOrder = async (orderId) => {
  await ElMessageBox.confirm('确认取消订单？', '提示')
  await request.post(`/api/user/orders/${orderId}/cancel`)
  ElMessage.success('订单已取消')
  loadOrders()
}

const goReview = (order) => {
  router.push({ path: '/my/reviews', query: { orderId: order.id } })
}
</script>

<style scoped>
.my-orders {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.order-card {
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 15px;
  overflow: hidden;
}
.order-header {
  background: #f5f5f5;
  padding: 15px;
  display: flex;
  justify-content: space-between;
}
.order-no {
  color: #666;
}
.order-status {
  color: #667eea;
  font-weight: bold;
}
.order-info {
  padding: 15px;
}
.order-info p {
  margin-bottom: 8px;
  color: #666;
}
.order-amount {
  font-size: 16px;
}
.price {
  color: #e4393c;
  font-weight: bold;
  font-size: 18px;
}
.order-actions {
  padding: 15px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
