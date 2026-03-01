import request from '@/utils/request'

export function createReview(data: {
  orderId: number
  stallId: number
  content: string
  rating: number
  images?: string
}) {
  return request.post('/reviews', data)
}

export function getMyReviews(params?: {
  page?: number
  pageSize?: number
}) {
  return request.get('/reviews/mine', { params })
}
