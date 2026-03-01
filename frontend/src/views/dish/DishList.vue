<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">全部菜品</h1>
      <p class="page-desc">发现校园里的各种美味</p>
    </div>

    <!-- Filters -->
    <div class="filter-bar">
      <n-input
        v-model:value="keyword"
        placeholder="搜索菜品..."
        clearable
        style="max-width: 320px;"
        @keyup.enter="loadData"
      >
        <template #prefix>
          <n-icon :component="SearchOutline" />
        </template>
      </n-input>
      <n-input-number
        v-model:value="minPrice"
        placeholder="最低价"
        :min="0"
        style="width: 120px;"
        @update:value="loadData"
      />
      <span style="color: #94A3B8;">—</span>
      <n-input-number
        v-model:value="maxPrice"
        placeholder="最高价"
        :min="0"
        style="width: 120px;"
        @update:value="loadData"
      />
    </div>

    <!-- Dish Grid -->
    <n-spin :show="loading">
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
            <p class="dish-card-desc" v-if="dish.description">{{ dish.description }}</p>
            <p class="dish-card-stall">{{ dish.stallName || '—' }}</p>
            <div class="dish-card-footer">
              <span class="dish-price">¥{{ dish.price?.toFixed(2) }}</span>
              <n-rate :value="dish.rating || 0" :count="5" readonly size="small" />
            </div>
          </div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="暂无菜品" style="padding: 60px 0;" />
    </n-spin>

    <div class="pagination-wrap" v-if="total > pageSize">
      <n-pagination
        v-model:page="page"
        :page-size="pageSize"
        :item-count="total"
        @update:page="loadData"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { SearchOutline } from '@vicons/ionicons5'
import { getDishes } from '@/api/dish'
import { PLACEHOLDER_DISH } from '@/utils/placeholder'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const dishes = ref<any[]>([])
const keyword = ref((route.query.keyword as string) || '')
const minPrice = ref<number | null>(null)
const maxPrice = ref<number | null>(null)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

async function loadData() {
  loading.value = true
  try {
    const res: any = await getDishes({
      keyword: keyword.value || undefined,
      minPrice: minPrice.value ?? undefined,
      maxPrice: maxPrice.value ?? undefined,
      page: page.value,
      size: pageSize.value,
    })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      dishes.value = data?.records || data?.list || data || []
      total.value = data?.total || dishes.value.length
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
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

.page-desc {
  font-size: 14px;
  color: #94A3B8;
  margin-top: 4px;
}

.filter-bar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

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
  height: 170px;
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
  margin-bottom: 10px;
}

.dish-card-desc {
  font-size: 12px;
  color: #94A3B8;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
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

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
