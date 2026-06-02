<template>
  <div class="book-detail" v-if="book">
    <section class="book-main">
      <div class="book-cover">
        <img v-if="book.cover" :src="book.cover" :alt="book.title">
        <div v-else class="cover-fallback">
          <span>{{ book.title }}</span>
          <small>{{ book.author }}</small>
        </div>
      </div>
      <div class="book-info">
        <p class="eyebrow">Book detail</p>
        <h1 class="book-title">{{ book.title }}</h1>
        <div class="meta-list">
          <p>{{ book.author }}</p>
          <p v-if="book.shopName">
            店铺：<router-link :to="`/shop/${book.shopId}`" class="shop-name">{{ book.shopName }}</router-link>
          </p>
          <p>ISBN：{{ book.isbn }}</p>
        </div>

        <div class="book-price-box">
          <div>
            <span class="price">¥{{ book.price }}</span>
            <span class="original-price" v-if="book.originalPrice">¥{{ book.originalPrice }}</span>
          </div>
          <span class="discount" v-if="book.originalPrice">{{ (book.price / book.originalPrice * 10).toFixed(1) }}折</span>
        </div>

        <div class="stock-row">
          <span>{{ book.stock > 0 ? '有货' : '缺货' }}</span>
          <span>销量 {{ book.sales || 0 }}</span>
        </div>

        <div class="quantity-box">
          <span>数量</span>
          <el-input-number v-model="quantity" :min="1" :max="book.stock" />
        </div>

        <div class="action-buttons">
          <el-button type="primary" size="large" @click="addToCart">加入购物车</el-button>
          <el-button type="danger" size="large" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </section>

    <section class="book-tabs">
      <el-tabs>
        <el-tab-pane label="商品详情">
          <div class="detail-content">
            <dl>
              <div>
                <dt>出版社</dt>
                <dd>{{ book.publisher || '暂无' }}</dd>
              </div>
              <div>
                <dt>出版日期</dt>
                <dd>{{ book.publishDate || '暂无' }}</dd>
              </div>
              <div>
                <dt>简介</dt>
                <dd>{{ book.description || '暂无简介' }}</dd>
              </div>
            </dl>
            <div v-if="book.detail" class="detail-text">{{ book.detail }}</div>
            <div v-if="detailImages.length > 0" class="detail-images">
              <img v-for="img in detailImages" :key="img.id" :src="img.imageUrl" class="detail-img" />
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="用户评价">
          <div class="reviews">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <el-rate v-model="review.rating" disabled />
                <span class="review-time">{{ review.createTime }}</span>
              </div>
              <p class="review-content">{{ review.content }}</p>
              <p class="review-reply" v-if="review.reply">
                <strong>商家回复：</strong>{{ review.reply }}
              </p>
            </div>
            <el-empty v-if="reviews.length === 0" description="暂无评价" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api'

const route = useRoute()
const router = useRouter()

const book = ref(null)
const reviews = ref([])
const detailImages = ref([])
const quantity = ref(1)

onMounted(async () => {
  const id = route.params.id
  const [bookRes, reviewRes, imgRes] = await Promise.all([
    request.get(`/api/user/books/${id}`),
    request.get(`/api/user/books/${id}/reviews?page=1&size=10`),
    request.get(`/api/user/books/${id}/images`)
  ])
  if (bookRes.data.code === 200) {
    const data = bookRes.data.data
    if (data.book) {
      book.value = { ...data.book, shopName: data.shopName }
    } else {
      book.value = data
    }
  }
  if (reviewRes.data.code === 200) reviews.value = reviewRes.data.data.records
  if (imgRes.data.code === 200) detailImages.value = imgRes.data.data
})

const addToCart = async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  await request.post(`/api/user/cart?bookId=${book.value.id}&quantity=${quantity.value}`)
  ElMessage.success('已加入购物车')
}

const buyNow = () => {
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push({
    path: '/order/confirm',
    query: {
      bookId: book.value.id,
      quantity: quantity.value
    }
  })
}
</script>

<style scoped>
.book-detail {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.book-main {
  display: grid;
  grid-template-columns: minmax(280px, 420px) minmax(0, 1fr);
  gap: clamp(32px, 6vw, 82px);
  align-items: start;
  border-bottom: 1px solid var(--app-border);
  padding: 18px 0 50px;
}

.book-cover {
  width: 100%;
  aspect-ratio: 3 / 4;
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  overflow: hidden;
  background: var(--app-surface-muted);
  box-shadow: var(--app-shadow);
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
  padding: 34px;
  color: #f6f2e9;
  background:
    linear-gradient(160deg, rgba(255,255,255,0.18), transparent 42%),
    #151515;
}

.cover-fallback span {
  font-size: clamp(30px, 5vw, 54px);
  line-height: 1.12;
  font-weight: 650;
}

.cover-fallback small {
  color: rgba(255,255,255,0.72);
}

.book-info {
  padding-top: 6px;
}

.eyebrow {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 14px;
  text-transform: uppercase;
}

.book-title {
  max-width: 760px;
  font-size: clamp(36px, 5vw, 66px);
  line-height: 1.06;
  font-weight: 640;
  color: var(--app-text);
  margin-bottom: 24px;
}

.meta-list {
  display: grid;
  gap: 9px;
  color: var(--app-text-muted);
  margin-bottom: 28px;
}

.shop-name {
  color: var(--app-text);
  font-weight: 600;
  text-decoration: none;
}

.shop-name:hover {
  text-decoration: underline;
}

.book-price-box {
  border-top: 1px solid var(--app-border);
  border-bottom: 1px solid var(--app-border);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 22px 0;
  margin-bottom: 20px;
}

.price {
  font-size: 34px;
  color: var(--app-text);
  font-weight: 720;
}

.original-price {
  font-size: 16px;
  color: var(--app-text-muted);
  text-decoration: line-through;
  margin-left: 10px;
}

.discount {
  border: 1px solid var(--app-border);
  border-radius: 999px;
  padding: 6px 12px;
  color: var(--app-text-muted);
  font-size: 13px;
}

.stock-row {
  display: flex;
  gap: 18px;
  color: var(--app-text-muted);
  margin-bottom: 22px;
}

.quantity-box {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 26px;
  color: var(--app-text-muted);
}

.action-buttons {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.book-tabs {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  padding: 24px;
}

.detail-content {
  padding: 12px 0;
}

.detail-content dl {
  display: grid;
  gap: 16px;
}

.detail-content dl > div {
  display: grid;
  grid-template-columns: 110px 1fr;
  gap: 20px;
  border-bottom: 1px solid var(--app-border);
  padding-bottom: 16px;
}

.detail-content dt {
  color: var(--app-text-muted);
}

.detail-content dd {
  color: var(--app-text);
  line-height: 1.8;
}

.reviews {
  padding: 10px 0;
}

.review-item {
  padding: 18px 0;
  border-bottom: 1px solid var(--app-border);
}

.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.review-time {
  color: var(--app-text-muted);
  font-size: 12px;
}

.review-content {
  color: var(--app-text);
  line-height: 1.7;
}

.review-reply {
  margin-top: 12px;
  padding: 12px 14px;
  background: var(--app-surface-muted);
  border-radius: var(--app-radius);
  color: var(--app-text-muted);
}

.detail-text {
  white-space: pre-wrap;
  line-height: 1.8;
  margin: 22px 0;
}

.detail-images {
  margin-top: 20px;
}

.detail-img {
  width: 100%;
  max-width: 820px;
  display: block;
  margin: 12px 0;
  border-radius: var(--app-radius);
}

@media (max-width: 820px) {
  .book-main {
    grid-template-columns: 1fr;
  }

  .book-cover {
    max-width: 420px;
  }

  .detail-content dl > div {
    grid-template-columns: 1fr;
    gap: 6px;
  }
}
</style>
