<template>
  <div class="page-stack">
    <div class="panel-header">
      <div>
        <h2>{{ isEdit ? '编辑 Bug' : '新增 Bug' }}</h2>
        <p>记录缺陷标题、模块、严重程度、复现步骤和处理人</p>
      </div>
    </div>

    <div class="glass-card">
      <el-form :model="form" label-position="top">
        <el-row :gutter="16">
          <el-col :xs="24" :md="12">
            <el-form-item label="标题" required>
              <el-input v-model="form.title" placeholder="请输入缺陷标题" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="所属模块">
              <el-input v-model="form.moduleName" placeholder="例如：用户登录 / 缺陷管理" />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="严重程度">
              <el-select v-model="form.severity" style="width: 100%">
                <el-option v-for="item in severities" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :md="12">
            <el-form-item label="处理人">
              <el-select v-model="form.assigneeId" style="width: 100%" clearable>
                <el-option v-for="item in users" :key="item.id" :label="item.nickname" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="问题描述">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="描述问题现象" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="复现步骤">
              <el-input v-model="form.reproduceSteps" type="textarea" :rows="4" placeholder="1. ... 2. ... 3. ..." />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="截图地址 / 文件名">
              <el-input v-model="form.screenshotUrl" placeholder="例如：login-error.png" />
            </el-form-item>
          </el-col>
        </el-row>

        <div style="display:flex; gap: 10px; justify-content:flex-end">
          <el-button @click="$router.back()">取消</el-button>
          <el-button type="primary" :loading="loading" @click="submit">保存</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { bugApi, userApi } from '../api/modules'
import { getUser } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const user = getUser()
const loading = ref(false)
const users = ref([])
const severities = ['低', '中', '高', '严重']
const isEdit = computed(() => Boolean(route.params.id))
const form = reactive({
  title: '',
  moduleName: '',
  severity: '中',
  description: '',
  reproduceSteps: '',
  screenshotUrl: '',
  assigneeId: 3
})

onMounted(async () => {
  await loadUsers()
  if (isEdit.value) await loadDetail()
})

async function loadUsers() {
  if (user.role === 'ADMIN') {
    users.value = await userApi.list()
  } else {
    users.value = [{ id: 1, nickname: '管理员' }, { id: 2, nickname: '测试人员' }, { id: 3, nickname: '开发人员' }]
  }
}

async function loadDetail() {
  const detail = await bugApi.detail(route.params.id)
  Object.assign(form, {
    title: detail.title,
    moduleName: detail.moduleName,
    severity: detail.severity,
    description: detail.description,
    reproduceSteps: detail.reproduceSteps,
    screenshotUrl: detail.screenshotUrl,
    assigneeId: detail.assigneeId
  })
}

async function submit() {
  if (!form.title.trim()) {
    ElMessage.warning('请填写标题')
    return
  }
  loading.value = true
  try {
    if (isEdit.value) {
      await bugApi.update(route.params.id, form)
      ElMessage.success('Bug 已更新')
      router.push(`/bugs/${route.params.id}`)
    } else {
      const created = await bugApi.create(form)
      ElMessage.success('Bug 已新增')
      router.push(`/bugs/${created.id}`)
    }
  } finally {
    loading.value = false
  }
}
</script>
