<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>会员管理</h2>
        <p>查询、维护会员资料和课时状态。</p>
      </div>
      <el-button type="primary" @click="router.push('/member/toAddMember')">添加会员信息</el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form class="filter-form" :model="filters" label-position="top">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="卡号 / 姓名 / 手机号" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filters.gender" clearable placeholder="全部">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="剩余课时">
          <div class="range-field">
            <el-input v-model="filters.minRemain" clearable placeholder="最少" />
            <span>至</span>
            <el-input v-model="filters.maxRemain" clearable placeholder="最多" />
          </div>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :loading="loading" @click="search">查询</el-button>
          <el-button :disabled="loading" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table v-loading="loading" :data="pagedList" class="data-table">
      <el-table-column prop="memberAccount" label="会员账号/卡号" width="140" />
      <el-table-column prop="memberName" label="姓名" />
      <el-table-column prop="cardTime" label="办卡时间" width="140" />
      <el-table-column prop="memberGender" label="性别" width="80" />
      <el-table-column prop="memberAge" label="年龄" width="80" />
      <el-table-column prop="memberPhone" label="联系方式" width="140" />
      <el-table-column prop="cardClass" label="购买课时" width="100" />
      <el-table-column prop="cardNextClass" label="剩余课时" width="100" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="info" @click="edit(scope.row.memberAccount)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.memberAccount)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <span>共 {{ memberList.length }} 条</span>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next"
        :total="memberList.length"
      />
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const memberList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const filters = reactive({ keyword: '', gender: '', minRemain: '', maxRemain: '' })

const pagedList = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return memberList.value.slice(start, start + pageSize.value)
})

function numberParam(value) {
  if (value === '') return undefined
  const n = Number(value)
  return Number.isFinite(n) ? n : undefined
}

async function load(params = {}) {
  loading.value = true
  try {
    const resp = await api.get('/api/member/search', { params })
    memberList.value = resp.data?.memberList || []
    page.value = 1
  } finally {
    loading.value = false
  }
}

function search() {
  return load({
    keyword: filters.keyword || undefined,
    gender: filters.gender || undefined,
    minRemain: numberParam(filters.minRemain),
    maxRemain: numberParam(filters.maxRemain)
  })
}

function reset() {
  filters.keyword = ''
  filters.gender = ''
  filters.minRemain = ''
  filters.maxRemain = ''
  return load()
}

function edit(memberAccount) {
  router.push({ path: '/member/toUpdateMember', query: { memberAccount } })
}

async function del(memberAccount) {
  if (!confirm('确定要删除吗？')) return
  await postForm('/api/member/delMember', { memberAccount })
  await search()
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.page-header,
.filter-form {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

h2,
p {
  margin: 0;
}

h2 {
  font-size: 28px;
  font-weight: 600;
}

p {
  margin-top: 4px;
  color: #667085;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 16px;
  border-radius: 6px;
  background: #f8fafc;
}

.filter-form {
  display: grid;
  grid-template-columns: minmax(220px, 1fr) 140px 260px auto;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-select) {
  width: 100%;
}

.range-field {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 8px;
  align-items: center;
}

.range-field span,
.table-footer span {
  color: #667085;
}

.data-table {
  width: 100%;
}

.table-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 14px;
}
</style>
