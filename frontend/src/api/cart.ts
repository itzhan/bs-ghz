import request from '@/utils/request'

export function getCartItems() {
  return request.get('/cart')
}

export function addToCart(data: { dishId: number; quantity: number }) {
  return request.post('/cart', data)
}

export function updateCartQuantity(id: number, quantity: number) {
  return request.put(`/cart/${id}`, { quantity })
}

export function removeFromCart(id: number) {
  return request.delete(`/cart/${id}`)
}

export function clearCart(stallId: number) {
  return request.delete('/cart/clear', { params: { stallId } })
}
