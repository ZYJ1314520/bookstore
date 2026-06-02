<template>
  <div class="book-list">
    <section class="list-hero">
      <div>
        <p class="eyebrow">{{ keyword ? 'Search results' : (currentCategoryName ? 'Category' : 'Library') }}</p>
        <h1>{{ keyword ? `搜索：${keyword}` : (currentCategoryName || '全部图书') }}</h1>
      </div>
      <p class="result-count" v-if="total > 0">共 {{ total }} 本</p>
    </section>

    <section class="toolbar">
      <div class="sort-group">
        <span class="filter-label">排序</span>
        <el-radio-group v-model="sort" @change="loadBooks">
          <el-radio-button value="sales">销量</el-radio-button>
          <el-radio-button value="price_asc">价格低</el-radio-button>
          <el-radio-button value="price_desc">价格高</el-radio-button>
          <el-radio-button value="new">最新</el-radio-button>
        </el-radio-group>
      </div>
      <div class="price-group">
        <span class="filter-label">价格</span>
        <el-input v-model="priceMin" placeholder="最低" class="price-input" @change="loadBooks" />
        <span class="range-line">-</span>
        <el-input v-model="priceMax" placeholder="最高" class="price-input" @change="loadBooks" />
        <el-button @click="loadBooks">筛选</el-button>
      </div>
    </section>

    <div class="book-grid" v-if="books.length > 0">
      <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
        <div class="book-cover">
          <img v-if="book.cover" :src="book.cover" :alt="book.title">
          <div v-else class="cover-fallback">
            <span>{{ book.title }}</span>
          </div>
        </div>
        <div class="book-info">
          <h3 class="book-title">{{ book.title }}</h3>
          <p class="book-author">{{ book.author }}</p>
          <div class="book-price">
            <span class="price">¥{{ formatPrice(book.price) }}</span>
            <span class="original-price" v-if="book.originalPrice">¥{{ book.originalPrice }}</span>
          </div>
          <p class="book-sales">已售 {{ book.sales || 0 }}</p>
        </div>
      </div>
    </div>

    <div class="pagination" v-if="total > 0">
      <el-pagination
        :current-page="page"
        :page-size="pageSize"
        :total="total"
        :pager-count="5"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[60, 120, 240]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>

    <el-empty v-if="books.length === 0" description="暂无图书" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/api'

const route = useRoute()

const books = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(120)
const sort = ref('sales')
const keyword = ref('')
const categoryId = ref(null)
const priceMin = ref('')
const priceMax = ref('')
const categories = ref([])

const currentCategoryName = computed(() => {
  if (!categoryId.value) return ''
  const cat = categories.value.find(c => String(c.id) === String(categoryId.value))
  return cat ? cat.name : ''
})

onMounted(async () => {
  keyword.value = route.query.keyword || ''
  categoryId.value = route.query.categoryId || null
  const catRes = await request.get('/api/public/categories')
  if (catRes.data.code === 200) categories.value = catRes.data.data
  loadBooks()
})

watch(() => route.query, (newQuery) => {
  keyword.value = newQuery.keyword || ''
  categoryId.value = newQuery.categoryId || null
  page.value = 1
  loadBooks()
})

const handlePageChange = (newPage) => {
  page.value = newPage
  loadBooks()
}

const handleSizeChange = (newSize) => {
  pageSize.value = newSize
  page.value = 1
  loadBooks()
}

const loadBooks = async () => {
  const params = {
    page: page.value,
    size: pageSize.value,
    sort: sort.value
  }
  if (keyword.value) {
    params.keyword = keyword.value
    saveHistory(keyword.value)
  }
  if (categoryId.value) params.categoryId = categoryId.value
  if (priceMin.value) params.priceMin = priceMin.value
  if (priceMax.value) params.priceMax = priceMax.value

  const res = await request.get('/api/user/books', { params })
  if (res.data.code === 200) {
    books.value = res.data.data.records
    total.value = res.data.data.total
  }
}

const saveHistory = (kw) => {
  let history = JSON.parse(localStorage.getItem('searchHistory') || '[]')
  history = history.filter(item => item !== kw)
  history.unshift(kw)
  if (history.length > 10) history = history.slice(0, 10)
  localStorage.setItem('searchHistory', JSON.stringify(history))
}

const formatPrice = (price) => {
  if (price === undefined || price === null || price === '') return '--'
  return price
}
</script>

<style scoped>
.book-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.list-hero {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  align-items: end;
  border-bottom: 1px solid var(--app-border);
  padding: 14px 0 34px;
}

.eyebrow {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 12px;
  text-transform: uppercase;
}

.list-hero h1 {
  font-size: clamp(34px, 5vw, 68px);
  line-height: 1.05;
  font-weight: 640;
}

.result-count {
  color: var(--app-text-muted);
  white-space: nowrap;
}

.toolbar {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  padding: 16px;
}

.toolbar {
  display: flex;
  gap: 26px;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
}

.filter-label {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-right: 10px;
}

.sort-group,
.price-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.price-input {
  width: 92px;
}

.range-line {
  color: var(--app-text-muted);
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 18px;
}

.book-card {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.2s ease, transform 0.2s ease;
}

.book-card:hover {
  border-color: var(--app-border-strong);
  transform: translateY(-3px);
}

.book-cover {
  aspect-ratio: 3 / 4;
  background: var(--app-surface-muted);
  overflow: hidden;
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
  align-items: flex-start;
  padding: 18px;
  color: #f6f2e9;
  background:
    linear-gradient(160deg, rgba(255,255,255,0.18), transparent 42%),
    #151515;
}

.cover-fallback span {
  font-size: 17px;
  line-height: 1.25;
  font-weight: 650;
}

.book-info {
  padding: 15px 16px 18px;
}

.book-title {
  font-size: 15px;
  color: var(--app-text);
  margin-bottom: 7px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 13px;
  color: var(--app-text-muted);
  margin-bottom: 12px;
}

.book-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 17px;
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
  margin-top: 5px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  padding: 20px;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
}

@media (max-width: 560px) {
  .list-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .book-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
