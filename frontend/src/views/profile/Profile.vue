<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>

    <div class="profile-grid">
      <!-- User Info Card -->
      <n-card title="基本信息" style="margin-bottom: 16px;">
        <div class="profile-header">
          <n-avatar :size="72" round :style="{ backgroundColor: '#2D6A4F', fontSize: '28px' }">
            {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
          </n-avatar>
          <div class="profile-basic">
            <h2>{{ userStore.userInfo?.nickname || '用户' }}</h2>
            <n-tag size="small" :type="userStore.userInfo?.role === 'merchant' ? 'warning' : 'info'">
              {{ userStore.userInfo?.role === 'merchant' ? '商户' : '普通用户' }}
            </n-tag>
          </div>
        </div>

        <n-divider />

        <n-form
          ref="profileFormRef"
          :model="profileForm"
          :rules="profileRules"
          label-width="80px"
          label-placement="left"
        >
          <n-form-item label="昵称" path="nickname">
            <n-input v-model:value="profileForm.nickname" placeholder="请输入昵称" />
          </n-form-item>
          <n-form-item label="手机号" path="phone">
            <n-input v-model:value="profileForm.phone" placeholder="请输入手机号" />
          </n-form-item>
          <n-form-item label="邮箱" path="email">
            <n-input v-model:value="profileForm.email" placeholder="请输入邮箱" />
          </n-form-item>
          <n-form-item label=" ">
            <n-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">
              保存修改
            </n-button>
          </n-form-item>
        </n-form>
      </n-card>

      <!-- Balance Card -->
      <n-card title="我的余额" style="margin-bottom: 16px;">
        <div class="balance-section">
          <div class="balance-display">
            <span class="balance-label">当前余额</span>
            <span class="balance-value">¥{{ (userStore.userInfo?.balance || 0).toFixed(2) }}</span>
          </div>
          <div class="recharge-form">
            <n-input-number
              v-model:value="rechargeAmount"
              :min="1"
              :max="10000"
              placeholder="充值金额"
              style="width: 200px;"
            >
              <template #prefix>¥</template>
            </n-input-number>
            <n-button type="warning" :loading="rechargeLoading" @click="handleRecharge">
              充值
            </n-button>
          </div>
          <div class="quick-recharge">
            <n-button
              v-for="amount in [10, 20, 50, 100]"
              :key="amount"
              size="small"
              secondary
              @click="rechargeAmount = amount"
            >
              ¥{{ amount }}
            </n-button>
          </div>
        </div>
      </n-card>

      <!-- Change Password Card -->
      <n-card title="修改密码">
        <n-form
          ref="pwdFormRef"
          :model="pwdForm"
          :rules="pwdRules"
          label-width="100px"
          label-placement="left"
        >
          <n-form-item label="当前密码" path="oldPassword">
            <n-input
              v-model:value="pwdForm.oldPassword"
              type="password"
              show-password-on="click"
              placeholder="请输入当前密码"
            />
          </n-form-item>
          <n-form-item label="新密码" path="newPassword">
            <n-input
              v-model:value="pwdForm.newPassword"
              type="password"
              show-password-on="click"
              placeholder="请输入新密码"
            />
          </n-form-item>
          <n-form-item label="确认新密码" path="confirmPassword">
            <n-input
              v-model:value="pwdForm.confirmPassword"
              type="password"
              show-password-on="click"
              placeholder="请再次输入新密码"
            />
          </n-form-item>
          <n-form-item label=" ">
            <n-button type="primary" :loading="pwdLoading" @click="handleChangePassword">
              修改密码
            </n-button>
          </n-form-item>
        </n-form>
      </n-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { useUserStore } from '@/stores/user'
import { updateProfile, changePassword, recharge } from '@/api/user'

const message = useMessage()
const userStore = useUserStore()

// Profile form
const profileFormRef = ref<FormInst | null>(null)
const profileLoading = ref(false)
const profileForm = reactive({
  nickname: userStore.userInfo?.nickname || '',
  phone: userStore.userInfo?.phone || '',
  email: userStore.userInfo?.email || '',
})

const profileRules: FormRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
}

async function handleUpdateProfile() {
  try {
    await profileFormRef.value?.validate()
  } catch {
    return
  }
  profileLoading.value = true
  try {
    const res: any = await updateProfile({
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email,
    })
    if (res.code === 200 || res.code === 0) {
      message.success('更新成功')
      await userStore.fetchProfile()
    } else {
      message.error(res.message || '更新失败')
    }
  } catch {
    message.error('更新失败')
  } finally {
    profileLoading.value = false
  }
}

// Recharge
const rechargeAmount = ref<number | null>(50)
const rechargeLoading = ref(false)

async function handleRecharge() {
  if (!rechargeAmount.value || rechargeAmount.value <= 0) {
    message.warning('请输入充值金额')
    return
  }
  rechargeLoading.value = true
  try {
    const res: any = await recharge(rechargeAmount.value)
    if (res.code === 200 || res.code === 0) {
      message.success('充值成功')
      await userStore.fetchProfile()
    } else {
      message.error(res.message || '充值失败')
    }
  } catch {
    message.error('充值失败')
  } finally {
    rechargeLoading.value = false
  }
}

// Change Password
const pwdFormRef = ref<FormInst | null>(null)
const pwdLoading = ref(false)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
})

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码不少于6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string) => {
        if (value !== pwdForm.newPassword) return new Error('两次密码不一致')
        return true
      },
      trigger: 'blur',
    },
  ],
}

async function handleChangePassword() {
  try {
    await pwdFormRef.value?.validate()
  } catch {
    return
  }
  pwdLoading.value = true
  try {
    const res: any = await changePassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword,
    })
    if (res.code === 200 || res.code === 0) {
      message.success('密码修改成功，请重新登录')
      userStore.logout()
    } else {
      message.error(res.message || '修改失败')
    }
  } catch {
    message.error('修改失败')
  } finally {
    pwdLoading.value = false
  }
}

onMounted(() => {
  // Refresh profile data
  userStore.fetchProfile().then(() => {
    profileForm.nickname = userStore.userInfo?.nickname || ''
    profileForm.phone = userStore.userInfo?.phone || ''
    profileForm.email = userStore.userInfo?.email || ''
  })
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1E293B;
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.profile-basic h2 {
  font-size: 20px;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 6px;
}

.balance-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.balance-display {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.balance-label {
  font-size: 14px;
  color: #64748B;
}

.balance-value {
  font-size: 36px;
  font-weight: 800;
  color: #E07A5F;
}

.recharge-form {
  display: flex;
  gap: 12px;
  align-items: center;
}

.quick-recharge {
  display: flex;
  gap: 8px;
}
</style>
