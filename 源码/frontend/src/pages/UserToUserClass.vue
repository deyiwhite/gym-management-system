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
        <el-table-column prop="classBegin" label="时间" width="180" />
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
            <el-button size="small" type="danger" @click="del(scope.row.recordId)">退课</el-button>
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
