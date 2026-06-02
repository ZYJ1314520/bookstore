<template>
  <div class="book-list">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="sort-group">
        <span class="filter-label">排序：</span>
        <el-radio-group v-model="sort" @change="loadBooks">
          <el-radio-button value="sales">销量</el-radio-button>
          <el-radio-button value="price_asc">价格↑</el-radio-button>
          <el-radio-button value="price_desc">价格↓</el-radio-button>
          <el-radio-button value="new">最新</el-radio-button>
        </el-radio-group>
      </div>
      <div class="price-group">
        <span class="filter-label">价格：</span>
        <el-input v-model="priceMin" placeholder="最低" style="width: 80px" @change="loadBooks" />
        <span>-</span>
        <el-input v-model="priceMax" placeholder="最高" style="width: 80px" @change="loadBooks" />
        <el-button size="small" @click="loadBooks">筛选</el-button>
      </div>
    </div>

    <!-- 搜索历史 -->
    <div class="search-history" v-if="searchHistory.length > 0 && !keyword">
      <div class="history-header">
        <span>搜索历史</span>
        <el-button link type="primary" @click="clearHistory">清空</el-button>
      </div>
      <div class="history-tags">
        <el-tag v-for="item in searchHistory" :key="item" @click="searchKeyword(item)" style="cursor: pointer">{{ item }}</el-tag>
      </div>
    </div>

    <!-- 热门搜索 -->
    <div class="hot-search">
      <span class="hot-label">🔥 热门：</span>
      <el-tag v-for="item in hotKeywords" :key="item" type="danger" @click="searchKeyword(item)" style="cursor: pointer">{{ item }}</el-tag>
    </div>

    <!-- 图书列表 -->
    <div class="book-grid">
      <div v-for="book in books" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
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

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadBooks"
      />
    </div>

    <!-- 空状态 -->
    <el-empty v-if="books.length === 0" description="暂无图书" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/api'

const route = useRoute()

const books = ref([])
const total = ref(0)
const page = ref(1)
const sort = ref('sales')
const keyword = ref('')
const categoryId = ref(null)
const priceMin = ref('')
const priceMax = ref('')
const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory') || '[]'))
const hotKeywords = ref(['三体', '活着', 'JavaScript', '高等数学', '经济学'])

onMounted(() => {
  keyword.value = route.query.keyword || ''
  categoryId.value = route.query.categoryId || null
  loadBooks()
})

watch(() => route.query, (newQuery) => {
  keyword.value = newQuery.keyword || ''
  categoryId.value = newQuery.categoryId || null
  page.value = 1
  loadBooks()
})

const loadBooks = async () => {
  const params = {
    page: page.value,
    size: 10,
    sort: sort.value
  }
  if (keyword.value) {
    params.keyword = keyword.value
    // 保存搜索历史
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
  searchHistory.value = history
}

const clearHistory = () => {
  localStorage.removeItem('searchHistory')
  searchHistory.value = []
}

const searchKeyword = (kw) => {
  keyword.value = kw
  loadBooks()
}
</script>

<style scoped>
.filter-bar {
  margin-bottom: 20px;
  padding: 15px;
  background: white;
  border-radius: 8px;
  display: flex;
  gap: 30px;
  align-items: center;
}
.filter-label {
  color: #666;
  margin-right: 8px;
}
.sort-group, .price-group {
  display: flex;
  align-items: center;
}
.search-history, .hot-search {
  margin-bottom: 15px;
  padding: 10px 15px;
  background: white;
  border-radius: 8px;
}
.history-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  color: #666;
}
.history-tags, .hot-search {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.hot-label {
  margin-right: 8px;
}
.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}
.book-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.3s;
}
.book-card:hover {
  transform: translateY(-5px);
}
.book-cover {
  height: 200px;
  overflow: hidden;
}
.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.book-info {
  padding: 15px;
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
  margin-bottom: 10px;
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
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
