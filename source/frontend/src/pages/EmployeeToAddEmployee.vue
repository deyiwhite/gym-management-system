<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加员工</h2>
      <el-button @click="router.push('/employee/selEmployee')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="form.employeeName" maxlength="20" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.employeeGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.employeeAge" :min="15" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="职务">
          <el-select v-model="form.staff" placeholder="请选择职务" filterable allow-create>
            <el-option label="健身教练" value="健身教练" />
            <el-option label="保洁员" value="保洁员" />
            <el-option label="前台" value="前台" />
            <el-option label="经理" value="经理" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="form.employeeMessage" maxlength="100" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postForm } from '../api/client'

const router = useRouter()
const msg = ref('')
const form = reactive({
  employeeName: '',
  employeeGender: '',
  employeeAge: null,
  staff: '',
  employeeMessage: ''
})

async function submit() {
  msg.value = ''
  if (!form.employeeName) { ElMessage.error('请输入姓名！'); return }
  if (!form.employeeGender) { ElMessage.error('请选择性别！'); return }
  if (form.employeeAge == null || form.employeeAge < 15 || form.employeeAge > 100) { ElMessage.error('请输入有效年龄(15-100)！'); return }
  if (!form.staff) { ElMessage.error('请选择职务！'); return }

  await postForm('/api/employee/addEmployee', {
    employeeName: form.employeeName,
    employeeGender: form.employeeGender,
    employeeAge: form.employeeAge,
    staff: form.staff,
    employeeMessage: form.employeeMessage
  })

  router.push('/employee/selEmployee')
}
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
