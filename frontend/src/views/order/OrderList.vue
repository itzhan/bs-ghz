<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">我的订单</h1>
    </div>

    <n-tabs type="line" v-model:value="statusTab" @update:value="loadOrders">
      <n-tab-pane v-for="tab in statusTabs" :key="tab.value" :name="tab.value" :tab="tab.label">
      </n-tab-pane>
    </n-tabs>

    <n-spin :show="loading" style="margin-top: 16px;">
      <div v-if="orders.length" class="order-list">
        <div v-for="order in orders" :key="order.id" class="order-card">
          <div class="order-header">
            <div class="order-header-left">
              <span class="order-no">订单号：{{ order.orderNo || order.id }}</span>
              <span class="order-time">{{ order.createdAt || order.createTime }}</span>
            </div>
            <n-tag :type="getStatusType(order.status)" size="small">
              {{ getStatusText(order.status) }}
            </n-tag>
          </div>
          <div class="order-body" @click="router.push(`/orders/${order.id}`)">
            <div class="order-stall">
              <n-icon :component="StorefrontOutline" size="16" />
              {{ order.stallName || '档口' }}
            </div>
            <div class="order-info-row">
              <span>共 {{ order.itemCount || '—' }} 件商品</span>
              <span class="order-amount">¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>
          <div class="order-footer">
            <n-space>
              <n-button
                v-if="order.status === 'pending' || order.status === 0"
                size="small"
                type="error"
                text
                @click="handleCancel(order)"
              >
                取消订单
              </n-button>
              <n-button
                v-if="order.status === 'completed' || order.status === 5"
                size="small"
                type="primary"
                @click="openReviewModal(order)"
              >
                去评价
              </n-button>
              <n-button
                size="small"
                @click="router.push(`/orders/${order.id}`)"
              >
                查看详情
              </n-button>
            </n-space>
          </div>
        </div>
      </div>
      <n-empty v-else-if="!loading" description="暂无订单" style="padding: 60px 0;" />
    </n-spin>

    <div class="pagination-wrap" v-if="total > pageSize">
      <n-pagination
        v-model:page="page"
        :page-size="pageSize"
        :item-count="total"
        @update:page="loadOrders"
      />
    </div>

    <!-- Review Modal -->
    <n-modal v-model:show="showReview" preset="card" title="评价订单" style="max-width: 500px;">
      <n-form>
        <n-form-item label="评分">
          <n-rate v-model:value="reviewForm.rating" :count="5" />
        </n-form-item>
        <n-form-item label="评价内容">
          <n-input
            v-model:value="reviewForm.content"
            type="textarea"
            :rows="3"
            placeholder="写下你的用餐体验..."
          />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="showReview = false">取消</n-button>
          <n-button type="primary" :loading="reviewLoading" @click="handleSubmitReview">
            提交评价
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { StorefrontOutline } from '@vicons/ionicons5'
import { getOrders, cancelOrder } from '@/api/order'
import { createReview } from '@/api/review'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const orders = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const statusTab = ref('')

const statusTabs = [
  { label: '全部', value: '' },
  { label: '待付款', value: '0' },
  { label: '已付款', value: '1' },
  { label: '制作中', value: '2' },
  { label: '待取餐', value: '3' },
  { label: '已完成', value: '5' },
  { label: '已取消', value: '6' },
]

const showReview = ref(false)
const reviewLoading = ref(false)
const currentOrder = ref<any>(null)
const reviewForm = reactive({
  rating: 5,
  content: '',
})

function getStatusType(status: string | number): 'default' | 'info' | 'success' | 'warning' | 'error' {
  const map: Record<string, 'default' | 'info' | 'success' | 'warning' | 'error'> = {
    pending: 'warning',
    '0': 'warning',
    paid: 'info',
    '1': 'info',
    preparing: 'info',
    '2': 'info',
    ready: 'success',
    '3': 'success',
    delivering: 'info',
    '4': 'info',
    completed: 'success',
    '5': 'success',
    cancelled: 'error',
    '6': 'error',
  }
  return map[String(status)] || 'default'
}

function getStatusText(status: string | number): string {
  const map: Record<string, string> = {
    pending: '待付款',
    '0': '待付款',
    paid: '已付款',
    '1': '已付款',
    preparing: '制作中',
    '2': '制作中',
    ready: '待取餐',
    '3': '待取餐',
    delivering: '配送中',
    '4': '配送中',
    completed: '已完成',
    '5': '已完成',
    cancelled: '已取消',
    '6': '已取消',
  }
  return map[String(status)] || String(status)
}

async function loadOrders() {
  loading.value = true
  try {
    const res: any = await getOrders({
      status: statusTab.value || undefined,
      page: page.value,
      size: pageSize.value,
    })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      orders.value = data?.records || data?.list || data || []
      total.value = data?.total || orders.value.length
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleCancel(order: any) {
  try {
    const res: any = await cancelOrder(order.id)
    if (res.code === 200 || res.code === 0) {
      message.success('订单已取消')
      loadOrders()
    } else {
      message.error(res.message || '取消失败')
    }
  } catch {
    message.error('取消失败')
  }
}



function openReviewModal(order: any) {
  currentOrder.value = order
  reviewForm.rating = 5
  reviewForm.content = ''
  showReview.value = true
}

async function handleSubmitReview() {
  if (!reviewForm.content.trim()) {
    message.warning('请填写评价内容')
    return
  }
  reviewLoading.value = true
  try {
    const res: any = await createReview({
      orderId: currentOrder.value.id,
      stallId: currentOrder.value.stallId,
      rating: reviewForm.rating,
      content: reviewForm.content,
    })
    if (res.code === 200 || res.code === 0) {
      message.success('评价成功')
      showReview.value = false
      loadOrders()
    } else {
      message.error(res.message || '评价失败')
    }
  } catch {
    message.error('评价失败')
  } finally {
    reviewLoading.value = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.page-container {
  max-width: 900px;
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

.order-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.order-card {
  background: #FFFFFF;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 20px;
  border-bottom: 1px solid #F1F5F9;
}

.order-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.order-no {
  font-size: 13px;
  color: #64748B;
}

.order-time {
  font-size: 12px;
  color: #94A3B8;
}

.order-body {
  padding: 16px 20px;
  cursor: pointer;
}

.order-body:hover {
  background: #FAFAF8;
}

.order-stall {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 8px;
}

.order-info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #64748B;
}

.order-amount {
  font-size: 18px;
  font-weight: 700;
  color: #E07A5F;
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  padding: 12px 20px;
  border-top: 1px solid #F1F5F9;
}

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
