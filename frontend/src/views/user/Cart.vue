<template>
  <div class="cart-page">
    <h2>购物车</h2>
    <div class="cart-list" v-if="cartList.length > 0">
      <el-table :data="cartList" style="width: 100%">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox v-model="row.selected" />
          </template>
        </el-table-column>
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
        <el-table-column label="数量" width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" @change="updateQuantity(row)" />
          </template>
        </el-table-column>
        <el-table-column label="小计" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="danger" link @click="removeItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="cart-footer">
        <div class="total">
          已选 <span>{{ selectedCount }}</span> 件，合计：<span class="price">¥{{ totalPrice }}</span>
        </div>
        <el-button type="primary" size="large" @click="goCheckout" :disabled="selectedCount === 0">
          去结算
        </el-button>
      </div>
    </div>
    <el-empty v-else description="购物车空空如也">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api'

const router = useRouter()
const cartList = ref([])

onMounted(async () => {
  await loadCart()
})

const loadCart = async () => {
  const res = await request.get('/api/user/cart')
  if (res.data.code === 200) {
    cartList.value = res.data.data.map(item => ({
      ...item,
      selected: true,
      price: 0,
      bookName: '',
      bookCover: ''
    }))
    // 获取图书信息
    for (let item of cartList.value) {
      const bookRes = await request.get(`/api/user/books/${item.bookId}`)
      if (bookRes.data.code === 200) {
        const book = bookRes.data.data
        item.price = book.price
        item.bookName = book.title
        item.bookCover = book.cover
      }
    }
  }
}

const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).length
})

const totalPrice = computed(() => {
  return cartList.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
    .toFixed(2)
})

const updateQuantity = async (item) => {
  await request.put(`/api/user/cart/${item.id}?quantity=${item.quantity}`)
}

const removeItem = async (id) => {
  await request.delete(`/api/user/cart/${id}`)
  ElMessage.success('已删除')
  await loadCart()
}

const goCheckout = () => {
  const selected = cartList.value.filter(item => item.selected)
  if (selected.length === 0) {
    ElMessage.warning('请选择商品')
    return
  }
  // 将选中的商品存入sessionStorage
  sessionStorage.setItem('checkoutItems', JSON.stringify(selected))
  router.push('/order/confirm')
}
</script>

<style scoped>
.cart-page {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  padding: 30px;
  border-radius: var(--app-radius);
}
h2 {
  margin-bottom: 24px;
  font-size: 32px;
  font-weight: 640;
}
.cart-item {
  display: flex;
  align-items: center;
  gap: 10px;
}
.item-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--app-radius);
  border: 1px solid var(--app-border);
}
.price {
  color: var(--app-text);
  font-weight: 700;
}
.cart-footer {
  margin-top: 20px;
  padding: 20px;
  background: var(--app-bg);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 20px;
}
.total {
  font-size: 16px;
}
</style>
