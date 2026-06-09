<template>
  <div class="my-favorites">
    <h2>我的收藏</h2>

    <div class="books-grid" v-if="books.length > 0">
      <div v-for="fav in books" :key="fav.id" class="book-card" @click="$router.push(`/book/${fav.bookId}`)">
        <div class="book-cover">
          <img v-if="fav.cover" :src="imgUrl(fav.cover)" :alt="fav.title">
          <div v-else class="cover-fallback">
            <span>{{ fav.title }}</span>
            <small>{{ fav.author }}</small>
          </div>
        </div>
        <div class="book-info">
          <h3 class="book-title">{{ fav.title }}</h3>
          <p class="book-author">{{ fav.author }}</p>
          <div class="book-bottom">
            <span class="price">¥{{ fav.price }}</span>
            <el-button type="danger" link size="small" @click.stop="removeFavorite(fav)">取消收藏</el-button>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="还没有收藏的图书">
      <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request, { imgUrl } from '@/api'

const books = ref([])

onMounted(async () => {
  await loadFavorites()
})

const loadFavorites = async () => {
  const res = await request.get('/api/user/favorites')
  if (res.data.code === 200) {
    const favs = res.data.data
    // 获取每本图书的详情
    const list = await Promise.all(favs.map(async (fav) => {
      try {
        const bookRes = await request.get(`/api/public/books/${fav.bookId}`)
        if (bookRes.data.code === 200) {
          const data = bookRes.data.data
          const book = data.book || data
          return { ...fav, title: book.title, author: book.author, cover: book.cover, price: book.price }
        }
      } catch (e) { /* ignore */ }
      return { ...fav, title: '未知图书', author: '', cover: '', price: 0 }
    }))
    books.value = list
  }
}

const removeFavorite = async (fav) => {
  await request.delete(`/api/user/favorites/${fav.id}`)
  ElMessage.success('已取消收藏')
  await loadFavorites()
}
</script>

<style scoped>
.my-favorites {
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
.book-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.price {
  font-size: 18px;
  color: var(--app-text);
  font-weight: 700;
}
</style>
