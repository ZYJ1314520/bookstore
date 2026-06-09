<template>
  <div class="home">
    <section class="hero">
      <div class="hero-copy">
        <p class="eyebrow">Curated bookstore</p>
        <h1>把值得读的书，放在更清爽的位置。</h1>
        <p class="hero-desc">从新书、畅销书到店铺精选，用更少的干扰完成浏览、比较和下单。</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="$router.push('/books')">浏览全部图书</el-button>
          <el-button size="large" @click="goLiterature">查看文学精选</el-button>
        </div>
      </div>

      <div class="hero-shelf" aria-label="精选图书">
        <div
          v-for="(book, index) in featuredBooks"
          :key="book.id || book.title"
          class="showcase-book"
          :class="'book-' + index"
          @click="book.id && $router.push('/book/' + book.id)"
        >
          <img v-if="book.cover" :src="imgUrl(book.cover)" :alt="book.title">
          <div v-else class="cover-fallback">
            <span>{{ book.title }}</span>
            <small>{{ book.author }}</small>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <div class="section-header">
        <p class="eyebrow">Popular now</p>
        <h2 class="section-title">热销图书</h2>
      </div>
      <div class="book-grid">
        <div v-for="book in hotBooks" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
          <div class="book-cover">
            <img v-if="book.cover" :src="imgUrl(book.cover)" :alt="book.title">
            <div v-else class="cover-fallback compact">
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
    </section>

    <section class="section">
      <div class="section-header">
        <p class="eyebrow">New arrivals</p>
        <h2 class="section-title">新书上架</h2>
      </div>
      <div class="book-grid">
        <div v-for="book in newBooks" :key="book.id" class="book-card" @click="$router.push('/book/' + book.id)">
          <div class="book-cover">
            <img v-if="book.cover" :src="imgUrl(book.cover)" :alt="book.title">
            <div v-else class="cover-fallback compact">
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
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request, { imgUrl } from '@/api'

const router = useRouter()
const hotBooks = ref([])
const newBooks = ref([])
const categories = ref([])
const fallbackBooks = [
  { title: '文学与生活', author: '编辑精选' },
  { title: '算法之美', author: '技术书架' },
  { title: '经济学入门', author: '通识阅读' },
  { title: '世界小说选', author: '经典馆藏' }
]

const featuredBooks = computed(() => {
  const source = [...hotBooks.value, ...newBooks.value].filter(Boolean)
  return (source.length ? source : fallbackBooks).slice(0, 4)
})

const goLiterature = () => {
  const cat = categories.value.find(c => c.name.includes('文学') || c.name.includes('小说'))
  if (cat) {
    router.push('/books?categoryId=' + cat.id)
  } else {
    router.push('/books?keyword=文学')
  }
}

const formatPrice = (price) => {
  if (price === undefined || price === null || price === '') return '--'
  return price
}

onMounted(async () => {
  const [hotRes, newRes, catRes] = await Promise.all([
    request.get('/api/public/books/hot?limit=10'),
    request.get('/api/public/books/new?limit=10'),
    request.get('/api/public/categories')
  ])
  if (hotRes.data.code === 200) hotBooks.value = hotRes.data.data
  if (newRes.data.code === 200) newBooks.value = newRes.data.data
  if (catRes.data.code === 200) categories.value = catRes.data.data
})
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 54px;
}

.hero {
  min-height: 520px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(340px, 0.88fr);
  gap: clamp(34px, 6vw, 86px);
  align-items: center;
  border-bottom: 1px solid var(--app-border);
  padding: 30px 0 58px;
}

.hero-copy {
  max-width: 760px;
}

.eyebrow {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0;
  margin-bottom: 14px;
  text-transform: uppercase;
}

.hero h1 {
  max-width: 780px;
  font-size: clamp(42px, 6.4vw, 82px);
  line-height: 1.04;
  font-weight: 650;
  letter-spacing: 0;
  margin-bottom: 24px;
}

.hero-desc {
  max-width: 560px;
  color: var(--app-text-muted);
  font-size: 18px;
  line-height: 1.7;
  margin-bottom: 32px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-shelf {
  min-height: 420px;
  position: relative;
}

.showcase-book {
  position: absolute;
  width: 46%;
  min-width: 150px;
  aspect-ratio: 3 / 4.1;
  border: 1px solid rgba(17, 17, 17, 0.16);
  border-radius: var(--app-radius);
  overflow: hidden;
  background: var(--app-surface);
  box-shadow: var(--app-shadow);
  cursor: pointer;
}

.showcase-book img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.book-0 {
  left: 6%;
  top: 0;
  z-index: 3;
}

.book-1 {
  right: 4%;
  top: 36px;
  z-index: 2;
}

.book-2 {
  left: 0;
  bottom: 18px;
  z-index: 1;
}

.book-3 {
  right: 14%;
  bottom: 0;
  z-index: 4;
}

.cover-fallback {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 24px;
  color: #f6f2e9;
  background:
    linear-gradient(160deg, rgba(255,255,255,0.18), transparent 42%),
    #151515;
}

.cover-fallback span {
  font-size: 24px;
  line-height: 1.22;
  font-weight: 650;
}

.cover-fallback small {
  color: rgba(255,255,255,0.72);
}

.cover-fallback.compact {
  padding: 18px;
}

.cover-fallback.compact span {
  font-size: 17px;
}

.section {
  padding-bottom: 14px;
}

.section-header {
  margin-bottom: 22px;
}

.section-title {
  font-size: clamp(28px, 3vw, 42px);
  font-weight: 620;
  color: var(--app-text);
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
  transform: translateY(-8px);
  box-shadow: var(--app-shadow);
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

@media (max-width: 900px) {
  .hero {
    grid-template-columns: 1fr;
    min-height: auto;
  }

  .hero-shelf {
    min-height: 340px;
  }
}

@media (max-width: 560px) {
  .hero h1 {
    font-size: 38px;
  }

  .hero-shelf {
    min-height: 280px;
  }

  .book-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
