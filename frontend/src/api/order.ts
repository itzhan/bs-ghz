import request from '@/utils/request'

export function createOrder(data: {
  stallId: number
  items: { dishId: number; quantity: number }[]
  paymentMethod?: string
  remark?: string
}) {
  return request.post('/orders', data)
}

export function getOrders(params?: {
  status?: string
  page?: number
  pageSize?: number
}) {
  return request.get('/orders', { params })
}

export function getOrderById(id: number) {
  return request.get(`/orders/${id}`)
}

export function getOrderItems(orderId: number) {
  return request.get(`/orders/${orderId}/items`)
}

export function cancelOrder(id: number, reason?: string) {
  return request.put(`/orders/${id}/cancel`, { reason })
}

export function payOrder(id: number, data?: { paymentMethod?: string }) {
  return request.put(`/orders/${id}/pay`, data)
}
