import request from '@/utils/request'

// ==================== 档口管理 ====================

export function getMyStalls() {
  return request.get('/merchant/stalls')
}

export function createStall(data: {
  name: string
  description?: string
  logo?: string
  location?: string
  phone?: string
  businessHours?: string
}) {
  return request.post('/merchant/stalls', data)
}

export function updateStall(id: number, data: {
  name: string
  description?: string
  logo?: string
  location?: string
  phone?: string
  businessHours?: string
}) {
  return request.put(`/merchant/stalls/${id}`, data)
}

export function updateStallStatus(id: number, status: number) {
  return request.put(`/merchant/stalls/${id}/status`, { status })
}

// ==================== 菜品管理 ====================

export function createDish(data: {
  stallId: number
  categoryId?: number
  name: string
  description?: string
  image?: string
  price: number
  originalPrice?: number
  stock?: number
  dailyLimit?: number
}) {
  return request.post('/merchant/dishes', data)
}

export function updateDish(id: number, data: {
  stallId: number
  categoryId?: number
  name: string
  description?: string
  image?: string
  price: number
  originalPrice?: number
  stock?: number
  dailyLimit?: number
}) {
  return request.put(`/merchant/dishes/${id}`, data)
}

export function updateDishStatus(id: number, status: number) {
  return request.put(`/merchant/dishes/${id}/status`, { status })
}

export function updateStock(id: number, stock: number) {
  return request.put(`/merchant/dishes/${id}/stock`, { stock })
}

// ==================== 分类管理 ====================

export function createCategory(data: { stallId: number; name: string; sort?: number }) {
  return request.post('/merchant/categories', data)
}

export function updateCategory(id: number, data: { name: string; sort?: number }) {
  return request.put(`/merchant/categories/${id}`, data)
}

export function deleteCategory(id: number) {
  return request.delete(`/merchant/categories/${id}`)
}

// ==================== 订单管理 ====================

export function getStallOrders(params: {
  stallId: number
  page?: number
  size?: number
  status?: number
}) {
  return request.get('/merchant/orders', { params })
}

export function updateOrderStatus(id: number, status: number) {
  return request.put(`/merchant/orders/${id}/status`, { status })
}

// ==================== 评价管理 ====================

export function replyReview(id: number, replyContent: string) {
  return request.put(`/merchant/reviews/${id}/reply`, { replyContent })
}

// ==================== 统计 ====================

export function getStallStats(stallId: number) {
  return request.get('/merchant/stats', { params: { stallId } })
}
