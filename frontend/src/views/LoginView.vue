<template>
  <div class="login-page">
    <section class="login-card">
      <div class="login-hero">
        <p class="eyebrow" style="color: rgba(255,255,255,.75)">BugFlow Console</p>
        <h1>缺陷流转，一屏掌控</h1>
        <p>面向学校期末项目的缺陷管理系统，覆盖提交、修复、关闭、转交和操作留痕，适合直接截图写入项目书。</p>
        <div class="status-flow" style="margin-top: 28px">
          <span class="flow-dot" style="background: rgba(255,255,255,.18); color: white">已提交</span>
          <span>→</span>
          <span class="flow-dot" style="background: rgba(255,255,255,.18); color: white">已修复</span>
          <span>→</span>
          <span class="flow-dot" style="background: rgba(255,255,255,.18); color: white">已关闭</span>
        </div>
      </div>

      <div class="login-form">
        <div class="panel-header" style="margin-bottom: 22px">
          <div>
            <h2>账号登录</h2>
            <p>请选择测试账号或输入用户名密码</p>
          </div>
        </div>

        <el-form :model="form" label-position="top" @keyup.enter="submit">
          <el-form-item label="用户名">
            <el-input v-model="form.username" size="large" placeholder="admin / tester / developer" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" size="large" type="password" show-password placeholder="123456" />
          </el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="submit">
            登录系统
          </el-button>
        </el-form>

        <div class="quick-users">
          <el-button v-for="item in users" :key="item.username" round @click="fill(item.username)">
            {{ item.label }}
          </el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api/modules'
import { setSession } from '../utils/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const users = [
  { label: '管理员', username: 'admin' },
  { label: '测试人员', username: 'tester' },
  { label: '开发人员', username: 'developer' }
]

function fill(username) {
  form.username = username
  form.password = '123456'
}

async function submit() {
  loading.value = true
  try {
    const result = await authApi.login(form)
    setSession(result.token, result.user)
    ElMessage.success('登录成功')
    router.replace('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>
