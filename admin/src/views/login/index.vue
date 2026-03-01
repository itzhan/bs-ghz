<template>
  <div class="login-container">
    <div class="login-card">
      <SwitchDark class="dark-switch" />

      <!-- 左侧品牌 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="brand-icon">🍽️</div>
          <h1 class="brand-title">校园餐饮管理系统</h1>
          <p class="brand-desc">高效管理 · 智慧运营</p>
        </div>
      </div>

      <!-- 右侧表单 -->
      <div class="form-section">
        <div class="form-header">
          <h3 class="form-title">管理员登录</h3>
        </div>
        <LoginForm />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="login">
import LoginForm from "./components/LoginForm.vue";
import SwitchDark from "@/components/SwitchDark/index.vue";
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
