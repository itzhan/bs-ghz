import request from '@/utils/request'

export function createComplaint(data: {
  stallId?: number
  orderId?: number
  type: string
  content: string
  images?: string
}) {
  return request.post('/complaints', data)
}

export function getMyComplaints(params?: {
  page?: number
  pageSize?: number
}) {
  return request.get('/complaints/mine', { params })
}
