<template>
  <section class="form-page">
    <div class="form-page-header">
      <h2>添加会员</h2>
      <el-button @click="router.push('/member/selMember')">返回</el-button>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form label-width="140px">
        <el-form-item label="姓名">
          <el-input v-model="form.memberName" />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="form.memberGender" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="form.memberAge" />
        </el-form-item>
        <el-form-item label="联系方式（11位手机号）">
          <el-input v-model="form.memberPhone" />
        </el-form-item>
        <el-form-item label="身高（cm）">
          <el-input v-model="form.memberHeight" />
        </el-form-item>
        <el-form-item label="体重（kg）">
          <el-input v-model="form.memberWeight" />
        </el-form-item>
        <el-form-item label="购买课时">
          <el-input v-model="form.cardClass" />
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
  memberName: '',
  memberGender: '',
  memberAge: '',
  memberPhone: '',
  memberHeight: '',
  memberWeight: '',
  cardClass: ''
})

async function submit() {
  msg.value = ''
  if (!form.memberName) { ElMessage.error('请输入姓名！'); return }
  if (!form.memberGender) { ElMessage.error('请输入性别！'); return }
  if (!form.memberAge) { ElMessage.error('请输入年龄！'); return }
  if (!form.memberPhone) { ElMessage.error('请输入联系方式！'); return }
  if (!form.memberHeight) { ElMessage.error('请输入身高！'); return }
  if (!form.memberWeight) { ElMessage.error('请输入体重！'); return }
  if (!form.cardClass) { ElMessage.error('请输入购买课时！'); return }

  await postForm('/api/member/addMember', {
    memberName: form.memberName,
    memberGender: form.memberGender,
    memberAge: Number(form.memberAge),
    memberPhone: Number(form.memberPhone),
    memberHeight: Number(form.memberHeight),
    memberWeight: Number(form.memberWeight),
    cardClass: Number(form.cardClass)
  })

  router.push('/member/selMember')
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
