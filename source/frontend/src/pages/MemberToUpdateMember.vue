<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>编辑会员</h2>
      <el-button @click="router.push('/member/selMember')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never" v-if="member">
      <el-form label-width="140px">
        <el-form-item label="会员卡号">
          <el-input v-model="member.memberAccount" disabled />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="member.memberPassword" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="member.memberName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="member.memberGender" placeholder="请选择性别">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="member.memberAge" :min="1" :max="100" :step="1" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="member.memberPhone" maxlength="11" show-word-limit />
        </el-form-item>
        <el-form-item label="身高（cm）">
          <el-input-number v-model="member.memberHeight" :min="50" :max="250" :step="1" />
        </el-form-item>
        <el-form-item label="体重（kg）">
          <el-input-number v-model="member.memberWeight" :min="20" :max="300" :step="1" />
        </el-form-item>
        <el-form-item label="购买课时">
          <el-input-number v-model="member.cardClass" :min="1" :max="999" :step="1" />
        </el-form-item>
        <el-form-item label="剩余课时">
          <el-input-number v-model="member.cardNextClass" :min="0" :max="999" :step="1" />
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
const member = ref(null)

async function load() {
  const memberAccount = String(route.query.memberAccount || '')
  const resp = await api.get('/api/member/toUpdateMember', { params: { memberAccount } })
  const list = resp.data?.memberList || []
  if (list.length) {
    member.value = list[0]
  }
}

async function submit() {
  const m = member.value
  if (!m) return
  if (!m.memberName) { ElMessage.error('姓名不能为空！'); return }
  if (m.memberAge == null || m.memberAge < 1 || m.memberAge > 100) { ElMessage.error('年龄需在1-100之间！'); return }
  if (!/^\d{11}$/.test(String(m.memberPhone || ''))) { ElMessage.error('请输入11位手机号！'); return }
  if (m.memberHeight == null || m.memberHeight < 50 || m.memberHeight > 250) { ElMessage.error('身高需在50-250cm之间！'); return }
  if (m.memberWeight == null || m.memberWeight < 20 || m.memberWeight > 300) { ElMessage.error('体重需在20-300kg之间！'); return }
  await postForm('/api/member/updateMember', member.value)
  router.push('/member/selMember')
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
