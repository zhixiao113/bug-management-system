<template>
  <div class="page-stack">
    <div class="panel-header">
      <div>
        <h2>用户管理</h2>
        <p>系统预置三种角色账号，用于权限演示</p>
      </div>
    </div>

    <div class="table-card">
      <el-table :data="users" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="角色">
          <template #default="{ row }"><el-tag>{{ roleName(row.role) }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { userApi } from '../api/modules'

const users = ref([])
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    users.value = await userApi.list()
  } finally {
    loading.value = false
  }
})

function roleName(role) {
  return { ADMIN: '管理员', TESTER: '测试人员', DEVELOPER: '开发人员' }[role] || role
}
</script>
