<template>
  <div class="app-shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <img src="../assets/bugflow-logo.svg" alt="BugFlow 缺陷管理系统图标" />
        </div>
        <div>
          <strong>BugFlow</strong>
          <span>缺陷管理系统</span>
        </div>
      </div>

      <nav class="nav-list">
        <router-link v-for="item in visibleMenus" :key="item.path" :to="item.path" class="nav-item">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </router-link>
      </nav>

      <div class="sidebar-card">
        <span>状态流转</span>
        <strong>已提交 → 已修复 → 已关闭</strong>
      </div>
    </aside>

    <main class="main-panel">
      <header class="topbar">
        <div>
          <p class="eyebrow">Defect Management</p>
          <h1>{{ route.meta.title || '控制台' }}</h1>
        </div>
        <div class="top-actions">
          <el-tag effect="dark" round>{{ roleName }}</el-tag>
          <el-dropdown>
            <button class="user-chip">
              <el-icon><User /></el-icon>
              {{ user?.nickname || user?.username }}
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <section class="content-stage">
        <router-view />
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { clearSession, getUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const user = computed(() => getUser())

const menus = [
  { label: '控制台', path: '/dashboard', icon: 'DataBoard' },
  { label: 'Bug 列表', path: '/bugs', icon: 'List' },
  { label: '新增 Bug', path: '/bugs/new', icon: 'CirclePlus', roles: ['ADMIN', 'TESTER'] },
  { label: '统计分析', path: '/statistics', icon: 'PieChart' },
  { label: '用户管理', path: '/users', icon: 'UserFilled', roles: ['ADMIN'] }
]

const visibleMenus = computed(() => {
  const role = user.value?.role
  return menus.filter((item) => !item.roles || item.roles.includes(role))
})

const roleName = computed(() => {
  const roleMap = { ADMIN: '管理员', TESTER: '测试人员', DEVELOPER: '开发人员' }
  return roleMap[user.value?.role] || '用户'
})

function logout() {
  clearSession()
  router.replace('/login')
}
</script>
