<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-left">
        <n-button text @click="router.push('/merchant/stalls')" style="margin-right: 12px;">← 返回</n-button>
        <h1 class="page-title">{{ stallName }}</h1>
      </div>
    </div>

    <n-tabs type="line" v-model:value="activeTab">
      <!-- ===== 菜品管理 ===== -->
      <n-tab-pane name="dishes" tab="菜品管理">
        <div class="tab-toolbar">
          <n-button type="primary" size="small" @click="openDishModal()">+ 添加菜品</n-button>
        </div>
        <n-spin :show="dishLoading">
          <div v-if="dishes.length" class="dish-list">
            <div v-for="dish in dishes" :key="dish.id" class="dish-item">
              <div class="dish-main">
                <span class="dish-name">{{ dish.name }}</span>
                <n-tag size="tiny" :type="dish.status === 1 ? 'success' : 'default'">{{ dish.status === 1 ? '上架' : '下架' }}</n-tag>
              </div>
              <div class="dish-meta">
                <span class="dish-price">¥{{ dish.price?.toFixed(2) }}</span>
                <span class="dish-stock">库存: {{ dish.stock ?? '∞' }}</span>
              </div>
              <div class="dish-actions">
                <n-button size="tiny" text type="primary" @click="openDishModal(dish)">编辑</n-button>
                <n-button size="tiny" text :type="dish.status === 1 ? 'warning' : 'success'"
                  @click="handleDishStatus(dish.id, dish.status === 1 ? 0 : 1)">
                  {{ dish.status === 1 ? '下架' : '上架' }}
                </n-button>
                <n-popover trigger="click">
                  <template #trigger>
                    <n-button size="tiny" text>改库存</n-button>
                  </template>
                  <div style="display:flex;gap:8px;align-items:center;">
                    <n-input-number v-model:value="stockInput" :min="0" size="small" style="width:100px;" />
                    <n-button size="tiny" type="primary" @click="handleUpdateStock(dish.id)">确定</n-button>
                  </div>
                </n-popover>
              </div>
            </div>
          </div>
          <n-empty v-else-if="!dishLoading" description="暂无菜品" style="padding:40px 0;" />
        </n-spin>
      </n-tab-pane>

      <!-- ===== 订单管理 ===== -->
      <n-tab-pane name="orders" tab="订单管理">
        <div class="tab-toolbar">
          <n-select v-model:value="orderStatus" :options="orderStatusOptions" placeholder="按状态筛选" clearable size="small" style="width:140px;" @update:value="handleStatusFilter" />
          <n-select v-model:value="orderPageSize" :options="pageSizeOptions" size="small" style="width:120px;" @update:value="handlePageSizeChange" />
        </div>
        <n-spin :show="orderLoading">
          <div v-if="orders.length" class="order-list">
            <div v-for="order in orders" :key="order.id" class="order-card">
              <div class="order-card-header">
                <div class="order-card-info">
                  <span class="order-no">{{ order.orderNo || `#${order.id}` }}</span>
                  <n-tag size="tiny" :type="orderTagType(order.status)">{{ orderStatusText(order.status) }}</n-tag>
                  <span class="order-amount">¥{{ order.totalAmount?.toFixed(2) || order.actualAmount?.toFixed(2) || '0.00' }}</span>
                </div>
                <span class="order-time">{{ formatTime(order.createTime || order.createdAt) }}</span>
              </div>
              <!-- 订单项 -->
              <div class="order-card-items" v-if="order.items && order.items.length">
                <span v-for="(item, idx) in order.items" :key="idx" class="order-item-tag">
                  {{ item.dishName || item.name }} ×{{ item.quantity }}
                </span>
              </div>
              <!-- 备注 -->
              <div class="order-card-remark" v-if="order.remark">
                <n-tag size="tiny" type="warning">备注</n-tag>
                <span>{{ order.remark }}</span>
              </div>
              <!-- 操作按钮 -->
              <div class="order-card-actions">
                <n-button size="tiny" text type="primary" @click="openOrderDetail(order)">查看详情</n-button>
                <n-button v-if="order.status === 1" size="tiny" type="info" @click="handleOrderStatus(order.id, 2)">开始制作</n-button>
                <n-button v-if="order.status === 2" size="tiny" type="success" @click="handleOrderStatus(order.id, 3)">出餐完成</n-button>
                <n-button v-if="order.status === 3" size="tiny" type="primary" @click="handleOrderStatus(order.id, 4)">确认完成</n-button>
                <n-button v-if="order.status === 0 || order.status === 1" size="tiny" type="error" @click="handleOrderStatus(order.id, 5)">取消订单</n-button>
              </div>
            </div>
          </div>
          <n-empty v-else-if="!orderLoading" description="暂无订单" style="padding:40px 0;" />
        </n-spin>
        <div class="pagination-wrap" v-if="orderTotal > orderPageSize">
          <n-pagination
            v-model:page="orderPage"
            :page-size="orderPageSize"
            :item-count="orderTotal"
            show-quick-jumper
            @update:page="loadOrders"
          />
        </div>
      </n-tab-pane>

      <!-- ===== 评价管理 ===== -->
      <n-tab-pane name="reviews" tab="评价管理">
        <n-spin :show="reviewLoading">
          <div v-if="reviews.length" class="review-list">
            <div v-for="review in reviews" :key="review.id" class="review-item">
              <div class="review-header">
                <span class="review-user">{{ review.nickname || review.username || '用户' }}</span>
                <n-rate :value="review.rating" readonly size="small" />
                <span class="review-time">{{ review.createTime || review.createdAt }}</span>
              </div>
              <div class="review-content">{{ review.content }}</div>
              <div v-if="review.replyContent" class="review-reply">
                <strong>商家回复：</strong>{{ review.replyContent }}
              </div>
              <div v-else class="review-reply-form">
                <n-input v-model:value="replyInputs[review.id]" size="small" placeholder="回复评价..." style="flex:1;" />
                <n-button size="tiny" type="primary" @click="handleReply(review.id)">回复</n-button>
              </div>
            </div>
          </div>
          <n-empty v-else-if="!reviewLoading" description="暂无评价" style="padding:40px 0;" />
        </n-spin>
      </n-tab-pane>
    </n-tabs>

    <!-- 菜品弹窗 -->
    <n-modal v-model:show="showDishModal" preset="card" :title="editingDish ? '编辑菜品' : '添加菜品'" style="max-width:520px;">
      <n-form ref="dishFormRef" :model="dishForm" :rules="dishRules" label-placement="left" label-width="70px">
        <n-form-item label="名称" path="name">
          <n-input v-model:value="dishForm.name" placeholder="菜品名称" />
        </n-form-item>
        <n-form-item label="价格" path="price">
          <n-input-number v-model:value="dishForm.price" :min="0" :precision="2" placeholder="售价" style="width:100%;">
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>
        <n-form-item label="原价">
          <n-input-number v-model:value="dishForm.originalPrice" :min="0" :precision="2" placeholder="划线价（可选）" style="width:100%;">
            <template #prefix>¥</template>
          </n-input-number>
        </n-form-item>
        <n-form-item label="库存">
          <n-input-number v-model:value="dishForm.stock" :min="0" placeholder="库存数量" style="width:100%;" />
        </n-form-item>
        <n-form-item label="描述">
          <n-input v-model:value="dishForm.description" type="textarea" :rows="2" placeholder="菜品描述" />
        </n-form-item>
      </n-form>
      <template #action>
        <n-space justify="end">
          <n-button @click="showDishModal = false">取消</n-button>
          <n-button type="primary" :loading="dishSubmitting" @click="handleDishSubmit">保存</n-button>
        </n-space>
      </template>
    </n-modal>

    <!-- 订单详情弹窗 -->
    <n-modal v-model:show="showOrderModal" preset="card" title="订单详情" style="max-width:600px;">
      <template v-if="selectedOrder">
        <n-descriptions bordered :column="2" size="small">
          <n-descriptions-item label="订单号">{{ selectedOrder.orderNo || `#${selectedOrder.id}` }}</n-descriptions-item>
          <n-descriptions-item label="状态">
            <n-tag size="small" :type="orderTagType(selectedOrder.status)">{{ orderStatusText(selectedOrder.status) }}</n-tag>
          </n-descriptions-item>
          <n-descriptions-item label="下单时间">{{ formatTime(selectedOrder.createTime || selectedOrder.createdAt) }}</n-descriptions-item>
          <n-descriptions-item label="总金额">
            <span style="font-weight:700;color:#E07A5F;">¥{{ selectedOrder.totalAmount?.toFixed(2) || '0.00' }}</span>
          </n-descriptions-item>
          <n-descriptions-item label="实付金额">¥{{ selectedOrder.actualAmount?.toFixed(2) || selectedOrder.totalAmount?.toFixed(2) || '0.00' }}</n-descriptions-item>
          <n-descriptions-item label="支付方式">{{ paymentMethodText(selectedOrder.paymentMethod) }}</n-descriptions-item>
          <n-descriptions-item label="付款时间" v-if="selectedOrder.paidAt || selectedOrder.payTime">{{ selectedOrder.paidAt || selectedOrder.payTime || '-' }}</n-descriptions-item>
          <n-descriptions-item label="完成时间" v-if="selectedOrder.completedAt">{{ selectedOrder.completedAt }}</n-descriptions-item>
          <n-descriptions-item label="备注" :span="2" v-if="selectedOrder.remark">{{ selectedOrder.remark }}</n-descriptions-item>
        </n-descriptions>

        <div style="margin-top:16px;">
          <h4 style="margin:0 0 8px;">订单项</h4>
          <n-table :bordered="false" :single-line="false" size="small">
            <thead>
              <tr><th>菜品</th><th style="width:60px;">单价</th><th style="width:50px;">数量</th><th style="width:70px;">小计</th></tr>
            </thead>
            <tbody>
              <tr v-for="(item, idx) in (selectedOrder.items || [])" :key="idx">
                <td>{{ item.dishName || item.name }}</td>
                <td>¥{{ item.price?.toFixed(2) }}</td>
                <td>{{ item.quantity }}</td>
                <td>¥{{ item.subtotal?.toFixed(2) || (item.price * item.quantity)?.toFixed(2) }}</td>
              </tr>
              <tr v-if="!selectedOrder.items?.length">
                <td colspan="4" style="text-align:center;color:#94A3B8;">暂无订单项数据</td>
              </tr>
            </tbody>
          </n-table>
        </div>

        <!-- 操作状态 -->
        <div style="margin-top:16px;" v-if="selectedOrder.status < 4">
          <h4 style="margin:0 0 8px;">更新状态</h4>
          <n-space>
            <n-button v-if="selectedOrder.status === 1" type="info" size="small" @click="handleOrderStatusFromDetail(2)">开始制作</n-button>
            <n-button v-if="selectedOrder.status === 2" type="success" size="small" @click="handleOrderStatusFromDetail(3)">出餐完成</n-button>
            <n-button v-if="selectedOrder.status === 3" type="primary" size="small" @click="handleOrderStatusFromDetail(4)">确认完成</n-button>
            <n-button v-if="selectedOrder.status === 0 || selectedOrder.status === 1" type="error" size="small" @click="handleOrderStatusFromDetail(5)">取消订单</n-button>
          </n-space>
        </div>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import type { FormInst, FormRules } from 'naive-ui'
import {
  createDish, updateDish, updateDishStatus, updateStock,
  getStallOrders, updateOrderStatus, replyReview,
} from '@/api/merchant'
import { getStallDishes, getStallReviews, getStallById } from '@/api/stall'

const route = useRoute()
const router = useRouter()
const message = useMessage()
const stallId = Number(route.params.id)
const stallName = ref('档口管理')
const activeTab = ref('dishes')

// ========== 加载档口信息 ==========
async function loadStallInfo() {
  try {
    const res: any = await getStallById(stallId)
    if (res.code === 200 || res.code === 0) {
      stallName.value = res.data?.name || '档口管理'
    }
  } catch { /* ignore */ }
}

// ========== 菜品 ==========
const dishLoading = ref(false)
const dishes = ref<any[]>([])
const stockInput = ref<number | null>(0)

async function loadDishes() {
  dishLoading.value = true
  try {
    const res: any = await getStallDishes(stallId, { page: 1, pageSize: 200 })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      dishes.value = data?.records || data?.list || data || []
    }
  } catch { /* ignore */ } finally {
    dishLoading.value = false
  }
}

async function handleDishStatus(id: number, status: number) {
  try {
    const res: any = await updateDishStatus(id, status)
    if (res.code === 200 || res.code === 0) {
      message.success('状态已更新')
      loadDishes()
    } else { message.error(res.message || '操作失败') }
  } catch { message.error('操作失败') }
}

async function handleUpdateStock(dishId: number) {
  if (stockInput.value == null) return
  try {
    const res: any = await updateStock(dishId, stockInput.value)
    if (res.code === 200 || res.code === 0) {
      message.success('库存已更新')
      loadDishes()
    } else { message.error(res.message || '操作失败') }
  } catch { message.error('操作失败') }
}

// 菜品弹窗
const showDishModal = ref(false)
const dishSubmitting = ref(false)
const editingDish = ref<any>(null)
const dishFormRef = ref<FormInst | null>(null)
const dishForm = reactive({
  name: '',
  price: null as number | null,
  originalPrice: null as number | null,
  stock: null as number | null,
  description: '',
})
const dishRules: FormRules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [{ required: true, type: 'number', message: '请输入价格', trigger: 'blur' }],
}

function openDishModal(dish?: any) {
  editingDish.value = dish || null
  if (dish) {
    Object.assign(dishForm, {
      name: dish.name,
      price: dish.price,
      originalPrice: dish.originalPrice,
      stock: dish.stock,
      description: dish.description || '',
    })
  } else {
    Object.assign(dishForm, { name: '', price: null, originalPrice: null, stock: null, description: '' })
  }
  showDishModal.value = true
}

async function handleDishSubmit() {
  try { await dishFormRef.value?.validate() } catch { return }
  dishSubmitting.value = true
  try {
    const payload: any = { stallId, name: dishForm.name, price: dishForm.price, description: dishForm.description }
    if (dishForm.originalPrice != null) payload.originalPrice = dishForm.originalPrice
    if (dishForm.stock != null) payload.stock = dishForm.stock
    let res: any
    if (editingDish.value) {
      res = await updateDish(editingDish.value.id, payload)
    } else {
      res = await createDish(payload)
    }
    if (res.code === 200 || res.code === 0) {
      message.success(editingDish.value ? '编辑成功' : '添加成功')
      showDishModal.value = false
      loadDishes()
    } else { message.error(res.message || '操作失败') }
  } catch { message.error('操作失败') } finally {
    dishSubmitting.value = false
  }
}

// ========== 订单 ==========
const orderLoading = ref(false)
const orders = ref<any[]>([])
const orderPage = ref(1)
const orderPageSize = ref(10)
const orderTotal = ref(0)
const orderStatus = ref<number | null>(null)

const orderStatusOptions = [
  { label: '待付款', value: 0 },
  { label: '已付款', value: 1 },
  { label: '制作中', value: 2 },
  { label: '待取餐', value: 3 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: 5 },
  { label: '已退款', value: 6 },
]

const pageSizeOptions = [
  { label: '每页 5 条', value: 5 },
  { label: '每页 10 条', value: 10 },
  { label: '每页 20 条', value: 20 },
  { label: '每页 50 条', value: 50 },
]

function orderStatusText(s: number) {
  return ({ 0: '待付款', 1: '已付款', 2: '制作中', 3: '待取餐', 4: '已完成', 5: '已取消', 6: '已退款' } as Record<number, string>)[s] || String(s)
}

function orderTagType(s: number): 'default' | 'info' | 'success' | 'warning' | 'error' {
  return ({ 0: 'warning', 1: 'info', 2: 'info', 3: 'success', 4: 'success', 5: 'error', 6: 'error' } as Record<number, any>)[s] || 'default'
}

function paymentMethodText(m: number | null | undefined) {
  if (!m) return '-'
  return ({ 1: '余额支付', 2: '微信支付', 3: '支付宝' } as Record<number, string>)[m] || String(m)
}

function formatTime(t: string | undefined) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

function handleStatusFilter() {
  orderPage.value = 1
  loadOrders()
}

function handlePageSizeChange() {
  orderPage.value = 1
  loadOrders()
}

async function loadOrders() {
  orderLoading.value = true
  try {
    const params: any = { stallId, page: orderPage.value, size: orderPageSize.value }
    if (orderStatus.value != null) params.status = orderStatus.value
    const res: any = await getStallOrders(params)
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      orders.value = data?.records || data?.list || data || []
      orderTotal.value = data?.total || orders.value.length
    }
  } catch { /* ignore */ } finally {
    orderLoading.value = false
  }
}

async function handleOrderStatus(id: number, status: number) {
  try {
    const res: any = await updateOrderStatus(id, status)
    if (res.code === 200 || res.code === 0) {
      message.success('订单状态已更新')
      loadOrders()
    } else { message.error(res.message || '操作失败') }
  } catch { message.error('操作失败') }
}

// 订单详情弹窗
const showOrderModal = ref(false)
const selectedOrder = ref<any>(null)

function openOrderDetail(order: any) {
  selectedOrder.value = { ...order }
  showOrderModal.value = true
}

async function handleOrderStatusFromDetail(status: number) {
  if (!selectedOrder.value) return
  try {
    const res: any = await updateOrderStatus(selectedOrder.value.id, status)
    if (res.code === 200 || res.code === 0) {
      message.success('订单状态已更新')
      selectedOrder.value.status = status
      showOrderModal.value = false
      loadOrders()
    } else { message.error(res.message || '操作失败') }
  } catch { message.error('操作失败') }
}

// ========== 评价 ==========
const reviewLoading = ref(false)
const reviews = ref<any[]>([])
const replyInputs = ref<Record<number, string>>({})

async function loadReviews() {
  reviewLoading.value = true
  try {
    const res: any = await getStallReviews(stallId, { page: 1, pageSize: 100 })
    if (res.code === 200 || res.code === 0) {
      const data = res.data
      reviews.value = data?.records || data?.list || data || []
    }
  } catch { /* ignore */ } finally {
    reviewLoading.value = false
  }
}

async function handleReply(reviewId: number) {
  const content = replyInputs.value[reviewId]?.trim()
  if (!content) { message.warning('请输入回复内容'); return }
  try {
    const res: any = await replyReview(reviewId, content)
    if (res.code === 200 || res.code === 0) {
      message.success('回复成功')
      loadReviews()
    } else { message.error(res.message || '回复失败') }
  } catch { message.error('回复失败') }
}

onMounted(() => {
  loadStallInfo()
  loadDishes()
  loadOrders()
  loadReviews()
})
</script>

<style scoped>
.page-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 32px 24px 60px;
}
.page-header {
  margin-bottom: 20px;
}
.header-left {
  display: flex;
  align-items: center;
}
.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1E293B;
  margin: 0;
}
.tab-toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

/* 菜品列表 */
.dish-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.dish-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-radius: 10px;
  padding: 14px 18px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.dish-main {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 180px;
}
.dish-name {
  font-weight: 600;
  color: #1E293B;
}
.dish-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #64748B;
}
.dish-price {
  font-weight: 600;
  color: #E07A5F;
}
.dish-actions {
  display: flex;
  gap: 8px;
}

/* 订单列表 - 卡片样式 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.order-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px 18px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  transition: box-shadow 0.2s;
}
.order-card:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.order-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}
.order-card-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.order-no {
  font-size: 13px;
  font-weight: 600;
  color: #334155;
}
.order-time {
  font-size: 12px;
  color: #94A3B8;
}
.order-amount {
  font-weight: 700;
  color: #E07A5F;
}
.order-card-items {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}
.order-item-tag {
  display: inline-block;
  font-size: 12px;
  background: #F1F5F9;
  color: #475569;
  padding: 2px 8px;
  border-radius: 4px;
}
.order-card-remark {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #64748B;
  margin-bottom: 8px;
}
.order-card-actions {
  display: flex;
  gap: 8px;
  padding-top: 8px;
  border-top: 1px solid #F1F5F9;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 评价列表 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.review-item {
  background: #fff;
  border-radius: 10px;
  padding: 16px 18px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.04);
}
.review-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.review-user {
  font-weight: 600;
  color: #1E293B;
}
.review-time {
  font-size: 12px;
  color: #94A3B8;
  margin-left: auto;
}
.review-content {
  font-size: 14px;
  color: #334155;
  margin-bottom: 8px;
}
.review-reply {
  font-size: 13px;
  color: #64748B;
  background: #F8FAFC;
  padding: 10px 12px;
  border-radius: 6px;
}
.review-reply-form {
  display: flex;
  gap: 8px;
  align-items: center;
}
</style>
