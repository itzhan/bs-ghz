<template>
  <div class="page-container">
    <n-spin :show="loading">
      <n-breadcrumb style="margin-bottom: 20px;">
        <n-breadcrumb-item @click="router.push('/')">首页</n-breadcrumb-item>
        <n-breadcrumb-item @click="router.push('/dishes')">菜品</n-breadcrumb-item>
        <n-breadcrumb-item>{{ dish.name || '详情' }}</n-breadcrumb-item>
      </n-breadcrumb>

      <div class="dish-detail" v-if="dish.id">
        <div class="dish-image">
          <img
            :src="dish.image || PLACEHOLDER_DISH"
            :alt="dish.name"
          />
        </div>
        <div class="dish-info">
          <h1 class="dish-name">{{ dish.name }}</h1>
          <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
          <div class="dish-price-row">
            <span class="dish-price">¥{{ dish.price?.toFixed(2) }}</span>
            <n-rate :value="dish.rating || 0" :count="5" readonly />
          </div>
          <div class="dish-meta">
            <div class="meta-item" v-if="dish.stallName || dish.stallId">
              <span class="meta-label">所属档口</span>
              <n-button
                text
                type="primary"
                @click="router.push(`/stalls/${dish.stallId}`)"
              >
                {{ dish.stallName || '查看档口' }}
              </n-button>
            </div>
            <div class="meta-item" v-if="dish.categoryName">
              <span class="meta-label">分类</span>
              <span>{{ dish.categoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">月售</span>
              <span>{{ dish.monthlySales || 0 }} 份</span>
            </div>
          </div>

          <n-divider />

          <div class="add-cart-section">
            <div class="quantity-row">
              <span style="color: #64748B;">数量</span>
              <n-input-number
                v-model:value="quantity"
                :min="1"
                :max="99"
                size="medium"
                style="width: 120px;"
              />
            </div>
            <n-button
              type="warning"
              size="large"
              block
              @click="handleAddToCart"
              :loading="cartLoading"
              style="height: 48px; font-size: 16px; margin-top: 16px;"
            >
              <template #icon>
                <n-icon :component="CartOutline" />
              </template>
              加入购物车
            </n-button>
          </div>
        </div>
      </div>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { CartOutline } from '@vicons/ionicons5'
import { getDishById } from '@/api/dish'
import { addToCart } from '@/api/cart'
import { useUserStore } from '@/stores/user'
import { PLACEHOLDER_DISH } from '@/utils/placeholder'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const dishId = Number(route.params.id)
const loading = ref(false)
const dish = ref<any>({})
const quantity = ref(1)
const cartLoading = ref(false)

async function loadDish() {
  loading.value = true
  try {
    const res: any = await getDishById(dishId)
    if (res.code === 200 || res.code === 0) {
      dish.value = res.data || {}
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleAddToCart() {
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  cartLoading.value = true
  try {
    const res: any = await addToCart({ dishId: dish.value.id, quantity: quantity.value })
    if (res.code === 200 || res.code === 0) {
      message.success('已加入购物车')
    } else {
      message.error(res.message || '添加失败')
    }
  } catch {
    message.error('添加失败')
  } finally {
    cartLoading.value = false
  }
}

onMounted(() => {
  loadDish()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 24px 60px;
}

.dish-detail {
  display: flex;
  gap: 40px;
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.dish-image {
  width: 420px;
  min-height: 360px;
  flex-shrink: 0;
  background: #F1F5F9;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  flex: 1;
  padding: 32px 32px 32px 0;
}

.dish-name {
  font-size: 26px;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 8px;
}

.dish-desc {
  font-size: 14px;
  color: #64748B;
  margin-bottom: 20px;
  line-height: 1.6;
}

.dish-price-row {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.dish-price {
  font-size: 32px;
  font-weight: 800;
  color: #E07A5F;
}

.dish-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #475569;
}

.meta-label {
  color: #94A3B8;
  min-width: 60px;
}

.add-cart-section {
  margin-top: 8px;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 16px;
}

@media (max-width: 768px) {
  .dish-detail {
    flex-direction: column;
  }
  .dish-image {
    width: 100%;
    height: 260px;
  }
  .dish-info {
    padding: 24px;
  }
}
</style>
