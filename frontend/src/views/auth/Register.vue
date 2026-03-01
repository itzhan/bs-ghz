<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1 class="auth-title">创建账号</h1>
          <p class="auth-subtitle">注册校园餐饮管理系统</p>
        </div>
        <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" size="large">
          <n-form-item path="username" label="">
            <n-input v-model:value="form.username" placeholder="用户名" clearable>
              <template #prefix>
                <n-icon :component="PersonOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="password" label="">
            <n-input
              v-model:value="form.password"
              type="password"
              show-password-on="click"
              placeholder="密码"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="confirmPassword" label="">
            <n-input
              v-model:value="form.confirmPassword"
              type="password"
              show-password-on="click"
              placeholder="确认密码"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="nickname" label="">
            <n-input v-model:value="form.nickname" placeholder="昵称" clearable>
              <template #prefix>
                <n-icon :component="HappyOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="phone" label="">
            <n-input v-model:value="form.phone" placeholder="手机号" clearable>
              <template #prefix>
                <n-icon :component="CallOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item path="role" label="">
            <n-select
              v-model:value="form.role"
              placeholder="选择角色"
              :options="roleOptions"
            />
          </n-form-item>
          <n-form-item label="">
            <n-button
              type="primary"
              block
              :loading="submitting"
              @click="handleRegister"
              style="height: 44px; font-size: 16px;"
            >
              注 册
            </n-button>
          </n-form-item>
        </n-form>
        <div class="auth-footer">
          已有账号？
          <router-link to="/login" class="link">立即登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { PersonOutline, LockClosedOutline, HappyOutline, CallOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const message = useMessage()
const userStore = useUserStore()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: '',
  role: 'user',
})

const roleOptions = [
  { label: '普通用户', value: 'user' },
  { label: '商户', value: 'merchant' },
]

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为 3-20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string) => {
        if (value !== form.password) return new Error('两次密码不一致')
        return true
      },
      trigger: 'blur',
    },
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
}

async function handleRegister() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    const res = await userStore.register({
      username: form.username,
      password: form.password,
      nickname: form.nickname,
      phone: form.phone,
      role: form.role,
    })
    message.success('注册成功，请登录')
    router.push('/login')
  } catch (e: any) {
    message.error(e?.response?.data?.message || '注册失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #F5F5F0 0%, #E8E4DF 100%);
  padding: 24px;
}

.auth-container {
  width: 100%;
  max-width: 420px;
}

.auth-card {
  background: #FFFFFF;
  border-radius: 16px;
  padding: 40px 36px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
}

.auth-header {
  text-align: center;
  margin-bottom: 32px;
}

.auth-title {
  font-size: 26px;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 8px;
}

.auth-subtitle {
  font-size: 14px;
  color: #94A3B8;
}

.auth-footer {
  text-align: center;
  font-size: 14px;
  color: #64748B;
  margin-top: 16px;
}

.link {
  color: #2D6A4F;
  font-weight: 600;
}

.link:hover {
  text-decoration: underline;
}
</style>
