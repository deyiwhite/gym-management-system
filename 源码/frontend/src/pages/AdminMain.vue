<template>
  <div class="admin-main">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">管理员主页</h2>
        <p class="page-sub">欢迎回来，以下是健身房今日概览</p>
      </div>
      <el-button class="logout-btn" @click="logout">
        <el-icon><SwitchButton /></el-icon>
        退出登录
      </el-button>
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col v-for="item in statCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <div class="stat-card" :style="{ '--accent': item.color, '--accent-bg': item.bg }">
          <div class="stat-icon">
            <el-icon :size="26"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-body">
            <span class="stat-value">{{ item.value }}</span>
            <span class="stat-unit">{{ item.unit }}</span>
          </div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </el-col>
    </el-row>

    <div class="quick-section">
      <div class="section-title">快捷入口</div>
      <div class="quick-grid">
        <div
          v-for="link in quickLinks"
          :key="link.label"
          class="quick-card"
          :style="{ '--q-color': link.color, '--q-bg': link.bg }"
          @click="router.push(link.path)"
        >
          <el-icon :size="22"><component :is="link.icon" /></el-icon>
          <span>{{ link.label }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Avatar, Calendar, Cpu, CreditCard, SwitchButton, User, UserFilled } from '@element-plus/icons-vue'
import { computed, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import api, { postForm } from '../api/client'

const router = useRouter()
const totals = reactive({
  memberTotal: 0,
  employeeTotal: 0,
  humanTotal: 0,
  equipmentTotal: 0
})

const statCards = computed(() => [
  { label: '健身房总人数', value: totals.humanTotal, unit: '人', icon: UserFilled, color: '#3b82f6', bg: '#eff6ff' },
  { label: '员工人数', value: totals.employeeTotal, unit: '人', icon: Avatar, color: '#10b981', bg: '#ecfdf5' },
  { label: '会员人数', value: totals.memberTotal, unit: '人', icon: User, color: '#f59e0b', bg: '#fffbeb' },
  { label: '器材数量', value: totals.equipmentTotal, unit: '件', icon: Cpu, color: '#8b5cf6', bg: '#f5f3ff' }
])

const quickLinks = [
  { label: '会员管理', path: '/member/selMember', icon: User, color: '#3b82f6', bg: '#eff6ff' },
  { label: '员工管理', path: '/employee/selEmployee', icon: Avatar, color: '#10b981', bg: '#ecfdf5' },
  { label: '课程管理', path: '/class/selClass', icon: Calendar, color: '#f59e0b', bg: '#fffbeb' },
  { label: '器材管理', path: '/equipment/selEquipment', icon: Cpu, color: '#8b5cf6', bg: '#f5f3ff' },
  { label: '会员卡查询', path: '/member/toSelByCard', icon: CreditCard, color: '#ef4444', bg: '#fff1f2' }
]

async function load() {
  const resp = await api.get('/api/toAdminMain')
  const data = resp.data || {}
  totals.memberTotal = data.memberTotal || 0
  totals.employeeTotal = data.employeeTotal || 0
  totals.humanTotal = data.humanTotal || 0
  totals.equipmentTotal = data.equipmentTotal || 0
}

async function logout() {
  await postForm('/api/logout', {})
  router.push('/')
}

onMounted(() => {
  load().catch(() => {})
})
</script>

<style scoped>
.admin-main {
  min-height: 100%;
  padding: 28px 32px;
  background: #f4f6f9;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 28px;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  margin: 0;
  color: #111827;
  font-size: 22px;
  font-weight: 600;
}

.page-sub {
  margin: 0;
  color: #9ca3af;
  font-size: 13px;
}

.logout-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border-color: #e5e7eb;
  background: #ffffff;
  color: #6b7280;
}

.logout-btn:hover {
  border-color: #ef4444;
  background: #fef2f2;
  color: #ef4444;
}

.stats-row {
  margin-bottom: 64px;
}

.stat-card {
  min-height: 158px;
  padding: 20px 20px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  background: #ffffff;
  cursor: default;
  transition: box-shadow 0.2s ease, transform 0.2s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  margin-bottom: 14px;
  border-radius: 12px;
  background: var(--accent-bg);
  color: var(--accent);
}

.stat-body {
  display: flex;
  align-items: baseline;
  gap: 4px;
  margin-bottom: 4px;
}

.stat-value {
  color: #111827;
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.stat-unit {
  color: #9ca3af;
  font-size: 14px;
}

.stat-label {
  color: #6b7280;
  font-size: 13px;
}

.quick-section {
  padding: 20px 20px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  background: #ffffff;
}

.section-title {
  margin-bottom: 16px;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(5, 132px);
  gap: 14px;
  justify-content: center;
}

.quick-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 7px;
  height: 78px;
  padding: 10px 8px;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  background: #ffffff;
  color: #4b5563;
  cursor: pointer;
  font-size: 12px;
  transition: background 0.2s ease, border-color 0.2s ease, color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

.quick-card:hover {
  border-color: var(--q-color);
  background: var(--q-bg);
  color: var(--q-color);
  box-shadow: 0 4px 12px rgba(15, 23, 42, 0.06);
  transform: translateY(-2px);
}

.quick-card :deep(.el-icon) {
  color: var(--q-color);
}

@media (max-width: 1080px) {
  .quick-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-main {
    padding: 20px;
  }

  .page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .logout-btn {
    width: fit-content;
  }

  .quick-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
