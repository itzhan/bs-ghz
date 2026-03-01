import request from '@/utils/request'

export function getDashboardStats() {
  return request.get('/stats/dashboard')
}

export function getDishRanking(params?: { limit?: number }) {
  return request.get('/stats/dishes/ranking', { params })
}
