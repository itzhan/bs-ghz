import request from '@/utils/request'

export function loginApi(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function registerApi(data: {
  username: string
  password: string
  nickname: string
  phone: string
  role: string
}) {
  return request.post('/auth/register', data)
}

export function getCurrentUserApi() {
  return request.get('/auth/current')
}
