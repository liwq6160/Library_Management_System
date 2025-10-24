import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'books',
        name: 'BookList',
        component: () => import('@/views/BookList.vue'),
        meta: { title: '图书列表', requiresAuth: true }
      },
      {
        path: 'books/:id',
        name: 'BookDetail',
        component: () => import('@/views/BookDetail.vue'),
        meta: { title: '图书详情', requiresAuth: true }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'book-management',
        name: 'BookManagement',
        component: () => import('@/views/BookManagement.vue'),
        meta: { title: '图书管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'category-management',
        name: 'CategoryManagement',
        component: () => import('@/views/CategoryManagement.vue'),
        meta: { title: '分类管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'my-borrows',
        name: 'MyBorrowRecords',
        component: () => import('@/views/MyBorrowRecords.vue'),
        meta: { title: '我的借阅', requiresAuth: true }
      },
      {
        path: 'borrow-management',
        name: 'BorrowManagement',
        component: () => import('@/views/BorrowManagement.vue'),
        meta: { title: '借阅管理', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'overdue-records',
        name: 'OverdueRecords',
        component: () => import('@/views/OverdueRecords.vue'),
        meta: { title: '逾期记录', requiresAuth: true, requiresAdmin: true }
      },
      {
        path: 'borrow-statistics',
        name: 'BorrowStatistics',
        component: () => import('@/views/BorrowStatistics.vue'),
        meta: { title: '借阅统计', requiresAuth: true, requiresAdmin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const isAdmin = userStore.isAdmin

  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 图书管理系统` : '图书管理系统'

  // 需要登录验证
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }

    // 需要管理员权限
    if (to.meta.requiresAdmin && !isAdmin) {
      ElMessage.error('您没有权限访问此页面')
      next('/')
      return
    }
  }

  // 已登录用户访问登录/注册页面，重定向到首页
  if ((to.path === '/login' || to.path === '/register') && isLoggedIn) {
    next('/')
    return
  }

  next()
})

export default router
