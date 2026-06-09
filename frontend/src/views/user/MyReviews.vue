<template>
  <div class="my-reviews">
    <h2>我的评价</h2>

    <el-tabs v-model="activeTab" @tab-change="loadData">
      <el-tab-pane label="待评价" name="pending" />
      <el-tab-pane label="评价历史" name="history" />
    </el-tabs>

    <!-- 待评价列表 -->
    <div v-if="activeTab === 'pending'" class="review-list">
      <div v-for="(item, index) in pendingList" :key="index" class="review-card">
        <div class="review-item-info">
          <img v-if="item.bookCover" :src="imgUrl(item.bookCover)" class="book-cover" />
          <div class="item-detail">
            <p class="book-name">{{ item.bookName }}</p>
            <p class="shop-name" v-if="item.shopName">{{ item.shopName }}</p>
            <p class="order-no">订单号：{{ item.orderId }}</p>
          </div>
        </div>
        <el-button type="primary" @click="showReviewDialog(item)">去评价</el-button>
      </div>
      <el-empty v-if="pendingList.length === 0" description="暂无待评价订单" />
    </div>

    <!-- 评价历史 -->
    <div v-if="activeTab === 'history'" class="review-list">
      <div v-for="review in historyList" :key="review.id" class="review-card history-card">
        <div class="review-book-info" v-if="review.bookName">
          <img v-if="review.bookCover" :src="imgUrl(review.bookCover)" class="book-cover" />
          <div>
            <p class="book-name">{{ review.bookName }}</p>
            <p class="shop-name" v-if="review.shopName">{{ review.shopName }}</p>
          </div>
        </div>
        <div class="review-header">
          <el-rate v-model="review.rating" disabled />
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <p class="review-reply" v-if="review.reply">
          <strong>商家回复：</strong>{{ review.reply }}
        </p>
      </div>
      <el-empty v-if="historyList.length === 0" description="暂无评价记录" />
    </div>

    <!-- 评价弹窗 -->
    <el-dialog v-model="dialogVisible" title="发表评价" width="500px">
      <el-form :model="reviewForm" :rules="rules" ref="formRef">
        <el-form-item label="评分" prop="rating">
          <el-rate v-model="reviewForm.rating" show-score />
        </el-form-item>
        <el-form-item label="评价内容" prop="content">
          <el-input v-model="reviewForm.content" type="textarea" :rows="4" placeholder="分享您的使用体验..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request, { imgUrl } from '@/api'

const route = useRoute()

const activeTab = ref('pending')
const pendingList = ref([])
const historyList = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const reviewForm = ref({
  orderId: null,
  bookId: null,
  rating: 5,
  content: ''
})

const rules = {
  rating: [{ required: true, message: '请选择评分', trigger: 'change' }],
  content: [{ required: true, message: '请输入评价内容', trigger: 'blur' }]
}

onMounted(() => {
  if (route.query.orderId) {
    activeTab.value = 'pending'
  }
  loadData()
})

const loadData = async () => {
  if (activeTab.value === 'pending') {
    const res = await request.get('/api/user/reviews/pending')
    if (res.data.code === 200) pendingList.value = res.data.data.records
  } else {
    const res = await request.get('/api/user/reviews/history')
    if (res.data.code === 200) historyList.value = res.data.data.records
  }
}

const showReviewDialog = (item) => {
  reviewForm.value = {
    orderId: item.orderId,
    bookId: item.bookId,
    rating: 5,
    content: ''
  }
  dialogVisible.value = true
}

const submitReview = async () => {
  await formRef.value.validate()
  await request.post('/api/user/reviews', reviewForm.value)
  ElMessage.success('评价成功')
  dialogVisible.value = false
  loadData()
}
</script>

<style scoped>
.my-reviews {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.review-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.review-item-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.book-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}
.item-detail {
  flex: 1;
}
.book-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 5px;
}
.shop-name {
  color: #999;
  font-size: 12px;
  margin-bottom: 3px;
}
.order-no {
  color: #999;
  font-size: 13px;
}
.history-card {
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}
.review-book-info {
  display: flex;
  align-items: center;
  gap: 12px;
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
</style>
