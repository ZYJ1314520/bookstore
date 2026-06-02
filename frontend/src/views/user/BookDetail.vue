<template>
  <div class="book-detail" v-if="book">
    <div class="book-main">
      <div class="book-cover">
        <img :src="book.cover || '/default-cover.jpg'" :alt="book.title">
      </div>
      <div class="book-info">
        <h1 class="book-title">{{ book.title }}</h1>
        <p class="book-author">作者：{{ book.author }}</p>
        <p class="book-shop" v-if="book.shopName">
          店铺：<router-link :to="`/shop/${book.shopId}`" class="shop-name">{{ book.shopName }}</router-link>
        </p>
        <p class="book-isbn">ISBN：{{ book.isbn }}</p>
        <div class="book-price-box">
          <span class="price">¥{{ book.price }}</span>
          <span class="original-price" v-if="book.originalPrice">¥{{ book.originalPrice }}</span>
          <span class="discount" v-if="book.originalPrice">{{ (book.price / book.originalPrice * 10).toFixed(1) }}折</span>
        </div>
        <p class="book-stock">库存：{{ book.stock > 0 ? '有货' : '缺货' }}</p>
        <p class="book-sales">销量：{{ book.sales }}</p>
        <div class="quantity-box">
          <span>数量：</span>
          <el-input-number v-model="quantity" :min="1" :max="book.stock" />
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="addToCart">加入购物车</el-button>
          <el-button type="danger" @click="buyNow">立即购买</el-button>
        </div>
      </div>
    </div>

    <!-- 图书详情 -->
    <div class="book-tabs">
      <el-tabs>
        <el-tab-pane label="商品详情">
          <div class="detail-content">
            <p><strong>出版社：</strong>{{ book.publisher }}</p>
            <p><strong>出版日期：</strong>{{ book.publishDate }}</p>
            <p><strong>简介：</strong>{{ book.description }}</p>
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
    </div>
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
    // 新接口返回 { book: {...}, shopName: "..." }
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
.book-main {
  display: flex;
  gap: 40px;
  background: white;
  padding: 30px;
  border-radius: 8px;
  margin-bottom: 20px;
}
.book-cover {
  width: 300px;
  height: 400px;
  flex-shrink: 0;
}
.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}
.book-info {
  flex: 1;
}
.book-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
}
.book-author, .book-isbn {
  color: #666;
  margin-bottom: 10px;
}
.book-shop {
  color: #666;
  margin-bottom: 10px;
}
.shop-name {
  color: #667eea;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
}
.shop-name:hover {
  text-decoration: underline;
}
.book-price-box {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}
.price {
  font-size: 28px;
  color: #e4393c;
  font-weight: bold;
}
.original-price {
  font-size: 16px;
  color: #999;
  text-decoration: line-through;
  margin-left: 10px;
}
.quantity-box {
  margin: 20px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}
.action-buttons {
  display: flex;
  gap: 15px;
}
.book-tabs {
  background: white;
  padding: 20px;
  border-radius: 8px;
}
.reviews {
  padding: 20px 0;
}
.review-item {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}
.review-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}
.review-time {
  color: #999;
  font-size: 12px;
}
.review-content {
  color: #333;
}
.review-reply {
  margin-top: 10px;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
  color: #666;
}
.detail-text {
  white-space: pre-wrap;
  line-height: 1.8;
  margin: 15px 0;
}
.detail-images {
  margin-top: 20px;
}
.detail-img {
  width: 100%;
  max-width: 800px;
  display: block;
  margin: 10px 0;
  border-radius: 4px;
}
</style>
