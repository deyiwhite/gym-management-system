<template>
  <div style="padding: 24px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2 style="margin: 0">我的课程</h2>
      <el-button @click="router.push('/toUserMain')">返回主页</el-button>
    </div>

    <el-card>
      <el-table :data="recordList" style="width: 100%">
        <el-table-column prop="classId" label="编号" width="120" />
        <el-table-column prop="className" label="名称" />
        <el-table-column label="时间" width="180">
          <template #default="scope">{{ formatDateTime(scope.row.classBegin) }}</template>
        </el-table-column>
        <el-table-column prop="coachName" label="教练" width="160" />
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="statusType(scope.row.status)">{{ statusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="scope">
            <el-tag v-if="scope.row.rating != null" :type="ratingType(scope.row.rating)" size="small">{{ scope.row.rating }}</el-tag>
            <span v-else style="color:#999">未评</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button
              v-if="scope.row.status === 0"
              size="small"
              type="danger"
              @click="del(scope.row.recordId)"
            >
              退课
            </el-button>
            <el-tag v-else type="info" size="small">不可退课</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!recordList.length" style="color: #666; margin-top: 16px">暂无课程</div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const recordList = ref([])

const statusMap = { 0: '已报名', 1: '已完成', 2: '已取消' }
function statusText(s) { return statusMap[s] ?? s }
function statusType(s) {
  if (s === 1) return 'success'
  if (s === 2) return 'danger'
  return 'info'
}
function ratingType(r) {
  if (r >= 8.0) return 'success'
  if (r >= 6.0) return ''
  if (r >= 5.0) return 'warning'
  return 'danger'
}

function padTime(value) {
  return String(value).padStart(2, '0')
}

function formatDateTime(value) {
  if (!value) return '-'
  if (typeof value === 'string') {
    const normalized = value.trim().replace('T', ' ')
    const matched = normalized.match(/^(\d{4}-\d{2}-\d{2})\s+(\d{2}:\d{2})/)
    if (matched) return `${matched[1]} ${matched[2]}`
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return String(value)

  return [
    date.getFullYear(),
    padTime(date.getMonth() + 1),
    padTime(date.getDate())
  ].join('-') + ` ${padTime(date.getHours())}:${padTime(date.getMinutes())}`
}

async function load() {
  const resp = await api.get('/api/user/toUserClass')
  recordList.value = resp.data?.classRecordList || []
}

async function del(recordId) {
  if (!confirm('确定要退课吗？')) return
  await postForm('/api/user/delUserClass', { recordId })
  await load()
}

onMounted(() => {
  load().catch(() => {})
})
</script>
