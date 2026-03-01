<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-header">
          <h1 class="auth-title">欢迎回来</h1>
          <p class="auth-subtitle">登录校园餐饮管理系统</p>
        </div>
        <n-form ref="formRef" :model="form" :rules="rules" label-placement="left" size="large">
          <n-form-item path="username" label="">
            <n-input v-model:value="form.username" placeholder="请输入用户名" clearable>
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
              placeholder="请输入密码"
            >
              <template #prefix>
                <n-icon :component="LockClosedOutline" color="#94A3B8" />
              </template>
            </n-input>
          </n-form-item>
          <n-form-item label="">
            <n-button
              type="primary"
              block
              :loading="submitting"
              @click="handleLogin"
              style="height: 44px; font-size: 16px;"
            >
              登 录
            </n-button>
          </n-form-item>
        </n-form>
        <div class="auth-footer">
          还没有账号？
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import { PersonOutline, LockClosedOutline } from '@vicons/ionicons5'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()
const formRef = ref<FormInst | null>(null)
const submitting = ref(false)

const form = reactive({
  username: '',
  password: '',
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    await userStore.login(form.username, form.password)
    message.success('登录成功')
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    message.error(e.message || '登录失败，请检查用户名和密码')
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
