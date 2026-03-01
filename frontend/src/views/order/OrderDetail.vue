<template>
  <div class="page-container">
    <n-breadcrumb style="margin-bottom: 20px;">
      <n-breadcrumb-item @click="router.push('/')">首页</n-breadcrumb-item>
      <n-breadcrumb-item @click="router.push('/orders')">我的订单</n-breadcrumb-item>
      <n-breadcrumb-item>订单详情</n-breadcrumb-item>
    </n-breadcrumb>

    <n-spin :show="loading">
      <template v-if="order.id">
        <!-- Order Status -->
        <n-card style="margin-bottom: 16px;">
          <div class="status-section">
            <div class="status-main">
              <n-tag :type="getStatusType(order.status)" size="large">
                {{ getStatusText(order.status) }}
              </n-tag>
              <span class="order-no">订单号：{{ order.orderNo || order.id }}</span>
            </div>
            <div class="status-actions" v-if="order.status === 'pending' || order.status === 0">
              <n-button type="error" text @click="handleCancel">取消订单</n-button>
              <n-button type="warning" @click="handlePay">立即支付</n-button>
            </div>
          </div>
        </n-card>

        <!-- Status Timeline -->
        <n-card title="订单进度" style="margin-bottom: 16px;">
          <n-timeline>
            <n-timeline-item
              v-for="(step, index) in timeline"
              :key="index"
              :type="step.active ? 'success' : 'default'"
              :title="step.title"
              :content="step.time || ''"
            />
          </n-timeline>
        </n-card>

        <!-- Order Items -->
        <n-card title="订单商品" style="margin-bottom: 16px;">
          <div class="order-stall">
            <n-icon :component="StorefrontOutline" size="16" />
            {{ order.stallName || '档口' }}
          </div>
          <div class="item-list">
            <div v-for="item in orderItems" :key="item.id" class="order-item">
              <div class="item-img">
                <img
                  :src="item.dishImage || PLACEHOLDER_SMALL"
                  :alt="item.dishName"
                />
              </div>
              <div class="item-info">
                <span class="item-name">{{ item.dishName }}</span>
                <span class="item-qty">x{{ item.quantity }}</span>
              </div>
              <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
          <n-divider />
          <div class="order-summary">
            <div class="summary-row">
              <span>商品总额</span>
              <span>¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="summary-row total">
              <span>实付金额</span>
              <span class="total-price">¥{{ order.totalAmount?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>
        </n-card>

        <!-- Order Info -->
        <n-card title="订单信息">
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单编号</span>
              <span>{{ order.orderNo || order.id }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">下单时间</span>
              <span>{{ order.createTime || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">支付方式</span>
              <span>{{ order.paymentMethod || '—' }}</span>
            </div>
            <div class="info-item" v-if="order.remark">
              <span class="info-label">备注</span>
              <span>{{ order.remark }}</span>
            </div>
          </div>
        </n-card>
      </template>
    </n-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useMessage } from 'naive-ui'
import { StorefrontOutline } from '@vicons/ionicons5'
import { getOrderById, getOrderItems, cancelOrder, payOrder } from '@/api/order'
import { PLACEHOLDER_SMALL } from '@/utils/placeholder'

const router = useRouter()
const route = useRoute()
const message = useMessage()

const orderId = Number(route.params.id)
const loading = ref(false)
const order = ref<any>({})
const orderItems = ref<any[]>([])

function getStatusType(status: string | number): 'default' | 'info' | 'success' | 'warning' | 'error' {
  const map: Record<string, 'default' | 'info' | 'success' | 'warning' | 'error'> = {
    pending: 'warning', '0': 'warning',
    paid: 'info', '1': 'info',
    preparing: 'info', '2': 'info',
    ready: 'success', '3': 'success',
    completed: 'success', '5': 'success',
    cancelled: 'error', '6': 'error',
  }
  return map[String(status)] || 'default'
}

function getStatusText(status: string | number): string {
  const map: Record<string, string> = {
    pending: '待付款', '0': '待付款',
    paid: '已付款', '1': '已付款',
    preparing: '制作中', '2': '制作中',
    ready: '待取餐', '3': '待取餐',
    completed: '已完成', '5': '已完成',
    cancelled: '已取消', '6': '已取消',
  }
  return map[String(status)] || String(status)
}

const timeline = computed(() => {
  const steps = [
    { title: '下单', time: order.value.createTime, active: true },
    { title: '付款', time: order.value.payTime, active: ['paid', 'preparing', 'ready', 'completed', 1, 2, 3, 4, 5].includes(order.value.status) },
    { title: '制作中', time: order.value.preparingTime, active: ['preparing', 'ready', 'completed', 2, 3, 4, 5].includes(order.value.status) },
    { title: '待取餐', time: order.value.readyTime, active: ['ready', 'completed', 3, 4, 5].includes(order.value.status) },
    { title: '已完成', time: order.value.completeTime, active: ['completed', 5].includes(order.value.status) },
  ]
  return steps
})

async function loadOrder() {
  loading.value = true
  try {
    const res: any = await getOrderById(orderId)
    if (res.code === 200 || res.code === 0) {
      order.value = res.data || {}
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function loadItems() {
  try {
    const res: any = await getOrderItems(orderId)
    if (res.code === 200 || res.code === 0) {
      orderItems.value = res.data || []
    }
  } catch {
    // ignore
  }
}

async function handleCancel() {
  try {
    const res: any = await cancelOrder(orderId)
    if (res.code === 200 || res.code === 0) {
      message.success('订单已取消')
      loadOrder()
    }
  } catch {
    message.error('取消失败')
  }
}

async function handlePay() {
  try {
    const res: any = await payOrder(orderId)
    if (res.code === 200 || res.code === 0) {
      message.success('支付成功')
      loadOrder()
    }
  } catch {
    message.error('支付失败')
  }
}

onMounted(() => {
  loadOrder()
  loadItems()
})
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px 24px 60px;
}

.status-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.order-no {
  font-size: 13px;
  color: #94A3B8;
}

.status-actions {
  display: flex;
  gap: 12px;
}

.order-stall {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 12px;
}

.item-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-img {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  overflow: hidden;
  background: #F1F5F9;
  flex-shrink: 0;
}

.item-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
}

.item-name {
  font-size: 14px;
  color: #1E293B;
}

.item-qty {
  font-size: 13px;
  color: #94A3B8;
}

.item-price {
  font-size: 14px;
  font-weight: 600;
  color: #1E293B;
  min-width: 70px;
  text-align: right;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: flex-end;
}

.summary-row {
  display: flex;
  gap: 24px;
  font-size: 14px;
  color: #64748B;
}

.summary-row.total {
  font-size: 16px;
  font-weight: 600;
  color: #1E293B;
}

.total-price {
  font-size: 20px;
  font-weight: 800;
  color: #E07A5F;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
  color: #475569;
}

.info-label {
  font-size: 12px;
  color: #94A3B8;
}
</style>
