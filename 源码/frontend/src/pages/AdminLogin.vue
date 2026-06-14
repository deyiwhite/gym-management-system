<template>
  <div class="login-wrapper">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="bg-circle bg-circle--1" />
      <div class="bg-circle bg-circle--2" />
      <div class="bg-circle bg-circle--3" />
      <div class="bg-wave" />
    </div>

    <!-- 登录容器 -->
    <div class="login-container">
      <!-- 左侧插画 -->
      <div class="login-illustration">
        <div class="illustration-content">
          <div class="illustration-icon">
            <svg viewBox="0 0 200 200" xmlns="http://www.w3.org/2000/svg">
              <circle cx="100" cy="50" r="22" fill="#fff" opacity="0.9" />
              <path
                d="M100 78c-16 0-30 10-36 25h72c-6-15-20-25-36-25z"
                fill="#fff"
                opacity="0.85"
              />
              <rect x="64" y="108" width="72" height="10" rx="5" fill="#fff" opacity="0.6" />
              <rect x="70" y="124" width="60" height="10" rx="5" fill="#fff" opacity="0.45" />
              <path
                d="M75 55l6-6 12 12 24-24 6 6-30 30z"
                fill="#10B981"
                stroke="#fff"
                stroke-width="2"
              />
              <path
                d="M40 145c0-33 27-60 60-60s60 27 60 60"
                fill="none"
                stroke="#fff"
                stroke-width="5"
                stroke-linecap="round"
                opacity="0.5"
              />
            </svg>
          </div>
          <h1 class="illustration-title">健身房管理系统</h1>
          <p class="illustration-subtitle">高效运营 · 智能管理</p>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-panel">
        <div class="form-header">
          <div class="form-avatar">
            <el-icon :size="28"><UserFilled /></el-icon>
          </div>
          <h2 class="form-title">管理员登录</h2>
          <p class="form-subtitle">请使用管理员账户登录系统</p>
        </div>

        <el-form class="login-form" @submit.prevent="submit">
          <el-input
            v-model="form.adminAccount"
            placeholder="请输入管理员账号"
            size="large"
            :prefix-icon="User"
            class="form-input"
          />

          <el-input
            v-model="form.adminPassword"
            placeholder="请输入密码"
            type="password"
            show-password
            size="large"
            :prefix-icon="Lock"
            class="form-input"
            @keyup.enter="submit"
          />

          <div class="form-options">
            <el-checkbox v-model="remembered" label="记住我" size="small" />
          </div>

          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="submit"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>

          <div class="form-error" v-if="msg">
            <el-icon><CircleCloseFilled /></el-icon>
            <span>{{ msg }}</span>
          </div>
        </el-form>

        <div class="form-footer">
          <span class="switch-text">会员？</span>
          <el-link type="primary" :underline="false" @click="router.push('/toUserLogin')">
            转到会员登录
          </el-link>
        </div>
      </div>
    </div>

    <!-- 底部版权 -->
    <div class="login-footer">
      <span>Copyright &copy; {{ new Date().getFullYear() }} 健身房管理系统</span>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { UserFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { postForm } from '../api/client'

const router = useRouter()
const form = reactive({
  adminAccount: '',
  adminPassword: ''
})
const msg = ref('')
const loading = ref(false)
const remembered = ref(false)

async function submit() {
  msg.value = ''
  loading.value = true
  try {
    const resp = await postForm('/api/adminLogin', {
      adminAccount: form.adminAccount,
      adminPassword: form.adminPassword
    })
    if (resp.data && resp.data.success) {
      router.push('/toAdminMain')
    } else {
      msg.value = resp.data?.message || '登录失败'
    }
  } catch (e) {
    msg.value = e?.response?.data?.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrapper {
  position: relative;
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #edf5f0 0%, #dcebe4 34%, #cfe3d8 68%, #eef7f2 100%);
  overflow: hidden;
}

/* ========== 背景装饰 ========== */
.bg-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.14), rgba(16, 185, 129, 0.04));
}

.bg-circle--1 {
  width: 500px;
  height: 500px;
  top: -200px;
  right: -120px;
  animation: float 8s ease-in-out infinite;
}

.bg-circle--2 {
  width: 300px;
  height: 300px;
  bottom: -80px;
  left: -60px;
  animation: float 10s ease-in-out infinite reverse;
}

.bg-circle--3 {
  width: 200px;
  height: 200px;
  top: 40%;
  left: 15%;
  animation: float 7s ease-in-out 2s infinite;
}

.bg-wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 200px;
  background: linear-gradient(135deg, rgba(16, 185, 129, 0.07), rgba(16, 185, 129, 0.02));
  clip-path: path('M0 200 C200 100, 400 150, 600 100 C800 50, 1000 120, 1200 80 C1400 40, 1600 110, 1800 90 C1900 80, 2000 60, 2000 60 L2000 200 Z');
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(3deg); }
}

/* ========== 主容器 ========== */
.login-container {
  position: relative;
  z-index: 1;
  display: flex;
  width: 960px;
  min-height: 540px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(255, 255, 255, 0.8);
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 左侧插画区 ========== */
.login-illustration {
  flex: 0 0 440px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(160deg, #10B981 0%, #0EA66F 42%, #0B6B4A 100%);
  position: relative;
  overflow: hidden;
}

.login-illustration::before {
  content: '';
  position: absolute;
  top: -100px;
  right: -80px;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.06);
}

.login-illustration::after {
  content: '';
  position: absolute;
  bottom: -60px;
  left: -40px;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.04);
}

.illustration-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: #fff;
}

.illustration-icon {
  width: 200px;
  height: 200px;
  margin: 0 auto 24px;
  animation: float 6s ease-in-out infinite;
}

.illustration-title {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 8px;
  letter-spacing: 2px;
}

.illustration-subtitle {
  font-size: 14px;
  opacity: 0.75;
  margin: 0;
  letter-spacing: 4px;
}

/* ========== 右侧表单区 ========== */
.login-form-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 48px 52px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;
}

.form-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10B981, #0EA66F);
  color: #fff;
  margin-bottom: 16px;
  box-shadow: 0 4px 15px rgba(16, 185, 129, 0.32);
}

.form-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 6px;
}

.form-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

/* ========== 表单元素 ========== */
.login-form {
  width: 100%;
}

.form-input {
  margin-bottom: 18px;
}

.form-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: all 0.2s;
  padding: 2px 12px;
}

.form-input :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.form-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #10B981 inset !important;
}

.form-input :deep(.el-input__prefix) {
  color: #a8abb2;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  border-radius: 8px;
  font-size: 16px;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #10B981, #0EA66F);
  border: none;
  transition: all 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, #22C55E, #10B981);
  box-shadow: 0 6px 20px rgba(16, 185, 129, 0.38);
  transform: translateY(-1px);
}

.login-btn:active:not(:disabled) {
  transform: translateY(0);
}

.form-error {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-top: 14px;
  padding: 10px 14px;
  background: #fef0f0;
  border-radius: 6px;
  color: #f56c6c;
  font-size: 13px;
  border: 1px solid #fde2e2;
  animation: shake 0.4s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-6px); }
  75% { transform: translateX(6px); }
}

/* ========== 底部切换 ========== */
.form-footer {
  text-align: center;
  margin-top: 28px;
  font-size: 13px;
}

.switch-text {
  color: #909399;
  margin-right: 4px;
}

/* ========== 版权 ========== */
.login-footer {
  position: absolute;
  bottom: 16px;
  z-index: 1;
  font-size: 12px;
  color: #a8abb2;
  letter-spacing: 1px;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .login-illustration {
    display: none;
  }

  .login-container {
    width: 92vw;
    min-height: auto;
  }

  .login-form-panel {
    padding: 36px 28px;
  }
}
</style>
