<template>
  <div style="padding: 24px">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px">
      <h2 style="margin: 0">报名选课</h2>
      <el-button @click="router.push('/user/toUserClass')">返回我的课程</el-button>
    </div>

    <el-card>
      <el-table :data="classList" style="width: 100%">
        <el-table-column prop="classId" label="编号" width="80" />
        <el-table-column prop="className" label="名称" />
        <el-table-column label="等级" width="80">
          <template #default="scope">{{ levelText(scope.row.classLevel) }}</template>
        </el-table-column>
        <el-table-column prop="classTime" label="时长" width="80" />
        <el-table-column prop="coach" label="教练" width="120" />
        <el-table-column label="价格" width="80">
          <template #default="scope">¥{{ scope.row.price }}</template>
        </el-table-column>
        <el-table-column label="容量" width="80">
          <template #default="scope">{{ scope.row.maxCapacity }}人</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button size="small" type="primary" @click="apply(scope.row.classId)">报名</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!classList.length" style="color: #666; margin-top: 16px">暂无课程</div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const classList = ref([])

const levelMap: Record<number, string> = { 1: '入门', 2: '进阶', 3: '专业' }
function levelText(l: number) { return levelMap[l] ?? '' }

async function load() {
  const resp = await api.get('/api/user/toApplyClass')
  classList.value = resp.data?.classList || []
}

async function apply(classId: number) {
  await postForm('/api/user/applyClass', { classId })
  await load()
  router.push('/user/toUserClass')
}

onMounted(() => { load().catch(() => {}) })
</script>
