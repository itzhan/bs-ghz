import request from '@/utils/request'

export function getStalls(params?: {
  keyword?: string
  status?: number
  page?: number
  size?: number
}) {
  return request.get('/stalls', { params })
}

export function getStallById(id: number) {
  return request.get(`/stalls/${id}`)
}

export function getStallDishes(stallId: number, params?: {
  categoryId?: number
  keyword?: string
  page?: number
  pageSize?: number
}) {
  return request.get(`/stalls/${stallId}/dishes`, { params })
}

export function getStallCategories(stallId: number) {
  return request.get(`/stalls/${stallId}/categories`)
}

export function getStallReviews(stallId: number, params?: {
  page?: number
  pageSize?: number
}) {
  return request.get(`/stalls/${stallId}/reviews`, { params })
}

export function getStallActivities(stallId: number) {
  return request.get(`/stalls/${stallId}/activities`)
}
