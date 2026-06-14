<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>编辑员工</h2>
      <el-button @click="router.push('/employee/selEmployee')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never" v-if="employee">
      <el-form label-width="120px">
        <el-form-item label="工号">
          <el-input v-model="employee.employeeAccount" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="employee.employeeName" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="employee.employeeGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="employee.employeeAge" :min="15" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="employee.staff" placeholder="请选择职务" filterable allow-create>
            <el-option label="健身教练" value="健身教练" />
            <el-option label="保洁员" value="保洁员" />
            <el-option label="前台" value="前台" />
            <el-option label="经理" value="经理" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="employee.employeeMessage" maxlength="100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">确认修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="form-card" shadow="never" v-else>加载中...</el-card>
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api, { postForm } from '../api/client'

const route = useRoute()
const router = useRouter()
const employee = ref(null)

async function load() {
  const employeeAccount = String(route.query.employeeAccount || '')
  const resp = await api.get('/api/employee/toUpdateEmployee', { params: { employeeAccount } })
  const list = resp.data?.employeeList || []
  employee.value = list[0] || null
}

async function submit() {
  const e = employee.value
  if (!e) return
  if (!e.employeeName) { ElMessage.error('姓名不能为空！'); return }
  if (e.employeeAge == null || e.employeeAge < 15 || e.employeeAge > 100) { ElMessage.error('年龄需在15-100之间！'); return }
  if (!e.staff) { ElMessage.error('请选择职务！'); return }
  await postForm('/api/employee/updateEmployee', employee.value)
  router.push('/employee/selEmployee')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.form-page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

h2 {
  margin: 0;
}
</style>
