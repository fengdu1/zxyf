import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { getToken } from '@/utils/auth'

const Layout = () => import('@/layout/index.vue')

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', public: true },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/home/index.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'clazz',
        name: 'clazz',
        component: () => import('@/views/clazz/index.vue'),
        meta: { title: '班级管理' },
      },
      {
        path: 'student',
        name: 'student',
        component: () => import('@/views/student/index.vue'),
        meta: { title: '学员管理' },
      },
      {
        path: 'department',
        name: 'department',
        component: () => import('@/views/department/index.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'employee',
        name: 'employee',
        component: () => import('@/views/employee/index.vue'),
        meta: { title: '员工管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫：未登录访问受保护页面则跳转登录
router.beforeEach((to) => {
  const hasToken = Boolean(getToken())
  if (!to.meta.public && !hasToken) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && hasToken) {
    return { path: '/' }
  }
  return true
})

export default router