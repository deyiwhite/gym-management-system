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

    <el-table v-loading="loading" :data="memberList" class="data-table">
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
      <span>共 {{ total }} 条</span>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next"
        :total="total"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </section>
</template>

<script setup>
import { onActivated, onBeforeUnmount, onDeactivated, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const memberList = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const filters = reactive({ keyword: '', gender: '', minRemain: '', maxRemain: '' })
let pageActive = true
let requestController = null

function numberParam(value) {
  if (value === '') return undefined
  const n = Number(value)
  return Number.isFinite(n) ? n : undefined
}

function isCanceled(error) {
  return error?.code === 'ERR_CANCELED' || error?.name === 'CanceledError'
}

async function load(params = {}) {
  requestController?.abort()
  const controller = new AbortController()
  requestController = controller
  loading.value = true
  try {
    const resp = await api.get('/api/member/search', { params, signal: controller.signal })
    if (!pageActive || controller.signal.aborted) return
    memberList.value = resp.data?.memberList || []
    total.value = resp.data?.total || 0
  } catch (error) {
    if (!isCanceled(error)) throw error
  } finally {
    if (requestController === controller) {
      requestController = null
      if (pageActive) loading.value = false
    }
  }
}

function buildParams() {
  return {
    keyword: filters.keyword || undefined,
    gender: filters.gender || undefined,
    minRemain: numberParam(filters.minRemain),
    maxRemain: numberParam(filters.maxRemain),
    page: page.value,
    pageSize: pageSize.value
  }
}

function search() {
  page.value = 1
  return load(buildParams())
}

function handlePageChange(p) {
  page.value = p
  return load(buildParams())
}

function handleSizeChange(s) {
  pageSize.value = s
  page.value = 1
  return load(buildParams())
}

function reset() {
  filters.keyword = ''
  filters.gender = ''
  filters.minRemain = ''
  filters.maxRemain = ''
  page.value = 1
  return load(buildParams())
}

function edit(memberAccount) {
  router.push({ path: '/member/toUpdateMember', query: { memberAccount } })
}

async function del(memberAccount) {
  try { await ElMessageBox.confirm('确定要删除该会员吗？', '删除确认', { type: 'warning' }) } catch { return }
  await postForm('/api/member/delMember', { memberAccount })
  await search()
}

onMounted(() => {
  load(buildParams()).catch(() => {})
})

onActivated(() => {
  pageActive = true
})

onDeactivated(() => {
  pageActive = false
  loading.value = false
  requestController?.abort()
})

onBeforeUnmount(() => {
  pageActive = false
  requestController?.abort()
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
  color: #0d1b2f;
  font-size: 26px;
  font-weight: 800;
}

p {
  margin-top: 6px;
  color: #718095;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 18px;
  border: 1px solid #e3eaf3;
  border-radius: 14px;
  background: #ffffff;
  box-shadow: 0 10px 22px rgba(30, 50, 77, 0.045);
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
  color: #718095;
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
