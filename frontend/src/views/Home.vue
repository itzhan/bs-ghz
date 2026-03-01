<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">校园餐饮，触手可及</h1>
        <p class="hero-subtitle">探索校园美味，享受便捷点餐体验</p>
        <div class="hero-search">
          <n-input
            v-model:value="searchKeyword"
            placeholder="搜索档口或菜品..."
            size="large"
            round
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <n-icon :component="SearchOutline" />
            </template>
            <template #suffix>
              <n-button type="primary" size="small" round @click="handleSearch">
                搜索
              </n-button>
            </template>
          </n-input>
        </div>
      </div>
    </section>

    <div class="home-body">
      <!-- 推荐档口 -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">推荐档口</h2>
          <n-button text type="primary" @click="router.push('/stalls')">
            查看全部 →
          </n-button>
        </div>
        <n-spin :show="loading.stalls">
          <div class="stall-scroll" v-if="stalls.length">
            <div
              v-for="stall in stalls"
              :key="stall.id"
              class="stall-card"
              @click="router.push(`/stalls/${stall.id}`)"
            >
              <div class="stall-card-img">
                <img
                  :src="stall.logo || stall.image || PLACEHOLDER_STALL"
                  :alt="stall.name"
                />
              </div>
              <div class="stall-card-body">
                <h3 class="stall-card-name">{{ stall.name }}</h3>
                <p class="stall-card-desc">{{ stall.description || '暂无描述' }}</p>
                <div class="stall-card-meta">
                  <n-rate :value="stall.rating || 0" :count="5" readonly size="small" />
                  <span class="meta-text">月售 {{ stall.monthlySales || 0 }}</span>
                </div>
              </div>
            </div>
          </div>
          <n-empty v-else description="暂无推荐档口" />
        </n-spin>
      </section>

      <!-- 热门菜品 -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">热门菜品</h2>
          <n-button text type="primary" @click="router.push('/dishes')">
            查看全部 →
          </n-button>
        </div>
        <n-spin :show="loading.dishes">
          <div class="dish-grid" v-if="dishes.length">
            <div
              v-for="dish in dishes"
              :key="dish.id"
              class="dish-card"
              @click="router.push(`/dishes/${dish.id}`)"
            >
              <div class="dish-card-img">
                <img
                  :src="dish.image || PLACEHOLDER_DISH"
                  :alt="dish.name"
                />
              </div>
              <div class="dish-card-body">
                <h3 class="dish-card-name">{{ dish.name }}</h3>
                <p class="dish-card-stall">{{ dish.description || dish.stallName || '' }}</p>
                <div class="dish-card-footer">
                  <span class="dish-price">¥{{ dish.price?.toFixed(2) }}</span>
                  <n-rate :value="dish.rating || 0" :count="5" readonly size="small" />
                </div>
              </div>
            </div>
          </div>
          <n-empty v-else description="暂无热门菜品" />
        </n-spin>
      </section>

      <!-- 数据概览 -->
      <section class="section">
        <div class="section-header">
          <h2 class="section-title">数据概览</h2>
        </div>
        <div class="stats-grid">
          <div class="stat-card" v-for="stat in statsList" :key="stat.label">
            <div class="stat-icon" :style="{ background: stat.bg }">
              <n-icon :size="24" :color="stat.color" :component="stat.icon" />
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ stat.value }}</span>
              <span class="stat-label">{{ stat.label }}</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  SearchOutline,
  StorefrontOutline,
  RestaurantOutline,
  PeopleOutline,
  ReceiptOutline,
} from '@vicons/ionicons5'
import { getStalls } from '@/api/stall'
import { getDishes } from '@/api/dish'
import { getDashboardStats } from '@/api/stats'
import { PLACEHOLDER_STALL, PLACEHOLDER_DISH } from '@/utils/placeholder'

const router = useRouter()
const searchKeyword = ref('')
const stalls = ref<any[]>([])
const dishes = ref<any[]>([])
const stats = ref<any>({})

const loading = reactive({
  stalls: false,
  dishes: false,
})

const statsList = computed(() => [
  {
    label: '总档口数',
    value: stats.value.totalStalls ?? stats.value.stallCount ?? 0,
    icon: StorefrontOutline,
    color: '#2D6A4F',
    bg: 'rgba(45, 106, 79, 0.1)',
  },
  {
    label: '总订单数',
    value: stats.value.totalOrders ?? stats.value.orderCount ?? 0,
    icon: RestaurantOutline,
    color: '#E07A5F',
    bg: 'rgba(224, 122, 95, 0.1)',
  },
  {
    label: '注册用户',
    value: stats.value.totalUsers ?? stats.value.userCount ?? 0,
    icon: PeopleOutline,
    color: '#3B82F6',
    bg: 'rgba(59, 130, 246, 0.1)',
  },
  {
    label: '总营收',
    value: stats.value.totalRevenue != null ? `¥${Number(stats.value.totalRevenue).toFixed(0)}` : '¥0',
    icon: ReceiptOutline,
    color: '#F59E0B',
    bg: 'rgba(245, 158, 11, 0.1)',
  },
])

function handleSearch() {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/dishes', query: { keyword: searchKeyword.value.trim() } })
  }
}

async function loadStalls() {
  loading.stalls = true
  try {
    const res: any = await getStalls({ page: 1, pageSize: 6 })
    if (res.code === 200 || res.code === 0) {
      stalls.value = res.data?.records || res.data?.list || res.data || []
    }
  } catch {
    // ignore
  } finally {
    loading.stalls = false
  }
}

async function loadDishes() {
  loading.dishes = true
  try {
    const res: any = await getDishes({ page: 1, pageSize: 8 })
    if (res.code === 200 || res.code === 0) {
      dishes.value = res.data?.records || res.data?.list || res.data || []
    }
  } catch {
    // ignore
  } finally {
    loading.dishes = false
  }
}

async function loadStats() {
  try {
    const res: any = await getDashboardStats()
    if (res.code === 200 || res.code === 0) {
      stats.value = res.data || {}
    }
  } catch {
    // ignore
  }
}

onMounted(() => {
  loadStalls()
  loadDishes()
  loadStats()
})
</script>

<style scoped>
.home-page {
  min-height: 100%;
}

.hero {
  background: linear-gradient(135deg, #F5F5F0 0%, #E8E4DF 100%);
  padding: 80px 24px 60px;
  text-align: center;
}

.hero-content {
  max-width: 600px;
  margin: 0 auto;
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  color: #1E293B;
  margin-bottom: 12px;
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 16px;
  color: #64748B;
  margin-bottom: 32px;
}

.hero-search {
  max-width: 480px;
  margin: 0 auto;
}

.home-body {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px 60px;
}

.section {
  margin-bottom: 48px;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.section-title {
  font-size: 22px;
  font-weight: 700;
  color: #1E293B;
}

/* Stall horizontal scroll */
.stall-scroll {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 8px;
  scroll-snap-type: x mandatory;
}

.stall-scroll::-webkit-scrollbar {
  height: 4px;
}

.stall-scroll::-webkit-scrollbar-thumb {
  background: #CBD5E1;
  border-radius: 2px;
}

.stall-card {
  min-width: 260px;
  max-width: 260px;
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  scroll-snap-align: start;
  flex-shrink: 0;
}

.stall-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stall-card-img {
  height: 140px;
  overflow: hidden;
  background: #F1F5F9;
}

.stall-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.stall-card-body {
  padding: 14px 16px;
}

.stall-card-name {
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stall-card-desc {
  font-size: 12px;
  color: #94A3B8;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stall-card-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.meta-text {
  font-size: 12px;
  color: #94A3B8;
}

/* Dish grid */
.dish-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

@media (max-width: 1024px) {
  .dish-grid { grid-template-columns: repeat(3, 1fr); }
}

@media (max-width: 768px) {
  .dish-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 480px) {
  .dish-grid { grid-template-columns: 1fr; }
}

.dish-card {
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.dish-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.dish-card-img {
  height: 160px;
  overflow: hidden;
  background: #F1F5F9;
}

.dish-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-card-body {
  padding: 14px 16px;
}

.dish-card-name {
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 4px;
}

.dish-card-stall {
  font-size: 12px;
  color: #94A3B8;
  margin-bottom: 8px;
}

.dish-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #E07A5F;
}

/* Stats grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

@media (max-width: 768px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}

.stat-card {
  background: #FFFFFF;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1E293B;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #94A3B8;
  margin-top: 2px;
}
</style>
