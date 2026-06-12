<template>
  <section class="page">
    <div class="page-header">
      <div>
        <h2>员工管理</h2>
        <p>按工号、姓名、性别和职务查询员工信息。</p>
      </div>
      <el-button type="primary" @click="router.push('/employee/toAddEmployee')">添加员工信息</el-button>
    </div>

    <el-card class="filter-card" shadow="never">
      <el-form class="filter-form" :model="filters" label-position="top">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" clearable placeholder="工号 / 姓名" @keyup.enter="search" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="filters.gender" clearable placeholder="全部">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="filters.staff" clearable placeholder="全部" class="staff-select">
            <el-option label="健身教练" value="健身教练" />
            <el-option label="保洁员" value="保洁员" />
          </el-select>
        </el-form-item>
        <el-form-item class="filter-actions">
          <el-button type="primary" :loading="loading" @click="search">查询</el-button>
          <el-button :disabled="loading" @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-table v-loading="loading" :data="pagedList" class="data-table">
      <el-table-column prop="employeeAccount" label="工号" width="120" />
      <el-table-column prop="employeeName" label="姓名" />
      <el-table-column prop="employeeGender" label="性别" width="90" />
      <el-table-column prop="employeeAge" label="年龄" width="80" />
      <el-table-column prop="entryTime" label="入职时间" width="160" />
      <el-table-column prop="staff" label="职务" />
      <el-table-column prop="employeeMessage" label="备注信息" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="info" @click="edit(scope.row.employeeAccount)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(scope.row.employeeAccount)">解雇</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-footer">
      <span>共 {{ employeeList.length }} 条</span>
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="sizes, prev, pager, next"
        :total="employeeList.length"
      />
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const employeeList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const filters = reactive({ keyword: '', gender: '', staff: '' })

const pagedList = computed(() => {
  const start = (page.value - 1) * pageSize.value
  return employeeList.value.slice(start, start + pageSize.value)
})

async function load(params = {}) {
  loading.value = true
  try {
    const resp = await api.get('/api/employee/search', { params })
    employeeList.value = resp.data?.employeeList || []
    page.value = 1
  } finally {
    loading.value = false
  }
}

function search() {
  return load({
    keyword: filters.keyword || undefined,
    gender: filters.gender || undefined,
    staff: filters.staff || undefined
  })
}

function reset() {
  filters.keyword = ''
  filters.gender = ''
  filters.staff = ''
  return load()
}

function edit(employeeAccount) {
  router.push({ path: '/employee/toUpdateEmployee', query: { employeeAccount } })
}

async function del(employeeAccount) {
  if (!confirm('确定要解雇吗？')) return
  await postForm('/api/employee/delEmployee', { employeeAccount })
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
  grid-template-columns: minmax(260px, 360px) 160px 180px auto;
  justify-content: start;
  align-items: end;
}

.filter-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.filter-form :deep(.el-select) {
  width: 100%;
}

.filter-actions {
  min-width: 168px;
}

@media (max-width: 1100px) {
  .filter-form {
    grid-template-columns: repeat(2, minmax(180px, 1fr));
  }
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
  color: #667085;
}
</style>
