<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加器材</h2>
      <el-button @click="router.push('/equipment/selEquipment')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="120px">
        <el-form-item label="器材名称">
          <el-input v-model="form.equipmentName" maxlength="30" />
        </el-form-item>
        <el-form-item label="器材位置">
          <el-select v-model="form.equipmentLocation" placeholder="请选择位置" filterable allow-create>
            <el-option label="1号房间" value="1号房间" />
            <el-option label="2号房间" value="2号房间" />
            <el-option label="3号房间" value="3号房间" />
          </el-select>
        </el-form-item>
        <el-form-item label="器材状态">
          <el-select v-model="form.equipmentStatus" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="损坏" value="损坏" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="form.equipmentMessage" maxlength="100" />
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
  if (!form.equipmentStatus) { ElMessage.error('请选择器材状态！'); return }

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
