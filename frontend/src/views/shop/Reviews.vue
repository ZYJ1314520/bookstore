<template>
  <div class="shop-reviews">
    <h2>评价管理</h2>

    <!-- 筛选 -->
    <div class="filter-bar">
      <el-select v-model="ratingFilter" placeholder="评分筛选" clearable @change="loadReviews">
        <el-option label="好评(5星)" :value="5" />
        <el-option label="中评(3-4星)" :value="3" />
        <el-option label="差评(1-2星)" :value="1" />
      </el-select>
    </div>

    <!-- 评价列表 -->
    <div class="review-list">
      <div v-for="review in reviews" :key="review.id" class="review-card">
        <div class="review-header">
          <el-rate v-model="review.rating" disabled />
          <span class="review-time">{{ review.createTime }}</span>
        </div>
        <p class="review-content">{{ review.content }}</p>
        <div class="review-reply" v-if="review.reply">
          <strong>我的回复：</strong>{{ review.reply }}
        </div>
        <div class="review-actions" v-else>
          <el-input v-model="review.replyContent" placeholder="输入回复内容" style="width: 400px" />
          <el-button type="primary" size="small" @click="replyReview(review)">回复</el-button>
        </div>
      </div>
    </div>

    <el-empty v-if="reviews.length === 0" description="暂无评价" />

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="loadReviews"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const reviews = ref([])
const total = ref(0)
const page = ref(1)
const ratingFilter = ref(null)

onMounted(() => {
  loadReviews()
})

const loadReviews = async () => {
  const params = { page: page.value, size: 10 }
  if (ratingFilter.value !== null) {
    params.rating = ratingFilter.value
  }
  const res = await request.get('/api/shop/reviews', { params })
  if (res.data.code === 200) {
    reviews.value = res.data.data.records.map(r => ({ ...r, replyContent: '' }))
    total.value = res.data.data.total
  }
}

const replyReview = async (review) => {
  if (!review.replyContent) {
    ElMessage.warning('请输入回复内容')
    return
  }
  await request.post(`/api/shop/reviews/${review.id}/reply?reply=${review.replyContent}`)
  ElMessage.success('回复成功')
  loadReviews()
}
</script>

<style scoped>
.shop-reviews {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.filter-bar {
  margin-bottom: 20px;
}
.review-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 15px;
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
  margin-bottom: 10px;
}
.review-reply {
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
  color: #666;
}
.review-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
