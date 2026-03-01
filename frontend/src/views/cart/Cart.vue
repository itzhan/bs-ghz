<template>
  <div class="page-container">
    <div class="page-header">
      <h1 class="page-title">购物车</h1>
    </div>

    <n-spin :show="loading">
      <template v-if="groupedItems.length">
        <!-- Grouped by Stall -->
        <div v-for="group in groupedItems" :key="group.stallId" class="cart-group">
          <div class="group-header">
            <n-icon :component="StorefrontOutline" size="18" color="#2D6A4F" />
            <span class="group-stall-name" @click="router.push(`/stalls/${group.stallId}`)">
              {{ group.stallName || '档口' }}
            </span>
          </div>
          <div class="cart-items">
            <div v-for="item in group.items" :key="item.id" class="cart-item">
              <div class="item-img">
                <img
                  :src="item.dishImage || PLACEHOLDER_SMALL"
                  :alt="item.dishName"
                />
              </div>
              <div class="item-info">
                <h4 class="item-name">{{ item.dishName }}</h4>
                <span class="item-price">¥{{ item.price?.toFixed(2) }}</span>
              </div>
              <div class="item-quantity">
                <n-input-number
                  :value="item.quantity"
                  :min="1"
                  :max="99"
                  size="small"
                  style="width: 110px;"
                  @update:value="(val: number | null) => handleQuantityChange(item, val || 1)"
                />
              </div>
              <div class="item-subtotal">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
              <n-button text type="error" @click="handleRemove(item)">
                <n-icon :component="TrashOutline" size="18" />
              </n-button>
            </div>
          </div>
        </div>

        <!-- Bottom Bar -->
        <div class="cart-bottom">
          <div class="bottom-left">
            <n-button text type="error" @click="handleClearCart">清空购物车</n-button>
          </div>
          <div class="bottom-right">
            <span class="total-label">合计：</span>
            <span class="total-price">¥{{ totalAmount.toFixed(2) }}</span>
            <n-button
              type="warning"
              size="large"
              @click="showCheckout = true"
              :disabled="!groupedItems.length"
              style="margin-left: 16px; min-width: 120px;"
            >
              去结算
            </n-button>
          </div>
        </div>
      </template>
      <n-empty v-else-if="!loading" description="购物车是空的" style="padding: 80px 0;">
        <template #extra>
          <n-button type="primary" @click="router.push('/dishes')">去逛逛</n-button>
        </template>
      </n-empty>
    </n-spin>

    <!-- Checkout Modal -->
    <n-modal v-model:show="showCheckout" preset="card" title="确认下单" style="max-width: 500px;">
      <div class="checkout-body">
        <div class="checkout-summary">
          <p>共 <strong>{{ cartItems.length }}</strong> 件商品</p>
          <p class="checkout-total">
            应付金额：<span class="accent">¥{{ totalAmount.toFixed(2) }}</span>
          </p>
        </div>
        <n-divider />
        <n-form-item label="支付方式">
          <n-radio-group v-model:value="paymentMethod">
            <n-radio :value="1">余额支付</n-radio>
            <n-radio :value="2">微信支付</n-radio>
            <n-radio :value="3">支付宝</n-radio>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="备注">
          <n-input v-model:value="remark" placeholder="如有特殊要求请备注..." type="textarea" :rows="2" />
        </n-form-item>
      </div>
      <template #action>
        <n-space justify="end">
          <n-button @click="showCheckout = false">取消</n-button>
          <n-button type="warning" :loading="checkoutLoading" @click="handleCheckout">
            确认支付 ¥{{ totalAmount.toFixed(2) }}
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { StorefrontOutline, TrashOutline } from '@vicons/ionicons5'
import { getCartItems, updateCartQuantity, removeFromCart, clearCart } from '@/api/cart'
import { getDishById } from '@/api/dish'
import { getStallById } from '@/api/stall'
import { createOrder } from '@/api/order'
import { PLACEHOLDER_SMALL } from '@/utils/placeholder'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const cartItems = ref<any[]>([])
const showCheckout = ref(false)
const checkoutLoading = ref(false)
const paymentMethod = ref(1)
const remark = ref('')

const groupedItems = computed(() => {
  const groups: Record<number, { stallId: number; stallName: string; items: any[] }> = {}
  for (const item of cartItems.value) {
    const sid = item.stallId || 0
    if (!groups[sid]) {
      groups[sid] = { stallId: sid, stallName: item.stallName || '档口', items: [] }
    }
    groups[sid].items.push(item)
  }
  return Object.values(groups)
})

const totalAmount = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price || 0) * item.quantity, 0)
})

async function loadCart() {
  loading.value = true
  try {
    const res: any = await getCartItems()
    if (res.code === 200 || res.code === 0) {
      const items = res.data || []
      // Enrich with dish and stall info
      const dishCache: Record<number, any> = {}
      const stallCache: Record<number, any> = {}
      for (const item of items) {
        // Fetch dish info
        if (item.dishId && !dishCache[item.dishId]) {
          try {
            const dishRes: any = await getDishById(item.dishId)
            if (dishRes.code === 200 || dishRes.code === 0) {
              dishCache[item.dishId] = dishRes.data || {}
            }
          } catch { /* ignore */ }
        }
        // Fetch stall info
        if (item.stallId && !stallCache[item.stallId]) {
          try {
            const stallRes: any = await getStallById(item.stallId)
            if (stallRes.code === 200 || stallRes.code === 0) {
              stallCache[item.stallId] = stallRes.data || {}
            }
          } catch { /* ignore */ }
        }
        const dish = dishCache[item.dishId] || {}
        const stall = stallCache[item.stallId] || {}
        item.dishName = dish.name || `菜品#${item.dishId}`
        item.dishImage = dish.image || null
        item.price = dish.price || 0
        item.stallName = stall.name || `档口#${item.stallId}`
      }
      cartItems.value = items
    }
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleQuantityChange(item: any, val: number) {
  try {
    await updateCartQuantity(item.id, val)
    item.quantity = val
  } catch {
    message.error('更新失败')
  }
}

async function handleRemove(item: any) {
  try {
    await removeFromCart(item.id)
    cartItems.value = cartItems.value.filter((i) => i.id !== item.id)
    message.success('已移除')
  } catch {
    message.error('删除失败')
  }
}

async function handleClearCart() {
  try {
    // Clear each stall group separately (backend requires stallId)
    const stallIds = [...new Set(cartItems.value.map(i => i.stallId).filter(Boolean))]
    for (const sid of stallIds) {
      await clearCart(sid)
    }
    cartItems.value = []
    message.success('购物车已清空')
  } catch {
    message.error('操作失败')
  }
}

async function handleCheckout() {
  checkoutLoading.value = true
  try {
    // Create orders grouped by stall
    for (const group of groupedItems.value) {
      const orderData = {
        stallId: group.stallId,
        items: group.items.map((i) => ({ dishId: i.dishId, quantity: i.quantity })),
        paymentMethod: paymentMethod.value,
        remark: remark.value,
      }
      const res: any = await createOrder(orderData)
      if (res.code !== 200 && res.code !== 0) {
        message.error(res.message || '下单失败')
        checkoutLoading.value = false
        return
      }
    }
    message.success('下单成功！')
    showCheckout.value = false
    cartItems.value = []
    router.push('/orders')
  } catch (e: any) {
    message.error(e?.response?.data?.message || '下单失败')
  } finally {
    checkoutLoading.value = false
  }
}

onMounted(() => {
  loadCart()
})
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 24px 100px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1E293B;
}

.cart-group {
  background: #FFFFFF;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.group-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 14px 20px;
  border-bottom: 1px solid #F1F5F9;
}

.group-stall-name {
  font-weight: 600;
  color: #2D6A4F;
  cursor: pointer;
}

.group-stall-name:hover {
  text-decoration: underline;
}

.cart-items {
  padding: 8px 0;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 20px;
}

.cart-item:hover {
  background: #FAFAF8;
}

.item-img {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #F1F5F9;
}

.item-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  min-width: 0;
}

.item-name {
  font-size: 14px;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 4px;
}

.item-price {
  font-size: 13px;
  color: #94A3B8;
}

.item-subtotal {
  font-size: 16px;
  font-weight: 700;
  color: #E07A5F;
  min-width: 80px;
  text-align: right;
}

/* Bottom Bar */
.cart-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #FFFFFF;
  border-radius: 12px;
  padding: 16px 24px;
  margin-top: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}

.bottom-right {
  display: flex;
  align-items: center;
}

.total-label {
  font-size: 14px;
  color: #64748B;
}

.total-price {
  font-size: 24px;
  font-weight: 800;
  color: #E07A5F;
}

/* Checkout */
.checkout-total {
  margin-top: 8px;
}

.accent {
  font-size: 20px;
  font-weight: 700;
  color: #E07A5F;
}
</style>
