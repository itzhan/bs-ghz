import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'stalls',
        name: 'StallList',
        component: () => import('@/views/stall/StallList.vue'),
        meta: { title: '档口列表' },
      },
      {
        path: 'stalls/:id',
        name: 'StallDetail',
        component: () => import('@/views/stall/StallDetail.vue'),
        meta: { title: '档口详情' },
      },
      {
        path: 'dishes',
        name: 'DishList',
        component: () => import('@/views/dish/DishList.vue'),
        meta: { title: '菜品列表' },
      },
      {
        path: 'dishes/:id',
        name: 'DishDetail',
        component: () => import('@/views/dish/DishDetail.vue'),
        meta: { title: '菜品详情' },
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/cart/Cart.vue'),
        meta: { title: '购物车', requiresAuth: true },
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/order/OrderList.vue'),
        meta: { title: '我的订单', requiresAuth: true },
      },
      {
        path: 'orders/:id',
        name: 'OrderDetail',
        component: () => import('@/views/order/OrderDetail.vue'),
        meta: { title: '订单详情', requiresAuth: true },
      },
      {
        path: 'favorites',
        name: 'FavoriteList',
        component: () => import('@/views/favorite/FavoriteList.vue'),
        meta: { title: '我的收藏', requiresAuth: true },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true },
      },
      {
        path: 'complaints',
        name: 'ComplaintList',
        component: () => import('@/views/complaint/ComplaintList.vue'),
        meta: { title: '我的投诉', requiresAuth: true },
      },
      // 商户管理
      {
        path: 'merchant/stalls',
        name: 'MerchantStalls',
        component: () => import('@/views/merchant/MyStalls.vue'),
        meta: { title: '我的档口', requiresAuth: true },
      },
      {
        path: 'merchant/stalls/:id',
        name: 'StallManage',
        component: () => import('@/views/merchant/StallManage.vue'),
        meta: { title: '档口管理', requiresAuth: true },
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  },
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || '校园餐饮'} - 校园餐饮管理系统`
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({ path: '/login', query: { redirect: to.fullPath } })
      return
    }
  }
  next()
})

export default router
