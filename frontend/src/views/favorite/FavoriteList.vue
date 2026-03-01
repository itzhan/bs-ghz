<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的收藏</h1>
    </div>

    <n-tabs type="line" v-model:value="activeType" @update:value="loadFavorites">
      <n-tab-pane name="stall" tab="收藏的档口">
        <n-spin :show="loading">
          <div class="fav-grid" v-if="favorites.length">
            <div
              v-for="item in favorites"
              :key="item.id"
              class="fav-card"
              @click="router.push(`/stalls/${item.targetId}`)"
            >
              <div class="fav-img">
                <img
                  :src="item.image || PLACEHOLDER_STALL"
                  :alt="item.name"
                />
              </div>
              <div class="fav-body">
                <h3 class="fav-name">{{ item.name || '档口' }}</h3>
                <p class="fav-desc">{{ item.description || '暂无描述' }}</p>
                <div class="fav-footer">
                  <n-rate :value="item.rating || 0" :count="5" readonly size="small" />
                  <n-button
                    text
                    type="error"
                    size="small"
                    @click.stop="handleRemove(item)"
                  >
                    取消收藏
                  </n-button>
                </div>
              </div>
            </div>
          </div>
          <n-empty v-else-if="!loading" description="暂无收藏的档口" style="padding: 60px 0;" />
        </n-spin>
      </n-tab-pane>

      <n-tab-pane name="dish" tab="收藏的菜品">
        <n-spin :show="loading">
          <div class="fav-grid" v-if="favorites.length">
            <div
              v-for="item in favorites"
              :key="item.id"
              class="fav-card"
              @click="router.push(`/dishes/${item.targetId}`)"
            >
              <div class="fav-img">
                <img
                  :src="item.image || PLACEHOLDER_DISH"
                  :alt="item.name"
                />
              </div>
              <div class="fav-body">
                <h3 class="fav-name">{{ item.name || '菜品' }}</h3>
                <div class="fav-footer">
                  <span class="fav-price" v-if="item.price">¥{{ item.price?.toFixed(2) }}</span>
                  <n-button
                    text
                    type="error"
                    size="small"
                    @click.stop="handleRemove(item)"
                  >
                    取消收藏
                  </n-button>
                </div>
              </div>
            </div>
          </div>
          <n-empty v-else-if="!loading" description="暂无收藏的菜品" style="padding: 60px 0;" />
        </n-spin>
      </n-tab-pane>
    </n-tabs>

    <div class="pagination-wrap" v-if="total > pageSize">
      <n-pagination
        v-model:page="page"
        :page-size="pageSize"
        :item-count="total"
        @update:page="loadFavorites"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { getFavorites, removeFavorite } from '@/api/favorite'
import { getStallById } from '@/api/stall'
import { getDishById } from '@/api/dish'
import { PLACEHOLDER_STALL, PLACEHOLDER_DISH } from '@/utils/placeholder'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const favorites = ref<any[]>([])
const activeType = ref('stall')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

async function loadFavorites() {
  loading.value = true
  try {
    const typeMap: Record<string, number> = { stall: 1, dish: 2 }
    const res: any = await getFavorites({
      type: typeMap[activeType.value] || 1,
      page: page.value,
      pageSize: pageSize.value,
    })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      const items = data?.records || data?.list || data || []
      // Enrich with details
      for (const item of items) {
        if (activeType.value === 'stall' && item.stallId) {
          item.targetId = item.stallId
          try {
            const r: any = await getStallById(item.stallId)
            if (r.code === 200 || r.code === 0) {
              const s = r.data || {}
              item.name = s.name
              item.description = s.description
              item.rating = s.avgRating || s.rating
              item.image = s.logo || s.image
            }
          } catch { /* ignore */ }
        } else if (activeType.value === 'dish' && item.dishId) {
          item.targetId = item.dishId
          try {
            const r: any = await getDishById(item.dishId)
            if (r.code === 200 || r.code === 0) {
              const d = r.data || {}
              item.name = d.name
              item.price = d.price
              item.image = d.image
              item.rating = d.rating
            }
          } catch { /* ignore */ }
        }
      }
      favorites.value = items
      total.value = data?.total || favorites.value.length
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleRemove(item: any) {
  try {
    await removeFavorite(item.id)
    favorites.value = favorites.value.filter((f) => f.id !== item.id)
    message.success('已取消收藏')
  } catch {
    message.error('操作失败')
  }
}

onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1E293B;
}

.fav-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 16px;
}

@media (max-width: 1024px) {
  .fav-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 640px) {
  .fav-grid { grid-template-columns: 1fr; }
}

.fav-card {
  background: #FFFFFF;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.fav-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.fav-img {
  height: 160px;
  overflow: hidden;
  background: #F1F5F9;
}

.fav-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.fav-body {
  padding: 14px 16px;
}

.fav-name {
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 4px;
}

.fav-desc {
  font-size: 12px;
  color: #94A3B8;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.fav-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.fav-price {
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
