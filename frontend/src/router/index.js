import { createRouter, createWebHistory } from 'vue-router'
import { getToken, hasRole } from '../utils/auth'
import MainLayout from '../layouts/MainLayout.vue'

const routes = [
  { path: '/login', component: () => import('../views/LoginView.vue') },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', component: () => import('../views/DashboardView.vue'), meta: { title: '控制台' } },
      { path: 'bugs', component: () => import('../views/BugListView.vue'), meta: { title: 'Bug 列表' } },
      { path: 'bugs/new', component: () => import('../views/BugFormView.vue'), meta: { title: '新增 Bug', roles: ['ADMIN', 'TESTER'] } },
      { path: 'bugs/:id/edit', component: () => import('../views/BugFormView.vue'), meta: { title: '编辑 Bug' } },
      { path: 'bugs/:id', component: () => import('../views/BugDetailView.vue'), meta: { title: 'Bug 详情' } },
      { path: 'statistics', component: () => import('../views/StatisticsView.vue'), meta: { title: '统计分析' } },
      { path: 'users', component: () => import('../views/UsersView.vue'), meta: { title: '用户管理', roles: ['ADMIN'] } }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path === '/login') return true
  if (!getToken()) return '/login'
  if (!hasRole(to.meta.roles)) return '/dashboard'
  return true
})

export default router
