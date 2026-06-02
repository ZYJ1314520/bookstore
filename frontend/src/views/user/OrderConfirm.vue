<template>
  <div class="order-confirm">
    <h2>确认订单</h2>

    <!-- 收货地址 -->
    <div class="section">
      <h3>收货地址</h3>
      <div class="address-list">
        <div v-for="addr in addresses" :key="addr.id"
             class="address-item" :class="{ active: selectedAddress === addr.id }"
             @click="selectedAddress = addr.id">
          <p><strong>{{ addr.receiverName }}</strong> {{ addr.phone }}</p>
          <p>{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}</p>
        </div>
      </div>
      <el-empty v-if="addresses.length === 0" description="暂无收货地址">
        <el-button type="primary" @click="$router.push('/my/address')">添加地址</el-button>
      </el-empty>
    </div>

    <!-- 商品信息 -->
    <div class="section">
      <h3>商品信息</h3>
      <el-table :data="orderItems" style="width: 100%">
        <el-table-column label="商品" width="400">
          <template #default="{ row }">
            <div class="cart-item">
              <img :src="row.bookCover || '/default-cover.jpg'" class="item-cover">
              <span>{{ row.bookName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数量" width="100">
          <template #default="{ row }">
            {{ row.quantity }}
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 提交订单 -->
    <div class="submit-bar">
      <div class="total">
        共 <span>{{ totalCount }}</span> 件，合计：<span class="price">¥{{ totalPrice }}</span>
      </div>
      <el-button type="primary" size="large" @click="submitOrder" :loading="loading">
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api'

const router = useRouter()
const route = useRoute()

const addresses = ref([])
const selectedAddress = ref(null)
const orderItems = ref([])
const loading = ref(false)

onMounted(async () => {
  // 获取地址列表
  const addrRes = await request.get('/api/user/addresses')
  if (addrRes.data.code === 200) {
    addresses.value = addrRes.data.data
    // 选择默认地址
    const defaultAddr = addresses.value.find(a => a.isDefault === 1)
    if (defaultAddr) selectedAddress.value = defaultAddr.id
    else if (addresses.value.length > 0) selectedAddress.value = addresses.value[0].id
  }

  // 获取订单商品
  if (route.query.bookId) {
    // 直接购买
    const bookRes = await request.get(`/api/user/books/${route.query.bookId}`)
    if (bookRes.data.code === 200) {
      const book = bookRes.data.data
      orderItems.value = [{
        bookId: book.id,
        bookName: book.title,
        bookCover: book.cover,
        price: book.price,
        quantity: parseInt(route.query.quantity) || 1
      }]
    }
  } else {
    // 从购物车结算
    const items = JSON.parse(sessionStorage.getItem('checkoutItems') || '[]')
    orderItems.value = items
  }
})

const totalCount = computed(() => {
  return orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
})

const totalPrice = computed(() => {
  return orderItems.value
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

const submitOrder = async () => {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  if (orderItems.value.length === 0) {
    ElMessage.warning('请选择商品')
    return
  }

  loading.value = true
  try {
    const res = await request.post('/api/user/orders', {
      addressId: selectedAddress.value,
      items: orderItems.value.map(item => ({
        bookId: item.bookId,
        quantity: item.quantity
      }))
    })
    if (res.data.code === 200) {
      ElMessage.success('订单创建成功')
      sessionStorage.removeItem('checkoutItems')
      router.push('/my/orders')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.order-confirm {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.section {
  margin-bottom: 30px;
}
.section h3 {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.address-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}
.address-item {
  padding: 15px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
}
.address-item.active {
  border-color: #667eea;
  background: #f5f7ff;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.item-cover {
  width: 50px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
}
.price {
  color: #e4393c;
  font-weight: bold;
}
.submit-bar {
  margin-top: 20px;
  padding: 20px;
  background: #f5f5f5;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}
</style>
