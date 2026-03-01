import request from '@/utils/request'

export function getDishes(params?: {
  keyword?: string
  categoryId?: number
  stallId?: number
  minPrice?: number
  maxPrice?: number
  page?: number
  size?: number
}) {
  return request.get('/dishes', { params })
}

export function getDishById(id: number) {
  return request.get(`/dishes/${id}`)
}
