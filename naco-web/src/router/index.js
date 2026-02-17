import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated, getUser } from '@/utils/auth'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/components/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/UserList.vue'),
        meta: { requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'merchants',
        name: 'MerchantList',
        component: () => import('@/views/MerchantList.vue')
      },
      {
        path: 'orders',
        name: 'OrderList',
        component: () => import('@/views/OrderList.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/naco-web/'),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiredRoles = to.matched
    .filter(record => record.meta && record.meta.roles)
    .flatMap(record => record.meta.roles || [])

  if (requiresAuth && !isAuthenticated()) {
    // 需要登录但未登录，跳转到登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }

  if (requiredRoles.length > 0) {
    const user = getUser()
    const userRole = user && user.role
    if (!userRole || !requiredRoles.includes(userRole)) {
      ElMessage.error('无权限访问该页面')
      next({ path: '/dashboard' })
      return
    }
  }

  if (to.path === '/login' && isAuthenticated()) {
    // 已登录访问登录页，跳转到首页
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
