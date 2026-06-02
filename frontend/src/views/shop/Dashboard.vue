<template>
  <div class="dashboard">
    <p class="eyebrow">Shop overview</p>
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
        lineStyle: { color: '#111111', width: 3 },
        itemStyle: { color: '#111111' },
        areaStyle: { color: 'rgba(17, 17, 17, 0.08)' }
      }
    ]
  }
  chart.setOption(option)
}
</script>

<style scoped>
.dashboard {
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
  margin-bottom: 30px;
}
.stat-card {
  background: var(--app-bg);
  border: 1px solid var(--app-border);
  color: var(--app-text);
  padding: 26px;
  border-radius: var(--app-radius);
}
.stat-value {
  font-size: 28px;
  font-weight: 720;
  margin-bottom: 10px;
}
.stat-label {
  font-size: 14px;
  color: var(--app-text-muted);
}
.chart-section {
  margin-top: 30px;
  background: var(--app-bg);
  border: 1px solid var(--app-border);
  border-radius: var(--app-radius);
  padding: 20px;
}
.chart-section h3 {
  margin-bottom: 20px;
}

@media (max-width: 960px) {
  .stat-cards {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 620px) {
  .stat-cards {
    grid-template-columns: 1fr;
  }
}
</style>
