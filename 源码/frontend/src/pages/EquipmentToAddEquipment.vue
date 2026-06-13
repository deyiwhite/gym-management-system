<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加器材</h2>
      <el-button @click="router.push('/equipment/selEquipment')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="140px">
        <el-form-item label="器材名称">
          <el-input v-model="form.equipmentName" />
        </el-form-item>
        <el-form-item label="器材位置">
          <el-input v-model="form.equipmentLocation" />
        </el-form-item>
        <el-form-item label="器材状态">
          <el-input v-model="form.equipmentStatus" />
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="form.equipmentMessage" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submit">添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </section>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { postForm } from '../api/client'

const router = useRouter()
const form = reactive({
  equipmentName: '',
  equipmentLocation: '',
  equipmentStatus: '',
  equipmentMessage: ''
})

async function submit() {
  if (!form.equipmentName) { ElMessage.error('请输入器材名称！'); return }
  if (!form.equipmentLocation) { ElMessage.error('请输入器材位置！'); return }
  if (!form.equipmentStatus) { ElMessage.error('请输入器材状态！'); return }
  if (!form.equipmentMessage) { ElMessage.error('请输入备注信息！'); return }

  await postForm('/api/equipment/addEquipment', {
    equipmentName: form.equipmentName,
    equipmentLocation: form.equipmentLocation,
    equipmentStatus: form.equipmentStatus,
    equipmentMessage: form.equipmentMessage
  })

  router.push('/equipment/selEquipment')
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
