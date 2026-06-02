<template>
  <div class="home">
    <!-- 轮播图 -->
    <div class="banner">
      <el-carousel height="300px">
        <el-carousel-item v-for="item in banners" :key="item.id">
          <div class="banner-item" :style="{ background: item.color }">
            <h3>{{ item.title }}</h3>
            <p>{{ item.desc }}</p>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- 热销图书 -->
    <section class="section">
      <h2 class="section-title">🔥 热销图书</h2>
      <div class="book-grid">
        <div v-for="book in hotBooks" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
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
    </section>

    <!-- 新书上架 -->
    <section class="section">
      <h2 class="section-title">📖 新书上架</h2>
      <div class="book-grid">
        <div v-for="book in newBooks" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
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
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api'

const hotBooks = ref([])
const newBooks = ref([])
const banners = ref([
  { id: 1, title: '📚 新书上架', desc: '海量好书，等你来读', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { id: 2, title: '🔥 热销图书', desc: '大家都在读的好书', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { id: 3, title: '💡 精品推荐', desc: '编辑精选，品质保证', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' }
])

onMounted(async () => {
  const [hotRes, newRes] = await Promise.all([
    request.get('/api/public/books/hot?limit=10'),
    request.get('/api/public/books/new?limit=10')
  ])
  if (hotRes.data.code === 200) hotBooks.value = hotRes.data.data
  if (newRes.data.code === 200) newBooks.value = newRes.data.data
})
</script>

<style scoped>
.banner {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}
.banner-item {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}
.banner-item h3 {
  font-size: 36px;
  margin-bottom: 10px;
}
.banner-item p {
  font-size: 18px;
  opacity: 0.9;
}
.section {
  margin-bottom: 40px;
}
.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
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
.book-sales {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}
</style>
