<template>
  <div class="dashboard">
    <h2>商家工作台</h2>

    <!-- 统计卡片 -->
    <div class="stat-cards">
      <div class="stat-card">
        <div class="stat-value">¥{{ stats.todaySales || 0 }}</div>
        <div class="stat-label">今日销售额</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.todayOrders || 0 }}</div>
        <div class="stat-label">今日订单数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.pendingOrders || 0 }}</div>
        <div class="stat-label">待发货订单</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ stats.totalBooks || 0 }}</div>
        <div class="stat-label">图书总数</div>
      </div>
    </div>

    <!-- 销售趋势 -->
    <div class="chart-section">
      <h3>销售趋势（近7天）</h3>
      <div ref="chartRef" style="height: 300px;"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api'
import * as echarts from 'echarts'

const stats = ref({})
const chartRef = ref(null)

onMounted(async () => {
  // 获取统计数据
  const statsRes = await request.get('/api/shop/dashboard')
  if (statsRes.data.code === 200) {
    stats.value = statsRes.data.data
  }

  // 获取销售趋势
  const trendRes = await request.get('/api/shop/dashboard/chart')
  if (trendRes.data.code === 200) {
    initChart(trendRes.data.data)
  }
})

const initChart = (data) => {
  const chart = echarts.init(chartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '销售额',
        type: 'line',
        data: data.map(item => item.sales),
        smooth: true,
        areaStyle: {}
      }
    ]
  }
  chart.setOption(option)
}
</script>

<style scoped>
.dashboard {
  background: white;
  padding: 30px;
  border-radius: 8px;
}
h2 {
  margin-bottom: 20px;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 10px;
  text-align: center;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}
.stat-label {
  font-size: 14px;
  opacity: 0.9;
}
.chart-section {
  margin-top: 30px;
}
.chart-section h3 {
  margin-bottom: 20px;
}
</style>
