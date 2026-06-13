<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>编辑器材</h2>
      <el-button @click="router.push('/equipment/selEquipment')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never" v-if="equipment">
      <el-form label-width="140px">
        <el-form-item label="器材id">
          <el-input v-model="equipment.equipmentId" disabled />
        </el-form-item>
        <el-form-item label="器材名称">
          <el-input v-model="equipment.equipmentName" />
        </el-form-item>
        <el-form-item label="器材位置">
          <el-input v-model="equipment.equipmentLocation" />
        </el-form-item>
        <el-form-item label="器材状态">
          <el-select v-model="equipment.equipmentStatus" style="width: 100%">
            <el-option label="正常" value="正常" />
            <el-option label="维修中" value="维修中" />
            <el-option label="已报废" value="已报废" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注信息">
          <el-input v-model="equipment.equipmentMessage" />
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
import api, { postForm } from '../api/client'

const route = useRoute()
const router = useRouter()
const equipment = ref(null)

async function load() {
  const equipmentId = Number(route.query.equipmentId || 0)
  const resp = await api.get('/api/equipment/toUpdateEquipment', { params: { equipmentId } })
  const list = resp.data?.equipmentList || []
  equipment.value = list[0] || null
}

async function submit() {
  if (!equipment.value) return
  await postForm('/api/equipment/updateEquipment', equipment.value)
  router.push('/equipment/selEquipment')
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
