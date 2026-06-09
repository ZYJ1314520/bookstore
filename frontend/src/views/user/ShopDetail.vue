<template>
  <div class="shop-detail" v-if="shop">
    <!-- 店铺信息头部 -->
    <div class="shop-header">
      <div class="shop-logo-wrap">
        <img v-if="shop.logo" :src="imgUrl(shop.logo)" class="shop-logo" />
        <div v-else class="logo-fallback">
          <span>{{ shop.shopName }}</span>
        </div>
      </div>
      <div class="shop-text">
        <h1 class="shop-name">{{ shop.shopName }}</h1>
        <p class="shop-desc" v-if="shop.description">{{ shop.description }}</p>
        <p class="shop-contact" v-if="shop.contactPhone">
          <el-icon><Phone /></el-icon> {{ shop.contactPhone }}
        </p>
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
            <img v-if="book.cover" :src="imgUrl(book.cover)" :alt="book.title">
            <div v-else class="cover-fallback">
              <span>{{ book.title }}</span>
              <small>{{ book.author }}</small>
            </div>
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
import request, { imgUrl } from '@/api'

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
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 24px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  padding: 30px;
}

.shop-logo-wrap {
  width: 100px;
  height: 100px;
  border-radius: var(--app-radius);
  overflow: hidden;
  flex-shrink: 0;
  border: 1px solid var(--app-border);
}

.shop-logo {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px;
  color: #f6f2e9;
  background:
    linear-gradient(160deg, rgba(255,255,255,0.18), transparent 42%),
    #151515;
  font-size: 20px;
  font-weight: 650;
  line-height: 1.2;
  text-align: center;
}

.shop-text {
  flex: 1;
}

.shop-name {
  font-size: 28px;
  font-weight: 640;
  color: var(--app-text);
  margin-bottom: 10px;
}

.shop-desc {
  color: var(--app-text-muted);
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.6;
}

.shop-contact {
  color: var(--app-text-muted);
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.shop-books {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  padding: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--app-border);
}

.section-header h2 {
  font-size: 20px;
  font-weight: 640;
  color: var(--app-text);
}

.books-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.book-card {
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  background: var(--app-surface);
}

.book-card:hover {
  box-shadow: var(--app-shadow);
  transform: translateY(-2px);
}

.book-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: var(--app-surface-muted);
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
  color: #f6f2e9;
  background:
    linear-gradient(160deg, rgba(255,255,255,0.18), transparent 42%),
    #151515;
}

.cover-fallback span {
  font-size: 16px;
  line-height: 1.3;
  font-weight: 650;
}

.cover-fallback small {
  color: rgba(255,255,255,0.72);
  font-size: 12px;
}

.book-info {
  padding: 14px;
}

.book-title {
  font-size: 14px;
  color: var(--app-text);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: 600;
}

.book-author {
  font-size: 12px;
  color: var(--app-text-muted);
  margin-bottom: 10px;
}

.book-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 18px;
  color: var(--app-text);
  font-weight: 700;
}

.original-price {
  font-size: 12px;
  color: var(--app-text-muted);
  text-decoration: line-through;
}

.book-sales {
  font-size: 12px;
  color: var(--app-text-muted);
  margin-top: 6px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
