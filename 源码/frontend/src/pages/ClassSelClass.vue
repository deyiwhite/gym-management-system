<template>
  <section class="class-page">
    <div class="page-header">
      <div>
        <h2>课程管理</h2>
        <p>维护课程信息，查看报名记录，并通过课程分析掌握报名、容量与评价情况。</p>
      </div>
      <el-button type="primary" @click="router.push('/class/toAddClass')">添加课程信息</el-button>
    </div>

    <el-radio-group v-model="activeTab" class="view-switch" @change="handleTabChange">
      <el-radio-button value="list">课程列表</el-radio-button>
      <el-radio-button value="records">报名记录</el-radio-button>
      <el-radio-button value="analysis">课程分析</el-radio-button>
    </el-radio-group>

    <section v-if="activeTab === 'list'" class="panel">
      <el-table :data="pagedClassList" class="data-table">
        <el-table-column prop="classId" label="编号" width="70" />
        <el-table-column label="名称" width="140">
          <template #default="scope">
            <el-link type="primary" @click="showDetail(scope.row.classId)">{{ scope.row.className }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="80">
          <template #default="scope">{{ levelText(scope.row.classLevel) }}</template>
        </el-table-column>
        <el-table-column prop="classTime" label="时长" width="80" />
        <el-table-column prop="classBegin" label="开课时间" width="180" />
        <el-table-column prop="coach" label="教练" width="110" />
        <el-table-column label="价格" width="90">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="容量" width="90">
          <template #default="scope">{{ scope.row.maxCapacity || '未设置' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button size="small" type="info" @click="openRecords(scope.row.classId)">报名记录</el-button>
            <el-button size="small" type="danger" @click="delClass(scope.row.classId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!classList.length" description="暂无课程数据" />

      <div v-if="classList.length" class="table-footer">
        <span class="table-count">共 {{ classList.length }} 门课程</span>
        <el-pagination
          v-model:current-page="classPage"
          v-model:page-size="classPageSize"
          :page-sizes="[10, 15, 20]"
          :total="classList.length"
          background
          layout="sizes, prev, pager, next"
        />
      </div>
    </section>

    <section v-if="activeTab === 'records'" class="panel">
      <el-card class="record-filter-card" shadow="never">
        <el-form class="record-form" :model="recordFilters" label-position="top">
          <el-form-item label="课程编号">
            <el-input v-model="recordFilters.classId" clearable placeholder="如 1" />
          </el-form-item>
          <el-form-item label="会员">
            <el-input v-model="recordFilters.memberKeyword" clearable placeholder="会员ID或姓名" />
          </el-form-item>
          <el-form-item label="报名状态">
            <el-select v-model="recordFilters.status" clearable placeholder="全部">
              <el-option label="已报名" value="0" />
              <el-option label="已完成" value="1" />
              <el-option label="已取消" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="评分状态">
            <el-select v-model="recordFilters.ratingStatus" clearable placeholder="全部" @change="handleRatingStatusChange">
              <el-option label="未评分" value="unrated" />
            </el-select>
          </el-form-item>
          <el-form-item label="评分范围">
            <div class="rating-range">
              <el-input
                v-model="recordFilters.lowRating"
                :disabled="recordFilters.ratingStatus === 'unrated'"
                clearable
                placeholder="最低"
              />
              <span>至</span>
              <el-input
                v-model="recordFilters.highRating"
                :disabled="recordFilters.ratingStatus === 'unrated'"
                clearable
                placeholder="最高"
              />
            </div>
          </el-form-item>
          <el-form-item class="record-actions">
            <el-button type="primary" :loading="recordsLoading" @click="loadRecords">查询</el-button>
            <el-button :disabled="recordsLoading" @click="resetRecordFilters">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div v-if="!recordsTouched" class="quiet-state">设置条件后点击查询，或从课程列表进入某门课的报名记录。</div>

      <el-table v-else v-loading="recordsLoading" :data="recordList" class="data-table">
        <el-table-column prop="recordId" label="记录ID" width="90" />
        <el-table-column prop="className" label="课程" />
        <el-table-column prop="memberId" label="会员ID" width="120" />
        <el-table-column prop="memberName" label="会员姓名" width="130" />
        <el-table-column prop="joinTime" label="报名时间" width="170" />
        <el-table-column label="状态" width="110">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.rating" :type="ratingType(scope.row.rating)" size="small">{{ scope.row.rating }}</el-tag>
            <span v-else class="muted">未评</span>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="recordsTouched && !recordsLoading && !recordList.length" description="暂无报名记录" />
    </section>

    <section v-if="activeTab === 'analysis'" class="panel">
      <div class="summary-grid">
        <button class="summary-card" type="button">
          <span>课程总数</span>
          <strong>{{ summaryNumber(analysis.summary.courseTotal) }}</strong>
        </button>
        <button class="summary-card" type="button">
          <span>报名总数</span>
          <strong>{{ summaryNumber(analysis.summary.registrationTotal) }}</strong>
        </button>
        <button class="summary-card" type="button">
          <span>平均评分</span>
          <strong>{{ summaryRating(analysis.summary.averageRating) }}</strong>
        </button>
        <button class="summary-card is-action" type="button" @click="showUnratedRecords">
          <span>未评分记录</span>
          <strong>{{ summaryNumber(analysis.summary.unratedTotal) }}</strong>
        </button>
      </div>

      <el-card class="analysis-card" shadow="never">
        <template #header>
          <div class="analysis-header">
            <div>
              <h3>课程运营分析</h3>
              <p>按报名热度排序，综合查看容量利用率和评价表现。</p>
            </div>
            <button class="plain-action" type="button" :disabled="analysisLoading" @click="loadAnalysis">
              {{ analysisLoading ? '刷新中' : '刷新数据' }}
            </button>
          </div>
        </template>

        <el-table v-loading="analysisLoading" :data="analysis.rows" class="data-table">
          <el-table-column label="排名" width="80">
            <template #default="scope">{{ scope.$index + 1 }}</template>
          </el-table-column>
          <el-table-column prop="className" label="课程名" />
          <el-table-column prop="coach" label="教练" width="130">
            <template #default="scope">{{ scope.row.coach || '未安排' }}</template>
          </el-table-column>
          <el-table-column label="已报名 / 容量" width="150">
            <template #default="scope">
              {{ numberValue(scope.row.enrolledCount) }} / {{ capacityText(scope.row.maxCapacity) }}
            </template>
          </el-table-column>
          <el-table-column label="利用率" min-width="220">
            <template #default="scope">
              <div class="usage-cell">
                <el-progress
                  :percentage="progressPercent(scope.row.capacityUsage)"
                  :color="usageColor(scope.row.capacityUsage)"
                  :show-text="false"
                />
                <el-tag :type="usageTagType(scope.row.capacityUsage)" size="small">
                  {{ usageText(scope.row.capacityUsage, scope.row.maxCapacity) }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="平均评分" width="120">
            <template #default="scope">
              <el-tag v-if="scope.row.averageRating != null" :type="ratingType(scope.row.averageRating)">
                {{ Number(scope.row.averageRating).toFixed(1) }}
              </el-tag>
              <span v-else class="muted">暂无评分</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </section>

    <el-dialog v-model="detailVisible" :title="detail.cur?.className || '课程详情'" width="620px">
      <template v-if="detail.cur">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="编号">{{ detail.cur.classId }}</el-descriptions-item>
          <el-descriptions-item label="等级">
            <el-tag>{{ levelText(detail.cur.classLevel) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="时长">{{ detail.cur.classTime || detail.cur.classDuration }}分钟</el-descriptions-item>
          <el-descriptions-item label="价格">¥{{ detail.cur.price }}</el-descriptions-item>
          <el-descriptions-item label="教练">{{ detail.cur.coach }}</el-descriptions-item>
          <el-descriptions-item label="容量">{{ detail.enrolled }} / {{ detail.cur.maxCapacity || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="开课时间" :span="2">{{ detail.cur.classBegin }}</el-descriptions-item>
          <el-descriptions-item label="课程描述" :span="2">{{ detail.cur.classDescription || '暂无描述' }}</el-descriptions-item>
          <el-descriptions-item label="使用器械" :span="2">
            <template v-if="detail.equipList?.length">
              <el-tag v-for="eq in detail.equipList" :key="eq.equipmentId" class="tag" size="small">
                {{ eq.equipmentName }}
              </el-tag>
            </template>
            <span v-else class="muted">未关联器械</span>
          </el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const activeTab = ref('list')
const classList = ref([])
const classPage = ref(1)
const classPageSize = ref(10)
const recordList = ref([])
const recordsLoading = ref(false)
const recordsTouched = ref(false)
const analysisLoading = ref(false)

const recordFilters = reactive({
  classId: '',
  memberKeyword: '',
  status: '',
  ratingStatus: '',
  lowRating: '',
  highRating: ''
})

const analysis = reactive({
  loaded: false,
  summary: {
    courseTotal: 0,
    registrationTotal: 0,
    averageRating: 0,
    unratedTotal: 0
  },
  rows: []
})

const detailVisible = ref(false)
const detail = reactive({ cur: null, equipList: [], enrolled: 0 })

const pagedClassList = computed(() => {
  const start = (classPage.value - 1) * classPageSize.value
  return classList.value.slice(start, start + classPageSize.value)
})

const levelMap = { 1: '入门', 2: '进阶', 3: '专业' }
const statusMap = { 0: '已报名', 1: '已完成', 2: '已取消' }

function levelText(level) {
  return levelMap[level] || '未设置'
}

function statusText(status) {
  return statusMap[status] || status
}

function statusType(status) {
  if (status === 1) return 'success'
  if (status === 2) return 'danger'
  return 'info'
}

function ratingType(rating) {
  const value = Number(rating)
  if (value >= 8) return 'success'
  if (value >= 6) return ''
  if (value >= 5) return 'warning'
  return 'danger'
}

function nullableNumber(value) {
  if (value === '' || value === null || value === undefined) return undefined
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : undefined
}

function numberValue(value) {
  return Number(value || 0)
}

function summaryNumber(value) {
  return numberValue(value)
}

function summaryRating(value) {
  return Number(value || 0).toFixed(1)
}

function capacityText(value) {
  const capacity = Number(value || 0)
  return capacity > 0 ? capacity : '未设置容量'
}

function progressPercent(value) {
  const percent = Number(value || 0)
  if (percent < 0) return 0
  if (percent > 100) return 100
  return percent
}

function usageText(value, maxCapacity) {
  if (!Number(maxCapacity || 0)) return '未设置容量'
  return `${Number(value || 0).toFixed(1)}%`
}

function usageColor(value) {
  const percent = Number(value || 0)
  if (percent >= 100) return '#ef4444'
  if (percent >= 90) return '#f59e0b'
  if (percent >= 60) return '#22c55e'
  return '#cbd5e1'
}

function usageTagType(value) {
  const percent = Number(value || 0)
  if (percent >= 100) return 'danger'
  if (percent >= 90) return 'warning'
  if (percent >= 60) return 'success'
  return 'info'
}

function handleRatingStatusChange() {
  if (recordFilters.ratingStatus === 'unrated') {
    recordFilters.lowRating = ''
    recordFilters.highRating = ''
  }
}

async function loadClasses() {
  const resp = await api.get('/api/class/selClass')
  classList.value = resp.data?.classList || []
  classPage.value = 1
}

async function loadRecords() {
  recordsTouched.value = true
  recordsLoading.value = true
  try {
    const params = {
      classId: nullableNumber(recordFilters.classId),
      memberKeyword: recordFilters.memberKeyword || undefined,
      status: nullableNumber(recordFilters.status),
      ratingStatus: recordFilters.ratingStatus || undefined,
      lowRating: recordFilters.ratingStatus === 'unrated' ? undefined : nullableNumber(recordFilters.lowRating),
      highRating: recordFilters.ratingStatus === 'unrated' ? undefined : nullableNumber(recordFilters.highRating)
    }
    const resp = await api.get('/api/class/record/search', { params })
    recordList.value = resp.data?.classRecordList || []
  } finally {
    recordsLoading.value = false
  }
}

async function loadAnalysis() {
  analysisLoading.value = true
  try {
    const resp = await api.get('/api/class/analysis')
    analysis.summary = resp.data?.summary || analysis.summary
    analysis.rows = resp.data?.courseAnalysisRows || []
    analysis.loaded = true
  } finally {
    analysisLoading.value = false
  }
}

async function showDetail(classId) {
  detailVisible.value = true
  const resp = await api.get('/api/class/classDetail', { params: { classId } })
  detail.cur = resp.data?.course
  detail.equipList = resp.data?.equipmentList || []
  detail.enrolled = resp.data?.enrolledCount || 0
}

function handleTabChange(tab) {
  if (tab === 'analysis' && !analysis.loaded) loadAnalysis().catch(() => {})
}

function openRecords(classId) {
  recordFilters.classId = String(classId)
  activeTab.value = 'records'
  loadRecords().catch(() => {})
}

function showUnratedRecords() {
  recordFilters.ratingStatus = 'unrated'
  recordFilters.lowRating = ''
  recordFilters.highRating = ''
  activeTab.value = 'records'
  loadRecords().catch(() => {})
}

function resetRecordFilters() {
  recordFilters.classId = ''
  recordFilters.memberKeyword = ''
  recordFilters.status = ''
  recordFilters.ratingStatus = ''
  recordFilters.lowRating = ''
  recordFilters.highRating = ''
  recordList.value = []
  recordsTouched.value = false
}

async function delClass(classId) {
  if (!confirm('确定要删除吗？')) return
  await postForm('/api/class/delClass', { classId })
  await loadClasses()
  if (analysis.loaded) await loadAnalysis()
}

onMounted(() => {
  loadClasses().catch(() => {})
})
</script>

<style scoped>
.class-page {
  min-width: 0;
}

.page-header,
.analysis-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

h2,
h3,
p {
  margin: 0;
}

h2 {
  font-size: 28px;
  font-weight: 600;
}

h3 {
  font-size: 18px;
  font-weight: 600;
}

p,
.muted {
  margin-top: 4px;
  color: #667085;
  font-size: 14px;
}

.view-switch {
  margin-bottom: 18px;
}

.panel {
  min-width: 0;
}

.record-filter-card {
  margin-bottom: 16px;
  border-radius: 6px;
  background: #f8fafc;
}

.record-form {
  display: grid;
  grid-template-columns: 130px minmax(170px, 1.2fr) 140px 140px 220px auto;
  gap: 12px;
  align-items: end;
}

.record-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.record-form :deep(.el-form-item__label) {
  padding-bottom: 5px;
  color: #667085;
  font-size: 13px;
  line-height: 1.2;
}

.record-form :deep(.el-select) {
  width: 100%;
}

.rating-range {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: center;
}

.rating-range span {
  color: #667085;
}

.record-actions {
  min-width: 132px;
}

.quiet-state {
  padding: 38px 20px;
  border: 1px dashed #d0d5dd;
  border-radius: 6px;
  color: #667085;
  text-align: center;
  background: #fbfcfe;
}

.data-table {
  width: 100%;
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-top: 14px;
}

.table-count {
  color: #667085;
  font-size: 13px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 18px;
}

.summary-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-height: 96px;
  padding: 18px;
  border: 1px solid #e6eaf0;
  border-radius: 6px;
  background: #ffffff;
  color: #111827;
  cursor: default;
  text-align: left;
}

.summary-card span {
  color: #667085;
  font-size: 14px;
}

.summary-card strong {
  margin-top: 10px;
  font-size: 30px;
  line-height: 1;
}

.summary-card.is-action {
  cursor: pointer;
}

.summary-card.is-action:hover {
  border-color: #409eff;
  box-shadow: 0 8px 20px rgba(64, 158, 255, 0.12);
}

.analysis-card {
  border-radius: 6px;
}

.usage-cell {
  display: grid;
  grid-template-columns: minmax(120px, 1fr) auto;
  gap: 12px;
  align-items: center;
}

.tag {
  margin-right: 8px;
}

@media (max-width: 1200px) {
  .record-form {
    grid-template-columns: repeat(2, minmax(160px, 1fr));
  }

  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
