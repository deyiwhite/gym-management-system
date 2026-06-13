<template>
  <el-container class="role-layout">
    <el-aside width="240px" class="role-aside">
      <div class="role-brand">
        <div class="role-title">健身房管理系统</div>
        <div class="role-subtitle">Gym Operations Console</div>
      </div>

      <el-menu
        :router="true"
        :default-active="activePath"
        class="role-menu"
        background-color="transparent"
        text-color="#c8d3e2"
        active-text-color="#ffffff"
      >
        <template v-if="role === 'admin'">
          <el-menu-item index="/toAdminMain">管理员主页</el-menu-item>
          <el-menu-item index="/member/toSelByCard">会员卡查询</el-menu-item>
          <el-menu-item index="/member/selMember">会员管理</el-menu-item>
          <el-menu-item index="/employee/selEmployee">员工管理</el-menu-item>
          <el-menu-item index="/equipment/selEquipment">器材管理</el-menu-item>
          <el-menu-item index="/class/selClass">课程管理</el-menu-item>
          <el-menu-item index="/class/analysis">课程运营分析</el-menu-item>
        </template>

        <template v-else>
          <el-menu-item index="/toUserMain">会员主页</el-menu-item>
          <el-menu-item index="/user/toUserInfo">个人信息</el-menu-item>

          <el-sub-menu index="user-course">
            <template #title>课程管理</template>
            <el-menu-item index="/user/toApplyClass">报名选课</el-menu-item>
            <el-menu-item index="/user/toUserClass">我的课程</el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>

      <div class="role-footer">
        <div class="role-footer-title">{{ role === 'admin' ? '管理员' : '会员' }}</div>
        <div class="role-footer-text">{{ role === 'admin' ? '今日运营数据已更新' : '欢迎回来' }}</div>
      </div>
    </el-aside>

    <el-main class="role-main">
      <slot />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

type Role = 'admin' | 'user'

const props = defineProps<{
  role: Role
}>()

const route = useRoute()
const activePath = computed(() => route.path)
</script>

<style scoped>
.role-layout {
  min-height: 100svh;
  width: 100%;
  min-width: 0;
  align-items: flex-start;
  background:
    linear-gradient(180deg, rgba(47, 126, 247, 0.08), transparent 320px),
    #eef3f8;
}

.role-aside {
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  flex: 0 0 240px;
  box-sizing: border-box;
  padding: 24px 14px 16px;
  background:
    radial-gradient(circle at 20% 0%, rgba(79, 140, 255, 0.22), transparent 34%),
    linear-gradient(180deg, #2c3e56, #223146);
  color: #c8d3e2;
  box-shadow: 8px 0 24px rgba(31, 45, 64, 0.14);
}

.role-brand {
  padding: 0 10px 22px;
}

.role-title {
  font-size: 21px;
  font-weight: 800;
  line-height: 1.25;
  color: #ffffff;
}

.role-subtitle {
  margin-top: 8px;
  color: #91a3b7;
  font-size: 12px;
}

.role-menu {
  border-right: none;
  flex: 1;
  padding: 0 12px;
  background: transparent;
}

.role-menu .el-menu-item {
  height: 42px;
  margin-bottom: 6px;
  padding-left: 13px !important;
  border-radius: 9px;
  font-size: 15px;
  transition: background 0.2s ease, color 0.2s ease;
}
.role-menu .el-sub-menu .el-sub-menu__title {
  height: 42px;
  padding-left: 13px !important;
  border-radius: 9px;
  font-size: 15px;
}

.role-menu :deep(.el-menu-item:hover),
.role-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: #ffffff;
}

.role-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.12);
  box-shadow: inset 3px 0 0 #62a4ff;
  color: #ffffff;
  font-weight: 700;
}

.role-footer {
  margin-top: auto;
  padding: 14px 13px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.07);
  color: #ffffff;
}

.role-footer-title {
  font-size: 14px;
  font-weight: 700;
}

.role-footer-text {
  margin-top: 6px;
  color: #9fb0c5;
  font-size: 13px;
}

.role-main {
  min-width: 0;
  padding: 24px 32px;
  overflow: auto;
  background:
    linear-gradient(180deg, rgba(47, 126, 247, 0.08), transparent 320px),
    #eef3f8;
}
</style>
