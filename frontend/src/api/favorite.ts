import request from '@/utils/request'

export function addFavorite(data: { stallId?: number; dishId?: number; type: number }) {
  return request.post('/favorites', data)
}

export function removeFavorite(id: number) {
  return request.delete(`/favorites/${id}`)
}

export function getFavorites(params?: {
  type?: number
  page?: number
  pageSize?: number
}) {
  return request.get('/favorites', { params })
}

export function checkFavorite(targetId: number, type: number) {
  return request.get('/favorites/check', { params: { targetId, type } })
}
