<template>
  <div class="admin-dashboard">
    <p class="eyebrow">Overview</p>
    <h2>数据大屏</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card green">
        <div class="stat-value">¥{{ stats.todaySales || 0 }}</div>
        <div class="stat-label">今日销售额</div>
      </div>
      <div class="stat-card blue">
        <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
        <div class="stat-label">今日订单数</div>
      </div>
      <div class="stat-card orange">
        <div class="stat-value">{{ stats.todayUsers || 0 }}</div>
        <div class="stat-label">今日新增用户</div>
      </div>
      <div class="stat-card purple">
        <div class="stat-value">{{ stats.totalShops || 0 }}</div>
        <div class="stat-label">商家总数</div>
      </div>
    </div>

    <!-- 更多统计 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
        <div class="stat-label">用户总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalBooks || 0 }}</div>
        <div class="stat-label">图书总数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
        <div class="stat-label">订单总数</div>
      </div>
      <div class="stat-card red">
        <div class="stat-value">{{ stats.pendingShops || 0 }}</div>
        <div class="stat-label">待审核商家</div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-row">
      <!-- 销售趋势 -->
      <div class="chart-section">
        <h3>销售趋势（近7天）</h3>
        <div ref="trendChartRef" style="height: 350px;"></div>
      </div>
      <!-- 分类销售占比 -->
      <div class="chart-section">
        <h3>分类销售占比</h3>
        <div ref="pieChartRef" style="height: 350px;"></div>
      </div>
    </div>

    <!-- 排行榜 -->
    <div class="charts-row">
      <!-- 热销图书排行 -->
      <div class="rank-section">
        <h3>热销图书 TOP10</h3>
        <div class="rank-list">
          <div v-for="(book, index) in hotBooks" :key="book.id" class="rank-item">
            <span class="rank-num" :class="{ top: index < 3 }">{{ index + 1 }}</span>
            <span class="rank-name">{{ book.title }}</span>
            <span class="rank-sales">已售 {{ book.sales }}</span>
          </div>
        </div>
      </div>
      <!-- 商家销售排行 -->
      <div class="rank-section">
        <h3>商家销售排行 TOP10</h3>
        <div class="rank-list">
          <div v-for="(shop, index) in shopRank" :key="shop.shopId" class="rank-item">
            <span class="rank-num" :class="{ top: index < 3 }">{{ index + 1 }}</span>
            <span class="rank-name">{{ shop.shopName }}</span>
            <span class="rank-sales">¥{{ shop.sales?.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api'
import * as echarts from 'echarts'

const stats = ref({})
const hotBooks = ref([])
const shopRank = ref([])
const trendChartRef = ref(null)
const pieChartRef = ref(null)

onMounted(async () => {
  // 获取统计数据
  const statsRes = await request.get('/api/admin/dashboard')
  if (statsRes.data.code === 200) {
    stats.value = statsRes.data.data
  }

  // 并发获取图表数据
  const [trendRes, categoryRes, hotRes, rankRes] = await Promise.all([
    request.get('/api/admin/dashboard/sales?days=7'),
    request.get('/api/admin/dashboard/category-sales'),
    request.get('/api/admin/dashboard/hot-books?limit=10'),
    request.get('/api/admin/dashboard/shop-rank?limit=10')
  ])

  if (trendRes.data.code === 200) initTrendChart(trendRes.data.data)
  if (categoryRes.data.code === 200) initPieChart(categoryRes.data.data)
  if (hotRes.data.code === 200) hotBooks.value = hotRes.data.data
  if (rankRes.data.code === 200) shopRank.value = rankRes.data.data
})

const initTrendChart = (data) => {
  const chart = echarts.init(trendChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['销售额', '订单量'] },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: [
      { type: 'value', name: '销售额' },
      { type: 'value', name: '订单量' }
    ],
    series: [
      {
        name: '销售额',
        type: 'bar',
        data: data.map(item => item.sales),
        itemStyle: { color: '#111111' }
      },
      {
        name: '订单量',
        type: 'line',
        yAxisIndex: 1,
        data: data.map(item => item.orders),
        itemStyle: { color: '#77776f' }
      }
    ]
  }
  chart.setOption(option)
}

const initPieChart = (data) => {
  const chart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        data: data.map(item => ({
          name: item.categoryName,
          value: item.sales?.toFixed(2) || 0
        }))
      }
    ]
  }
  chart.setOption(option)
}
</script>

<style scoped>
.admin-dashboard {
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  padding: 30px;
  border-radius: var(--app-radius);
}
.eyebrow {
  color: var(--app-text-muted);
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 10px;
  text-transform: uppercase;
}
h2 {
  margin-bottom: 24px;
  color: var(--app-text);
  font-size: 32px;
  font-weight: 640;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.stat-card {
  background: var(--app-bg);
  border: 1px solid var(--app-border);
  padding: 26px;
  border-radius: var(--app-radius);
}
.stat-card.green,
.stat-card.blue,
.stat-card.orange,
.stat-card.purple,
.stat-card.red {
  background: var(--app-bg);
  color: var(--app-text);
}
.stat-value {
  font-size: 32px;
  font-weight: 720;
  margin-bottom: 10px;
}
.stat-label {
  font-size: 14px;
  color: var(--app-text-muted);
}
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}
.chart-section, .rank-section {
  background: var(--app-bg);
  border: 1px solid var(--app-border);
  padding: 20px;
  border-radius: var(--app-radius);
}
.chart-section h3, .rank-section h3 {
  margin-bottom: 15px;
  font-size: 16px;
}
.rank-list {
  max-height: 320px;
  overflow-y: auto;
}
.rank-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid var(--app-border);
}
.rank-num {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background: var(--app-surface);
  border: 1px solid var(--app-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  margin-right: 12px;
  flex-shrink: 0;
}
.rank-num.top {
  background: var(--app-text);
  color: var(--app-surface);
  border-color: var(--app-text);
}
.rank-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rank-sales {
  color: var(--app-text);
  font-weight: 700;
  margin-left: 10px;
}

@media (max-width: 960px) {
  .stat-cards,
  .charts-row {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 620px) {
  .stat-cards,
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>
