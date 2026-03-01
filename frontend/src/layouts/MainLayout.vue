<template>
  <n-layout style="min-height: 100vh">
    <!-- Top Navbar -->
    <n-layout-header bordered style="height: 64px; padding: 0 24px; display: flex; align-items: center; background: #FFFFFF; position: sticky; top: 0; z-index: 100;">
      <div class="navbar">
        <div class="navbar-left">
          <router-link to="/" class="logo">
            <span class="logo-icon">🍜</span>
            <span class="logo-text">校园餐饮</span>
          </router-link>
          <nav class="nav-links">
            <router-link to="/" class="nav-link" :class="{ active: route.path === '/' }">首页</router-link>
            <router-link to="/stalls" class="nav-link" :class="{ active: route.path.startsWith('/stalls') }">档口</router-link>
            <router-link to="/dishes" class="nav-link" :class="{ active: route.path.startsWith('/dishes') }">菜品</router-link>
            <router-link v-if="userStore.userInfo?.role === 'merchant' || userStore.userInfo?.role === 1" to="/merchant/stalls" class="nav-link" :class="{ active: route.path.startsWith('/merchant') }">档口管理</router-link>
          </nav>
        </div>
        <div class="navbar-right">
          <template v-if="userStore.isLoggedIn()">
            <router-link to="/cart" class="nav-icon-link">
              <n-badge :value="cartCount" :max="99">
                <n-icon size="22" color="#64748B">
                  <CartOutline />
                </n-icon>
              </n-badge>
            </router-link>
            <n-dropdown :options="userMenuOptions" @select="handleUserMenu" trigger="click">
              <div class="user-trigger">
                <n-avatar :size="32" round :style="{ backgroundColor: '#2D6A4F', cursor: 'pointer' }">
                  {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                </n-avatar>
                <span class="user-name">{{ userStore.userInfo?.nickname || '用户' }}</span>
              </div>
            </n-dropdown>
          </template>
          <template v-else>
            <n-space>
              <n-button text @click="router.push('/login')" style="color: #2D6A4F; font-weight: 500;">登录</n-button>
              <n-button type="primary" size="small" round @click="router.push('/register')">注册</n-button>
            </n-space>
          </template>
        </div>
      </div>
    </n-layout-header>

    <!-- Content -->
    <n-layout-content style="min-height: calc(100vh - 64px - 60px);">
      <router-view />
    </n-layout-content>

    <!-- Footer -->
    <n-layout-footer style="height: 60px; display: flex; align-items: center; justify-content: center; background: #FFFFFF; border-top: 1px solid #E5E5E0;">
      <n-text depth="3" style="font-size: 13px;">
        © 2026 校园餐饮管理系统 · 毕业设计
      </n-text>
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { h, ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { NIcon } from 'naive-ui'
import {
  CartOutline,
  ReceiptOutline,
  HeartOutline,
  PersonOutline,
  ChatboxEllipsesOutline,
  LogOutOutline,
  StorefrontOutline,
} from '@vicons/ionicons5'
import type { DropdownOption } from 'naive-ui'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const cartCount = ref(0)

const renderIcon = (icon: any) => {
  return () => h(NIcon, null, { default: () => h(icon) })
}

const isMerchant = computed(() => {
  const role = userStore.userInfo?.role
  return role === 'merchant' || role === 1
})

const userMenuOptions = computed<DropdownOption[]>(() => {
  const items: DropdownOption[] = [
    { label: '我的订单', key: 'orders', icon: renderIcon(ReceiptOutline) },
    { label: '我的收藏', key: 'favorites', icon: renderIcon(HeartOutline) },
    { label: '我的投诉', key: 'complaints', icon: renderIcon(ChatboxEllipsesOutline) },
  ]
  if (isMerchant.value) {
    items.push({ label: '我的档口', key: 'merchant/stalls', icon: renderIcon(StorefrontOutline) })
  }
  items.push(
    { label: '个人中心', key: 'profile', icon: renderIcon(PersonOutline) },
    { type: 'divider', key: 'd1' },
    { label: '退出登录', key: 'logout', icon: renderIcon(LogOutOutline) },
  )
  return items
})

function handleUserMenu(key: string) {
  if (key === 'logout') {
    userStore.logout()
    router.push('/')
  } else {
    router.push(`/${key}`)
  }
}
</script>

<style scoped>
.navbar {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 32px;
}

.navbar-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 700;
  color: #2D6A4F;
}

.logo-icon {
  font-size: 24px;
}

.logo-text {
  letter-spacing: 1px;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  padding: 6px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #64748B;
  transition: all 0.2s;
}

.nav-link:hover {
  color: #2D6A4F;
  background: rgba(45, 106, 79, 0.06);
}

.nav-link.active {
  color: #2D6A4F;
  background: rgba(45, 106, 79, 0.1);
}

.nav-icon-link {
  display: flex;
  align-items: center;
  padding: 4px;
  cursor: pointer;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-trigger:hover {
  background: rgba(0, 0, 0, 0.04);
}

.user-name {
  font-size: 14px;
  color: #1E293B;
  font-weight: 500;
}
</style>
