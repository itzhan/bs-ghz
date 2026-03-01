<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">全部档口</h1>
      <p class="page-desc">探索校园内的各个美食档口</p>
    </div>

    <!-- Filters -->
    <div class="filter-bar">
      <n-input
        v-model:value="keyword"
        placeholder="搜索档口..."
        clearable
        style="max-width: 320px;"
        @keyup.enter="loadData"
      >
        <template #prefix>
          <n-icon :component="SearchOutline" />
        </template>
      </n-input>
      <n-select
        v-model:value="statusFilter"
        placeholder="营业状态"
        :options="statusOptions"
        clearable
        style="width: 140px;"
        @update:value="loadData"
      />
    </div>

    <!-- Stall Grid -->
    <n-spin :show="loading">
      <div class="stall-grid" v-if="stalls.length">
        <div
          v-for="stall in stalls"
          :key="stall.id"
          class="stall-card"
          @click="router.push(`/stalls/${stall.id}`)"
        >
          <div class="stall-img">
            <img
              :src="stall.logo || stall.image || PLACEHOLDER_STALL"
              :alt="stall.name"
            />
            <n-tag
              :type="stall.status === 1 ? 'success' : 'default'"
              size="small"
              class="status-tag"
            >
              {{ stall.status === 1 ? '营业中' : '休息中' }}
            </n-tag>
          </div>
          <div class="stall-body">
            <h3 class="stall-name">{{ stall.name }}</h3>
            <p class="stall-desc">{{ stall.description || '暂无描述' }}</p>
            <div class="stall-meta">
              <div class="meta-left">
                <n-rate :value="stall.rating || 0" :count="5" readonly size="small" />
                <span class="rating-text">{{ (stall.rating || 0).toFixed(1) }}</span>
              </div>
              <span class="meta-sales">月售 {{ stall.monthlySales || 0 }}</span>
            </div>
            <div class="stall-info">
              <n-icon :component="LocationOutline" size="14" color="#94A3B8" />
              <span>{{ stall.location || '—' }}</span>
              <n-icon :component="TimeOutline" size="14" color="#94A3B8" style="margin-left: 12px;" />
              <span>{{ stall.businessHours || '—' }}</span>
            </div>
          </div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="暂无档口数据" style="padding: 60px 0;" />
    </n-spin>

    <!-- Pagination -->
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
import { useRouter } from 'vue-router'
import { SearchOutline, LocationOutline, TimeOutline } from '@vicons/ionicons5'
import { getStalls } from '@/api/stall'
import { PLACEHOLDER_STALL } from '@/utils/placeholder'

const router = useRouter()
const loading = ref(false)
const stalls = ref<any[]>([])
const keyword = ref('')
const statusFilter = ref<number | null>(null)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const statusOptions = [
  { label: '营业中', value: 1 },
  { label: '休息中', value: 0 },
]

async function loadData() {
  loading.value = true
  try {
    const res: any = await getStalls({
      keyword: keyword.value || undefined,
      status: statusFilter.value ?? undefined,
      page: page.value,
      size: pageSize.value,
    })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      stalls.value = data?.records || data?.list || data || []
      total.value = data?.total || stalls.value.length
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
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.stall-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

@media (max-width: 1024px) {
  .stall-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .stall-grid { grid-template-columns: 1fr; }
}

.stall-card {
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.stall-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.stall-img {
  height: 180px;
  overflow: hidden;
  position: relative;
  background: #F1F5F9;
}

.stall-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.stall-body {
  padding: 16px;
}

.stall-name {
  font-size: 17px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 6px;
}

.stall-desc {
  font-size: 13px;
  color: #94A3B8;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.stall-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.rating-text {
  font-size: 14px;
  font-weight: 600;
  color: #F59E0B;
}

.meta-sales {
  font-size: 12px;
  color: #94A3B8;
}

.stall-info {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #94A3B8;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
