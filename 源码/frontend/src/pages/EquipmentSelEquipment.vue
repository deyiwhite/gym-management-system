<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>器材管理</h2>
        <p>按器材编号、名称、位置和状态查询器材。</p>
      </div>
      <el-button type="primary" @click="router.push('/equipment/toAddEquipment')">添加器材信息</el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form class="filter-form" :model="filters" label-position="top">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="编号 / 名称" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="位置">
          <el-select v-model="filters.location" clearable placeholder="全部" filterable>
            <el-option label="1号房间" value="1号房间" />
            <el-option label="2号房间" value="2号房间" />
            <el-option label="3号房间" value="3号房间" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filters.status" clearable placeholder="全部">
            <el-option label="正常" value="正常" />
            <el-option label="损坏" value="损坏" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :loading="loading" @click="search">查询</el-button>
          <el-button :disabled="loading" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table v-loading="loading" :data="equipmentList" class="data-table">
      <el-table-column prop="equipmentId" label="器材ID" width="120" />
      <el-table-column prop="equipmentName" label="名称" />
      <el-table-column prop="equipmentLocation" label="位置" width="160" />
      <el-table-column prop="equipmentStatus" label="状态" width="120" />
      <el-table-column prop="equipmentMessage" label="备注" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="info" @click="edit(scope.row.equipmentId)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.equipmentId)">删除</el-button>
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
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import api, { postForm } from '../api/client'

const router = useRouter()
const equipmentList = ref([])
const total = ref(0)
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const filters = reactive({ keyword: '', location: '', status: '' })

async function load(params = {}) {
  loading.value = true
  try {
    const resp = await api.get('/api/equipment/search', { params })
    equipmentList.value = resp.data?.equipmentList || []
    total.value = resp.data?.total || 0
  } finally {
    loading.value = false
  }
}

function search() {
  page.value = 1
  return load(buildParams())
}

function buildParams() {
  return {
    keyword: filters.keyword || undefined,
    location: filters.location || undefined,
    status: filters.status || undefined,
    page: page.value,
    pageSize: pageSize.value
  }
}

function reset() {
  filters.keyword = ''
  filters.location = ''
  filters.status = ''
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

function edit(equipmentId) {
  router.push({ path: '/equipment/toUpdateEquipment', query: { equipmentId } })
}

async function del(equipmentId) {
  try { await ElMessageBox.confirm('确定要删除该器材吗？', '删除确认', { type: 'warning' }) } catch { return }
  await postForm('/api/equipment/delEquipment', { equipmentId })
  await search()
}

onMounted(() => {
  load(buildParams()).catch(() => {})
})
</script>

<style scoped>
.page-header {
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
  grid-template-columns: minmax(200px, 260px) 170px 170px auto;
  gap: 30px;
  align-items: end;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-select) {
  width: 100%;
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

.table-footer span {
  color: #718095;
}
</style>
