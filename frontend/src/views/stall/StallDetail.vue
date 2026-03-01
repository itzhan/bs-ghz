<template>
  <div class="page-container">
    <n-spin :show="loading">
      <!-- Breadcrumb -->
      <n-breadcrumb style="margin-bottom: 20px;">
        <n-breadcrumb-item @click="router.push('/')">首页</n-breadcrumb-item>
        <n-breadcrumb-item @click="router.push('/stalls')">档口</n-breadcrumb-item>
        <n-breadcrumb-item>{{ stall.name || '详情' }}</n-breadcrumb-item>
      </n-breadcrumb>

      <!-- Stall Header -->
      <div class="stall-header" v-if="stall.id">
        <div class="stall-cover">
          <img
            :src="stall.logo || stall.image || PLACEHOLDER_STALL"
            :alt="stall.name"
          />
        </div>
        <div class="stall-info">
          <div class="stall-title-row">
            <h1 class="stall-name">{{ stall.name }}</h1>
            <n-tag :type="stall.status === 1 ? 'success' : 'default'" size="medium">
              {{ stall.status === 1 ? '营业中' : '休息中' }}
            </n-tag>
          </div>
          <p class="stall-desc">{{ stall.description || '暂无描述' }}</p>
          <div class="stall-meta-row">
            <div class="meta-item">
              <n-rate :value="stall.rating || 0" :count="5" readonly size="small" />
              <span class="rating-text">{{ (stall.rating || 0).toFixed(1) }} 分</span>
            </div>
            <div class="meta-item">
              <n-icon :component="LocationOutline" size="16" />
              <span>{{ stall.location || '—' }}</span>
            </div>
            <div class="meta-item">
              <n-icon :component="TimeOutline" size="16" />
              <span>{{ stall.businessHours || '—' }}</span>
            </div>
          </div>
          <div class="stall-actions">
            <n-button
              :type="isFavorited ? 'primary' : 'default'"
              @click="toggleFavorite"
              :loading="favLoading"
            >
              <template #icon>
                <n-icon :component="isFavorited ? Heart : HeartOutline" />
              </template>
              {{ isFavorited ? '已收藏' : '收藏' }}
            </n-button>
          </div>
        </div>
      </div>

      <!-- Tabs -->
      <n-tabs type="line" animated style="margin-top: 24px;" v-model:value="activeTab">
        <!-- Menu Tab -->
        <n-tab-pane name="menu" tab="菜单">
          <div class="menu-section">
            <!-- Category filter -->
            <div class="category-bar" v-if="categories.length">
              <n-tag
                v-for="cat in [{ id: 0, name: '全部' }, ...categories]"
                :key="cat.id"
                :type="selectedCategory === cat.id ? 'primary' : 'default'"
                :bordered="selectedCategory !== cat.id"
                style="cursor: pointer;"
                @click="selectedCategory = cat.id; loadDishes()"
              >
                {{ cat.name }}
              </n-tag>
            </div>
            <!-- Dish List -->
            <n-spin :show="dishLoading">
              <div class="dish-list" v-if="dishes.length">
                <div v-for="dish in dishes" :key="dish.id" class="dish-item">
                  <div class="dish-img" @click="router.push(`/dishes/${dish.id}`)">
                    <img
                      :src="dish.image || PLACEHOLDER_SMALL"
                      :alt="dish.name"
                    />
                  </div>
                  <div class="dish-info">
                    <h3 class="dish-name" @click="router.push(`/dishes/${dish.id}`)">{{ dish.name }}</h3>
                    <p class="dish-desc">{{ dish.description || '暂无描述' }}</p>
                    <div class="dish-bottom">
                      <span class="dish-price">¥{{ dish.price?.toFixed(2) }}</span>
                      <span class="dish-sales">月售 {{ dish.monthlySales || 0 }}</span>
                    </div>
                  </div>
                  <div class="dish-action">
                    <n-button
                      type="warning"
                      size="small"
                      circle
                      @click="handleAddToCart(dish)"
                    >
                      <template #icon><n-icon :component="AddOutline" /></template>
                    </n-button>
                  </div>
                </div>
              </div>
              <n-empty v-else-if="!dishLoading" description="暂无菜品" />
            </n-spin>
          </div>
        </n-tab-pane>

        <!-- Reviews Tab -->
        <n-tab-pane name="reviews" tab="评价">
          <n-spin :show="reviewLoading">
            <div v-if="reviews.length">
              <div v-for="review in reviews" :key="review.id" class="review-item">
                <div class="review-header">
                  <n-avatar :size="36" round style="background: #2D6A4F;">
                    {{ review.nickname?.charAt(0) || 'U' }}
                  </n-avatar>
                  <div class="review-user">
                    <span class="review-name">{{ review.nickname || '匿名用户' }}</span>
                    <n-rate :value="review.rating || 0" :count="5" readonly size="small" />
                  </div>
                  <span class="review-time">{{ review.createTime || '' }}</span>
                </div>
                <p class="review-content">{{ review.content }}</p>
              </div>
            </div>
            <n-empty v-else-if="!reviewLoading" description="暂无评价" />
          </n-spin>
        </n-tab-pane>

        <!-- Activities Tab -->
        <n-tab-pane name="activities" tab="活动/优惠">
          <n-spin :show="activityLoading">
            <div v-if="activities.length">
              <n-card v-for="act in activities" :key="act.id" style="margin-bottom: 12px;">
                <div class="activity-item">
                  <n-tag type="warning" size="small">{{ act.type || '优惠' }}</n-tag>
                  <h4 style="margin: 8px 0 4px;">{{ act.title }}</h4>
                  <p style="color: #64748B; font-size: 13px;">{{ act.description }}</p>
                  <p style="color: #94A3B8; font-size: 12px; margin-top: 6px;">
                    {{ act.startTime }} ~ {{ act.endTime }}
                  </p>
                </div>
              </n-card>
            </div>
            <n-empty v-else-if="!activityLoading" description="暂无活动" />
          </n-spin>
        </n-tab-pane>
      </n-tabs>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import {
  LocationOutline,
  TimeOutline,
  HeartOutline,
  Heart,
  AddOutline,
} from '@vicons/ionicons5'
import { getStallById, getStallDishes, getStallCategories, getStallReviews, getStallActivities } from '@/api/stall'
import { addToCart } from '@/api/cart'
import { addFavorite, removeFavorite, checkFavorite } from '@/api/favorite'
import { useUserStore } from '@/stores/user'
import { PLACEHOLDER_STALL, PLACEHOLDER_SMALL } from '@/utils/placeholder'

const router = useRouter()
const route = useRoute()
const message = useMessage()
const userStore = useUserStore()

const stallId = Number(route.params.id)
const loading = ref(false)
const stall = ref<any>({})
const activeTab = ref('menu')

// Menu
const categories = ref<any[]>([])
const selectedCategory = ref(0)
const dishes = ref<any[]>([])
const dishLoading = ref(false)

// Reviews
const reviews = ref<any[]>([])
const reviewLoading = ref(false)

// Activities
const activities = ref<any[]>([])
const activityLoading = ref(false)

// Favorite
const isFavorited = ref(false)
const favLoading = ref(false)
const favoriteId = ref<number | null>(null)

async function loadStall() {
  loading.value = true
  try {
    const res: any = await getStallById(stallId)
    if (res.code === 200 || res.code === 0) {
      stall.value = res.data || {}
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  try {
    const res: any = await getStallCategories(stallId)
    if (res.code === 200 || res.code === 0) {
      categories.value = res.data || []
    }
  } catch {
    // ignore
  }
}

async function loadDishes() {
  dishLoading.value = true
  try {
    const res: any = await getStallDishes(stallId, {
      categoryId: selectedCategory.value || undefined,
    })
    if (res.code === 200 || res.code === 0) {
      dishes.value = res.data?.records || res.data?.list || res.data || []
    }
  } catch {
    // ignore
  } finally {
    dishLoading.value = false
  }
}

async function loadReviews() {
  reviewLoading.value = true
  try {
    const res: any = await getStallReviews(stallId, { page: 1, pageSize: 20 })
    if (res.code === 200 || res.code === 0) {
      reviews.value = res.data?.records || res.data?.list || res.data || []
    }
  } catch {
    // ignore
  } finally {
    reviewLoading.value = false
  }
}

async function loadActivities() {
  activityLoading.value = true
  try {
    const res: any = await getStallActivities(stallId)
    if (res.code === 200 || res.code === 0) {
      activities.value = res.data || []
    }
  } catch {
    // ignore
  } finally {
    activityLoading.value = false
  }
}

async function checkFav() {
  if (!userStore.isLoggedIn()) return
  try {
    const res: any = await checkFavorite(stallId, 1)
    if (res.code === 200 || res.code === 0) {
      isFavorited.value = !!res.data?.favorited || !!res.data
      favoriteId.value = res.data?.id || null
    }
  } catch {
    // ignore
  }
}

async function toggleFavorite() {
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  favLoading.value = true
  try {
    if (isFavorited.value && favoriteId.value) {
      await removeFavorite(favoriteId.value)
      isFavorited.value = false
      favoriteId.value = null
      message.success('已取消收藏')
    } else {
      const res: any = await addFavorite({ stallId: stallId, type: 1 })
      isFavorited.value = true
      favoriteId.value = res.data?.id || null
      message.success('收藏成功')
    }
  } catch {
    message.error('操作失败')
  } finally {
    favLoading.value = false
  }
}

async function handleAddToCart(dish: any) {
  if (!userStore.isLoggedIn()) {
    message.warning('请先登录')
    router.push('/login')
    return
  }
  try {
    const res: any = await addToCart({ dishId: dish.id, quantity: 1 })
    if (res.code === 200 || res.code === 0) {
      message.success(`已添加 ${dish.name} 到购物车`)
    } else {
      message.error(res.message || '添加失败')
    }
  } catch {
    message.error('添加失败')
  }
}

onMounted(() => {
  loadStall()
  loadCategories()
  loadDishes()
  loadReviews()
  loadActivities()
  checkFav()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 24px 60px;
}

.stall-header {
  display: flex;
  gap: 24px;
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.stall-cover {
  width: 360px;
  min-height: 240px;
  flex-shrink: 0;
  background: #F1F5F9;
}

.stall-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.stall-info {
  flex: 1;
  padding: 24px 24px 24px 0;
  display: flex;
  flex-direction: column;
}

.stall-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.stall-name {
  font-size: 24px;
  font-weight: 700;
  color: #1E293B;
}

.stall-desc {
  font-size: 14px;
  color: #64748B;
  margin-bottom: 16px;
}

.stall-meta-row {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748B;
}

.rating-text {
  font-weight: 600;
  color: #F59E0B;
}

.stall-actions {
  margin-top: auto;
}

@media (max-width: 768px) {
  .stall-header {
    flex-direction: column;
  }
  .stall-cover {
    width: 100%;
    height: 200px;
  }
  .stall-info {
    padding: 16px;
  }
}

/* Menu */
.menu-section {
  padding-top: 8px;
}

.category-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.dish-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.dish-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #FFFFFF;
  border-radius: 10px;
  padding: 12px 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.04);
}

.dish-img {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  cursor: pointer;
  background: #F1F5F9;
}

.dish-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  flex: 1;
  min-width: 0;
}

.dish-name {
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  cursor: pointer;
}

.dish-name:hover {
  color: #2D6A4F;
}

.dish-desc {
  font-size: 12px;
  color: #94A3B8;
  margin: 4px 0 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.dish-bottom {
  display: flex;
  align-items: center;
  gap: 12px;
}

.dish-price {
  font-size: 18px;
  font-weight: 700;
  color: #E07A5F;
}

.dish-sales {
  font-size: 12px;
  color: #94A3B8;
}

.dish-action {
  flex-shrink: 0;
}

/* Reviews */
.review-item {
  padding: 16px 0;
  border-bottom: 1px solid #F1F5F9;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.review-user {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.review-name {
  font-size: 14px;
  font-weight: 600;
  color: #1E293B;
}

.review-time {
  margin-left: auto;
  font-size: 12px;
  color: #94A3B8;
}

.review-content {
  font-size: 14px;
  color: #475569;
  line-height: 1.6;
}
</style>
