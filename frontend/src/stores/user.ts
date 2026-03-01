import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginApi, registerApi, getCurrentUserApi } from '@/api/auth'

interface UserInfo {
  id: number
  username: string
  nickname: string
  phone: string
  email: string
  avatar: string
  role: string
  balance: number
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  // Restore userInfo from localStorage
  const savedInfo = localStorage.getItem('userInfo')
  if (savedInfo) {
    try {
      userInfo.value = JSON.parse(savedInfo)
    } catch {
      localStorage.removeItem('userInfo')
    }
  }

  const isLoggedIn = () => !!token.value

  async function login(username: string, password: string) {
    const res: any = await loginApi({ username, password })
    if (res.code === 200 || res.code === 0) {
      token.value = res.data?.token || res.data
      localStorage.setItem('token', token.value)
      await fetchProfile()
      return res
    }
    throw new Error(res.message || '登录失败')
  }

  async function register(data: {
    username: string
    password: string
    nickname: string
    phone: string
    role: string
  }) {
    const res: any = await registerApi(data)
    return res
  }

  async function fetchProfile() {
    try {
      const res: any = await getCurrentUserApi()
      if (res.code === 200 || res.code === 0) {
        userInfo.value = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
      }
    } catch {
      // ignore
    }
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    fetchProfile,
    logout,
  }
})
