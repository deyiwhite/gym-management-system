<template>
  <section class="class-analysis">
    <div class="page-header">
      <div>
        <h2>课程运营分析</h2>
        <p>查看课程报名热度、评分表现和未评分记录。</p>
      </div>
      <el-button :loading="loading" @click="refresh">刷新数据</el-button>
    </div>

    <el-alert
      v-if="errorMsg"
      class="error-alert"
      :title="errorMsg"
      type="error"
      show-icon
      :closable="false"
    />

    <div class="summary-grid">
      <div v-for="item in summaryCards" :key="item.label" class="summary-card">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span>报名 TOP 10</span>
              <small>横向排行</small>
            </div>
          </template>
          <div ref="enrollChartRef" class="chart-box" />
          <el-empty v-if="!loading && !enrollChartRows.length" description="暂无报名数据" />
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="chart-header">
              <span>评分 TOP 5</span>
              <small>平均评分</small>
            </div>
          </template>
          <div ref="ratingChartRef" class="chart-box" />
          <el-empty v-if="!loading && !ratingChartRows.length" description="暂无评分数据" />
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { BarChart, PieChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { init, use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import api from '../api/client'

use([BarChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const loading = ref(false)
const errorMsg = ref('')
const summary = ref({
  courseTotal: 0,
  registrationTotal: 0,
  averageRating: 0,
  unratedTotal: 0
})
const rows = ref([])

const enrollChartRef = ref(null)
const ratingChartRef = ref(null)
let enrollChart = null
let ratingChart = null
let resizeObserver = null

const chartColors = ['#2563eb', '#16a34a', '#f59e0b', '#dc2626', '#7c3aed', '#0891b2', '#db2777', '#64748b', '#ea580c', '#059669']

const summaryCards = computed(() => [
  { label: '课程总数', value: numberText(summary.value.courseTotal) },
  { label: '报名总数', value: numberText(summary.value.registrationTotal) },
  { label: '平均评分', value: ratingText(summary.value.averageRating) },
  { label: '未评分记录', value: numberText(summary.value.unratedTotal) }
])

const enrollChartRows = computed(() => {
  return [...rows.value]
    .filter((row) => Number(row.enrolledCount || 0) > 0)
    .sort((a, b) => Number(b.enrolledCount || 0) - Number(a.enrolledCount || 0))
    .slice(0, 10)
})

const ratingChartRows = computed(() => {
  return [...rows.value]
    .filter((row) => row.averageRating !== null && row.averageRating !== undefined)
    .sort((a, b) => Number(b.averageRating || 0) - Number(a.averageRating || 0))
    .slice(0, 5)
})

function numberText(value) {
  return Number(value || 0)
}

function ratingText(value) {
  return Number(value || 0).toFixed(1)
}

function niceAxisMax(value) {
  const max = Number(value || 0)
  if (max <= 0) return 10
  const padded = max * 1.15
  const magnitude = 10 ** Math.floor(Math.log10(padded))
  const normalized = padded / magnitude
  const niceNormalized = normalized <= 2 ? 2 : normalized <= 5 ? 5 : 10
  return niceNormalized * magnitude
}

async function load() {
  if (loading.value) return
  loading.value = true
  errorMsg.value = ''

  try {
    const resp = await api.get('/api/class/analysis', {
      params: { page: 1, pageSize: 1000 }
    })
    const data = resp.data || {}
    summary.value = data.summary || summary.value
    rows.value = data.courseAnalysisRows || []

    await nextTick()
    renderCharts()
  } catch (error) {
    errorMsg.value = error?.response?.status === 404
      ? '课程分析接口不存在，请确认后端已启动并包含 /api/class/analysis。'
      : error?.response?.data?.message || error?.message || '课程分析数据加载失败'
    clearCharts()
  } finally {
    loading.value = false
  }
}

function buildEnrollmentData() {
  return enrollChartRows.value.map((row, index) => ({
    rank: index + 1,
    name: row.className || '未命名课程',
    value: Number(row.enrolledCount || 0)
  }))
}

function renderCharts() {
  renderEnrollChart()
  renderRatingChart()
}

function renderEnrollChart() {
  if (!enrollChartRef.value) return
  if (!enrollChart) enrollChart = init(enrollChartRef.value)

  const data = buildEnrollmentData()
  if (!data.length) {
    enrollChart.clear()
    return
  }
  const maxEnrollment = Math.max(...data.map((item) => item.value))

  enrollChart.setOption({
    color: chartColors,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter(params) {
        const item = params?.[0]
        return item ? `${item.name}<br/>报名人数：${item.value} 人` : ''
      }
    },
    grid: { left: 18, right: 28, top: 8, bottom: 8, containLabel: true },
    xAxis: {
      type: 'value',
      max: niceAxisMax(maxEnrollment),
      splitLine: { lineStyle: { color: '#edf1f7' } },
      axisLabel: { color: '#718095', formatter: '{value} 人' }
    },
    yAxis: {
      type: 'category',
      inverse: true,
      data: data.map((item) => `TOP ${item.rank}  ${item.name}`),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#263548',
        fontSize: 12,
        width: 112,
        overflow: 'truncate'
      }
    },
    series: [{
      name: '报名人数',
      type: 'bar',
      barWidth: 16,
      itemStyle: {
        borderRadius: [0, 8, 8, 0],
        color(params) {
          return chartColors[params.dataIndex % chartColors.length]
        }
      },
      label: {
        show: true,
        position: 'right',
        color: '#0d1b2f',
        fontSize: 12,
        fontWeight: 700,
        formatter: '{c} 人'
      },
      data
    }]
  })
}

function renderRatingChart() {
  if (!ratingChartRef.value) return
  if (!ratingChart) ratingChart = init(ratingChartRef.value)

  const data = ratingChartRows.value.map((row) => ({
    name: row.className || '未命名课程',
    value: Number(row.averageRating || 0)
  }))
  if (!data.length) {
    ratingChart.clear()
    return
  }

  ratingChart.setOption({
    color: chartColors.slice(0, 5),
    tooltip: { trigger: 'item', formatter: '{b}: {c} 分' },
    legend: { bottom: 0, type: 'scroll', itemWidth: 10, itemHeight: 10 },
    series: [{
      name: '平均评分',
      type: 'pie',
      roseType: 'area',
      radius: ['18%', '72%'],
      center: ['50%', '43%'],
      itemStyle: { borderColor: '#fff', borderRadius: 5, borderWidth: 2 },
      label: { formatter: '{b}\n{c} 分' },
      data
    }]
  })
}

function clearCharts() {
  enrollChart?.clear()
  ratingChart?.clear()
}

function resizeCharts() {
  enrollChart?.resize()
  ratingChart?.resize()
}

function refresh() {
  load().catch(() => {})
}

onMounted(() => {
  load().catch(() => {})
  resizeObserver = new ResizeObserver(resizeCharts)
  if (enrollChartRef.value) resizeObserver.observe(enrollChartRef.value)
  if (ratingChartRef.value) resizeObserver.observe(ratingChartRef.value)
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  resizeObserver?.disconnect()
  enrollChart?.dispose()
  ratingChart?.dispose()
})
</script>

<style scoped>
.class-analysis {
  min-width: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

h2,
p {
  margin: 0;
}

h2 {
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.error-alert {
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  min-height: 92px;
  padding: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.summary-card span {
  color: #718095;
  font-size: 14px;
}

.summary-card strong {
  display: block;
  margin-top: 10px;
  color: #111827;
  font-size: 30px;
  line-height: 1;
}

.chart-card {
  min-height: 462px;
  border: 1px solid #e3eaf3;
  border-radius: 8px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #0d1b2f;
  font-size: 15px;
  font-weight: 700;
}

.chart-header small {
  color: #718095;
  font-size: 12px;
  font-weight: 500;
}

.chart-box {
  width: 100%;
  height: 380px;
}

@media (max-width: 900px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-card {
    margin-bottom: 18px;
  }
}

@media (max-width: 560px) {
  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .summary-grid {
    grid-template-columns: 1fr;
  }
}
</style>
