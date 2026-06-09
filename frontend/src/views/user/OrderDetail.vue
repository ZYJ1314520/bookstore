<template>
  <div class="order-detail" v-if="order">
    <div class="page-header">
      <h2>订单详情</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <!-- 订单状态 -->
    <div class="status-bar">
      <span class="status-text">订单状态：</span>
      <el-tag :type="statusType(order.status)" size="large">{{ statusText(order.status) }}</el-tag>
    </div>

    <!-- 收货信息 -->
    <div class="section">
      <h3>收货信息</h3>
      <p><strong>{{ order.receiverName }}</strong> {{ order.receiverPhone }}</p>
      <p>{{ order.receiverAddress }}</p>
    </div>

    <!-- 商品列表 -->
    <div class="section">
      <h3>商品信息</h3>
      <el-table :data="orderItems" style="width: 100%">
        <el-table-column label="商品" min-width="300">
          <template #default="{ row }">
            <div class="book-info">
              <img :src="imgUrl(row.bookCover) || '/default-cover.jpg'" class="book-cover">
              <div>
                <p>{{ row.bookName }}</p>
                <p class="shop-name" v-if="row.shopName">书店：{{ row.shopName }}</p>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="80" prop="quantity" />
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div class="total-bar">
        订单总额：<span class="price">¥{{ order.totalAmount }}</span>
      </div>
    </div>

    <!-- 订单信息 -->
    <div class="section">
      <h3>订单信息</h3>
      <p>订单编号：{{ order.orderNo }}</p>
      <p>下单时间：{{ order.createTime }}</p>
      <p v-if="order.payTime">支付时间：{{ order.payTime }}</p>
      <p v-if="order.shipTime">发货时间：{{ order.shipTime }}</p>
      <p v-if="order.receiveTime">收货时间：{{ order.receiveTime }}</p>
    </div>

    <!-- 操作按钮 -->
    <div class="actions">
      <el-button v-if="order.status === 0" type="primary" size="large" @click="payOrder">去支付</el-button>
      <el-button v-if="order.status === 2" type="success" size="large" @click="receiveOrder">确认收货</el-button>
      <el-button v-if="order.status === 0" type="danger" size="large" @click="cancelOrder">取消订单</el-button>
      <el-button v-if="order.status === 3" type="warning" size="large" @click="goReview">去评价</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request, { imgUrl } from '@/api'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const orderItems = ref([])

onMounted(async () => {
  const id = route.params.id
  const [orderRes, itemsRes] = await Promise.all([
    request.get(`/api/user/orders/${id}`),
    request.get(`/api/user/orders/${id}/items`)
  ])
  if (orderRes.data.code === 200) order.value = orderRes.data.data
  if (itemsRes.data.code === 200) orderItems.value = itemsRes.data.data
})

const statusText = (status) => {
  const map = { 0: '待付款', 1: '待发货', 2: '已发货', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const statusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

const payOrder = async () => {
  try {
    await ElMessageBox.confirm('确认支付？', '提示')
    const res = await request.put(`/api/user/orders/${order.value.id}/pay`)
    if (res.data.code === 200) {
      ElMessage.success('支付成功')
      order.value.status = 1
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '支付失败')
  }
}

const receiveOrder = async () => {
  try {
    await ElMessageBox.confirm('确认已收到货物？', '提示')
    const res = await request.put(`/api/user/orders/${order.value.id}/receive`)
    if (res.data.code === 200) {
      ElMessage.success('已确认收货')
      order.value.status = 3
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确认取消订单？', '提示')
    const res = await request.post(`/api/user/orders/${order.value.id}/cancel`)
    if (res.data.code === 200) {
      ElMessage.success('订单已取消')
      order.value.status = 4
    }
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

const goReview = () => {
  router.push({ path: '/my/reviews', query: { orderId: order.value.id } })
}
</script>

<style scoped>
.order-detail {
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
.status-bar {
  background: #f5f7ff;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.status-text {
  color: #666;
}
.section {
  margin-bottom: 25px;
}
.section h3 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.book-info {
  display: flex;
  align-items: center;
  gap: 10px;
}
.book-cover {
  width: 50px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.shop-name {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}
.price {
  color: #e4393c;
  font-weight: bold;
}
.total-bar {
  text-align: right;
  padding: 15px 0;
  font-size: 16px;
  border-top: 1px solid #eee;
  margin-top: 10px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}
</style>
