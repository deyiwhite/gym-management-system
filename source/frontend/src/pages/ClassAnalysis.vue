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
      <button
        v-for="item in summaryCards"
        :key="item.label"
        class="summary-card"
        :class="{ 'is-action': item.action }"
        type="button"
        @click="item.action?.()"
      >
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </button>
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

    <el-card class="chart-card bubble-card" shadow="never">
      <template #header>
        <div class="chart-header">
          <span>热度 × 口碑诊断</span>
          <small>报名人数 / 平均评分 / 未评分记录</small>
        </div>
      </template>
      <div class="bubble-panel">
        <div class="bubble-plot">
          <div ref="bubbleChartRef" class="bubble-chart-box" />
          <el-empty v-if="!loading && !bubbleChartRows.length" description="暂无诊断数据" />
        </div>
        <aside class="bubble-guide">
          <h3>怎么看</h3>
          <p>横轴越靠右，报名人数越多；纵轴越靠上，平均评分越高。</p>
          <div class="guide-list">
            <div class="guide-item is-star">
              <strong>右上</strong>
              <span>高热高评 · 明星课程</span>
            </div>
            <div class="guide-item is-potential">
              <strong>左上</strong>
              <span>低热高评 · 潜力课程</span>
            </div>
            <div class="guide-item is-watch">
              <strong>右下</strong>
              <span>高热低评 · 重点优化</span>
            </div>
            <div class="guide-item is-grow">
              <strong>左下</strong>
              <span>低热低评 · 待培育</span>
            </div>
          </div>
          <div class="bubble-size-guide">
            <span class="size-dot is-small"></span>
            <span class="size-dot is-large"></span>
            <em>气泡越大，未评分记录越多</em>
          </div>
        </aside>
      </div>
    </el-card>
  </section>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { BarChart, PieChart, ScatterChart } from 'echarts/charts'
import { GridComponent, LegendComponent, MarkAreaComponent, MarkLineComponent, TooltipComponent } from 'echarts/components'
import { init, use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import api from '../api/client'

use([BarChart, PieChart, ScatterChart, GridComponent, LegendComponent, MarkAreaComponent, MarkLineComponent, TooltipComponent, CanvasRenderer])

const router = useRouter()
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
const bubbleChartRef = ref(null)
let enrollChart = null
let ratingChart = null
let bubbleChart = null
let resizeObserver = null

const chartColors = ['#2563eb', '#16a34a', '#f59e0b', '#dc2626', '#7c3aed', '#0891b2', '#db2777', '#64748b', '#ea580c', '#059669']

const summaryCards = computed(() => [
  { label: '课程总数', value: numberText(summary.value.courseTotal), action: goCourseList },
  { label: '报名总数', value: numberText(summary.value.registrationTotal), action: goAllRecords },
  { label: '平均评分', value: ratingText(summary.value.averageRating), action: goRatedRecords },
  { label: '未评分记录', value: numberText(summary.value.unratedTotal), action: goUnratedRecords }
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

const bubbleChartRows = computed(() => {
  return [...rows.value]
    .filter((row) => Number(row.enrolledCount || 0) > 0 && row.averageRating !== null && row.averageRating !== undefined)
    .map((row) => ({
      name: row.className || '未命名课程',
      enrolled: Number(row.enrolledCount || 0),
      rating: Number(row.averageRating || 0),
      unrated: Number(row.unratedCount || 0)
    }))
    .sort((a, b) => b.enrolled - a.enrolled)
})

const bubbleLabelNames = computed(() => {
  const names = new Set()
  ;[...bubbleChartRows.value]
    .sort((a, b) => b.enrolled - a.enrolled)
    .slice(0, 7)
    .forEach((item) => names.add(item.name))
  ;[...bubbleChartRows.value]
    .sort((a, b) => b.rating - a.rating)
    .slice(0, 4)
    .forEach((item) => names.add(item.name))
  return names
})

function bubbleLabelText(item) {
  if (item.enrolled >= 34 || item.rating >= 8.5) {
    return `${item.name} ${item.enrolled}人 / ${item.rating.toFixed(1)}分`
  }
  if (item.enrolled >= 20) {
    return `${item.name} ${item.enrolled}人`
  }
  return item.name
}

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
  renderBubbleChart()
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

function renderBubbleChart() {
  if (!bubbleChartRef.value) return
  if (!bubbleChart) bubbleChart = init(bubbleChartRef.value)

  const data = bubbleChartRows.value
  if (!data.length) {
    bubbleChart.clear()
    return
  }

  const heatSplit = 20
  const ratingSplit = 8.3
  const xMax = 50
  const yMin = 7
  const yMax = 9.4

  bubbleChart.setOption({
    color: chartColors,
    tooltip: {
      trigger: 'item',
      formatter(params) {
        const item = params.data.raw
        return [
          `<strong>${item.name}</strong>`,
          `报名人数：${item.enrolled} 人`,
          `平均评分：${item.rating.toFixed(1)} 分`,
          `未评分记录：${item.unrated} 条`
        ].join('<br/>')
      }
    },
    grid: { left: 52, right: 28, top: 28, bottom: 54, containLabel: false },
    xAxis: {
      type: 'value',
      name: '报名人数',
      nameLocation: 'middle',
      nameGap: 30,
      min: 0,
      max: xMax,
      interval: 10,
      axisLine: { lineStyle: { color: '#9dafc6' } },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#dde7f2' } },
      axisLabel: { color: '#718095', formatter: '{value} 人' },
      nameTextStyle: { color: '#44546a', fontWeight: 700 }
    },
    yAxis: {
      type: 'value',
      name: '平均评分',
      nameLocation: 'middle',
      nameRotate: 90,
      min: yMin,
      max: yMax,
      interval: 0.5,
      nameGap: 40,
      axisLine: { lineStyle: { color: '#9dafc6' } },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#dde7f2' } },
      axisLabel: { color: '#718095', formatter: '{value} 分' },
      nameTextStyle: { color: '#44546a', fontWeight: 700 }
    },
    series: [{
      name: '课程诊断',
      type: 'scatter',
      data: data.map((item, index) => ({
        name: item.name,
        value: [item.enrolled, item.rating, item.unrated],
        raw: item,
        itemStyle: {
          color: chartColors[index % chartColors.length],
          opacity: 0.82,
          borderColor: '#fff',
          borderWidth: 2
        }
      })),
      symbolSize(value) {
        return 20 + Math.min(Number(value[2] || 0) * 5.2, 46)
      },
      label: {
        show: true,
        formatter(params) {
          const item = params.data.raw
          if (bubbleLabelNames.value.has(item.name)) {
            return bubbleLabelText(item)
          }
          return item.name
        },
        position: 'top',
        color: '#0d1b2f',
        fontSize: 11,
        fontWeight: 700
      },
      labelLayout: { hideOverlap: true },
      emphasis: {
        focus: 'self',
        label: { show: true, fontSize: 13 }
      },
      markArea: {
        silent: true,
        itemStyle: { opacity: 0.72 },
        label: {
          show: true,
          position: 'insideTop',
          color: '#95a3b5',
          fontSize: 13,
          fontWeight: 700
        },
        data: [
          [
            { name: '低热高评：潜力课', xAxis: 0, yAxis: ratingSplit, itemStyle: { color: '#f3f7fb' } },
            { xAxis: heatSplit, yAxis: yMax }
          ],
          [
            { name: '高热高评：明星课', xAxis: heatSplit, yAxis: ratingSplit, itemStyle: { color: '#e8f6ef' } },
            { xAxis: xMax, yAxis: yMax }
          ],
          [
            {
              name: '低热低评：待培育',
              xAxis: 0,
              yAxis: yMin,
              itemStyle: { color: '#fff8e8' },
              label: { position: 'insideBottom' }
            },
            { xAxis: heatSplit, yAxis: ratingSplit }
          ],
          [
            {
              name: '高热低评：重点优化',
              xAxis: heatSplit,
              yAxis: yMin,
              itemStyle: { color: '#fff1f1' },
              label: { position: 'insideBottom' }
            },
            { xAxis: xMax, yAxis: ratingSplit }
          ]
        ]
      },
      markLine: {
        symbol: 'none',
        silent: true,
        lineStyle: { color: '#9aa8bb', type: 'dashed' },
        label: { show: false },
        data: [
          { xAxis: heatSplit },
          { yAxis: ratingSplit }
        ]
      }
    }]
  })
}

function clearCharts() {
  enrollChart?.clear()
  ratingChart?.clear()
  bubbleChart?.clear()
}

function resizeCharts() {
  enrollChart?.resize()
  ratingChart?.resize()
  bubbleChart?.resize()
}

function refresh() {
  load().catch(() => {})
}

function goCourseList() {
  router.push({
    path: '/class/selClass',
    query: { tab: 'list', t: Date.now() }
  })
}

function goAllRecords() {
  goClassRecords()
}

function goRatedRecords() {
  goClassRecords('rated')
}

function goUnratedRecords() {
  goClassRecords('unrated')
}

function goClassRecords(ratingStatus) {
  const query = {
    tab: 'records',
    t: Date.now()
  }
  if (ratingStatus) query.ratingStatus = ratingStatus

  router.push({
    path: '/class/selClass',
    query
  })
}

onMounted(() => {
  load().catch(() => {})
  resizeObserver = new ResizeObserver(resizeCharts)
  if (enrollChartRef.value) resizeObserver.observe(enrollChartRef.value)
  if (ratingChartRef.value) resizeObserver.observe(ratingChartRef.value)
  if (bubbleChartRef.value) resizeObserver.observe(bubbleChartRef.value)
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  resizeObserver?.disconnect()
  enrollChart?.dispose()
  ratingChart?.dispose()
  bubbleChart?.dispose()
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
  border: 1px solid #e3eaf3;
  min-height: 92px;
  padding: 18px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
  cursor: default;
  text-align: left;
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

.summary-card.is-action {
  cursor: pointer;
}

.summary-card.is-action:hover {
  border-color: #2f7ef7;
  box-shadow: 0 14px 30px rgba(47, 126, 247, 0.12);
}

.chart-card {
  min-height: 462px;
  border: 1px solid #e3eaf3;
  border-radius: 8px;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
}

.bubble-card {
  margin-top: 20px;
  min-height: 574px;
}

.bubble-card :deep(.el-card__body) {
  padding: 26px 32px 30px;
}

.bubble-panel {
  display: grid;
  grid-template-columns: minmax(680px, 1fr) 172px;
  gap: 26px;
  align-items: start;
}

.bubble-plot {
  min-width: 0;
}

.bubble-guide {
  padding: 16px;
  border: 1px solid #e3eaf3;
  border-radius: 8px;
  background: #f8fafc;
}

.bubble-guide h3,
.bubble-guide p {
  margin: 0;
}

.bubble-guide h3 {
  color: #0d1b2f;
  font-size: 16px;
  font-weight: 800;
}

.bubble-guide p {
  margin-top: 8px;
  color: #64748b;
  font-size: 12px;
  line-height: 1.55;
}

.guide-list {
  display: grid;
  gap: 8px;
  margin-top: 16px;
}

.guide-item {
  padding: 9px;
  border: 1px solid #e3eaf3;
  border-radius: 8px;
}

.guide-item strong,
.guide-item span {
  display: block;
}

.guide-item strong {
  color: #0d1b2f;
  font-size: 12px;
}

.guide-item span {
  margin-top: 4px;
  color: #334155;
  font-size: 12px;
  font-weight: 700;
}

.guide-item.is-star {
  border-color: #bfdbfe;
  background: #eff6ff;
}

.guide-item.is-potential {
  border-color: #bbf7d0;
  background: #ecfdf5;
}

.guide-item.is-watch {
  border-color: #fecaca;
  background: #fef2f2;
}

.guide-item.is-grow {
  border-color: #fde68a;
  background: #fffbeb;
}

.bubble-size-guide {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  color: #64748b;
  font-size: 12px;
  font-style: normal;
}

.bubble-size-guide em {
  font-style: normal;
}

.size-dot {
  flex: 0 0 auto;
  border-radius: 999px;
  background: rgba(37, 99, 235, 0.24);
  border: 1px solid rgba(37, 99, 235, 0.36);
}

.size-dot.is-small {
  width: 12px;
  height: 12px;
}

.size-dot.is-large {
  width: 24px;
  height: 24px;
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

.bubble-chart-box {
  width: 100%;
  height: 470px;
}

@media (max-width: 900px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .chart-card {
    margin-bottom: 18px;
  }

  .bubble-panel {
    grid-template-columns: 1fr;
  }

  .bubble-card :deep(.el-card__body) {
    padding: 20px;
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
