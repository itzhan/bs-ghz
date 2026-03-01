import request from '@/utils/request'

export function updateProfile(data: {
  nickname?: string
  phone?: string
  email?: string
  avatar?: string
}) {
  return request.put('/users/profile', data)
}

export function changePassword(data: {
  oldPassword: string
  newPassword: string
}) {
  return request.put('/users/password', data)
}

export function recharge(amount: number) {
  return request.post('/users/recharge', { amount })
}
