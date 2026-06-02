<template>
  <div class="shop-detail" v-if="shop">
    <!-- 店铺信息头部 -->
    <div class="shop-header">
      <div class="shop-info">
        <img v-if="shop.logo" :src="shop.logo" class="shop-logo" />
        <div class="shop-text">
          <h1 class="shop-name">{{ shop.shopName }}</h1>
          <p class="shop-desc" v-if="shop.description">{{ shop.description }}</p>
          <p class="shop-contact" v-if="shop.contactPhone">
            <el-icon><Phone /></el-icon> {{ shop.contactPhone }}
          </p>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="shop-books">
      <div class="section-header">
        <h2>店铺商品</h2>
        <el-select v-model="sort" placeholder="排序方式" @change="loadBooks" style="width: 140px">
          <el-option label="综合排序" value="sales" />
          <el-option label="销量最高" value="sales" />
          <el-option label="价格从低到高" value="price_asc" />
          <el-option label="价格从高到低" value="price_desc" />
          <el-option label="最新上架" value="new" />
        </el-select>
      </div>

      <div class="books-grid" v-if="books.length > 0">
        <div v-for="book in books" :key="book.id" class="book-card" @click="goToBook(book.id)">
          <div class="book-cover">
            <img :src="book.cover || '/default-cover.jpg'" :alt="book.title">
          </div>
          <div class="book-info">
            <h3 class="book-title">{{ book.title }}</h3>
            <p class="book-author">{{ book.author }}</p>
            <div class="book-price">
              <span class="price">¥{{ book.price }}</span>
              <span class="original-price" v-if="book.originalPrice">¥{{ book.originalPrice }}</span>
            </div>
            <p class="book-sales">已售 {{ book.sales }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="该店铺暂无商品" />

      <!-- 分页 -->
      <div class="pagination" v-if="total > size">
        <el-pagination
          v-model:current-page="page"
          :page-size="size"
          :total="total"
          layout="prev, pager, next"
          @current-change="loadBooks"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Phone } from '@element-plus/icons-vue'
import request from '@/api'

const route = useRoute()
const router = useRouter()

const shop = ref(null)
const books = ref([])
const sort = ref('sales')
const page = ref(1)
const size = ref(12)
const total = ref(0)

onMounted(() => {
  loadShopInfo()
  loadBooks()
})

const loadShopInfo = async () => {
  const id = route.params.id
  const res = await request.get(`/api/public/shops/${id}`)
  if (res.data.code === 200) {
    shop.value = res.data.data
  }
}

const loadBooks = async () => {
  const id = route.params.id
  const res = await request.get(`/api/public/shops/${id}/books`, {
    params: { page: page.value, size: size.value, sort: sort.value }
  })
  if (res.data.code === 200) {
    books.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const goToBook = (id) => {
  router.push(`/book/${id}`)
}
</script>

<style scoped>
.shop-detail {
  max-width: 1200px;
  margin: 0 auto;
}
.shop-header {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 20px;
}
.shop-info {
  display: flex;
  align-items: center;
  gap: 20px;
}
.shop-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}
.shop-text {
  flex: 1;
}
.shop-name {
  font-size: 24px;
  color: #333;
  margin-bottom: 8px;
}
.shop-desc {
  color: #666;
  margin-bottom: 8px;
  font-size: 14px;
}
.shop-contact {
  color: #999;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
}
.shop-books {
  background: white;
  border-radius: 8px;
  padding: 20px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.section-header h2 {
  font-size: 18px;
  color: #333;
}
.books-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.book-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}
.book-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}
.book-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}
.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.book-info {
  padding: 12px;
}
.book-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.book-author {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}
.book-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}
.price {
  font-size: 18px;
  color: #e4393c;
  font-weight: bold;
}
.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}
.book-sales {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
