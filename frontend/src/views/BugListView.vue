<template>
  <div class="page-stack">
    <div class="panel-header">
      <div>
        <h2>Bug 列表</h2>
        <p>搜索、筛选、状态流转和转交处理人</p>
      </div>
      <el-button v-if="canCreate" type="primary" @click="$router.push('/bugs/new')">新增 Bug</el-button>
    </div>

    <div class="filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="标题">
          <el-input v-model="query.keyword" placeholder="搜索标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="item in statuses" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="query.severity" placeholder="全部" clearable style="width: 140px">
            <el-option v-for="item in severities" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form>
    </div>

    <div class="table-card">
      <el-table :data="bugs" v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="标题" min-width="210" />
        <el-table-column prop="moduleName" label="模块" width="120" />
        <el-table-column label="严重程度" width="110">
          <template #default="{ row }"><el-tag :type="severityType(row.severity)">{{ row.severity }}</el-tag></template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }"><el-tag :type="statusType(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="creatorName" label="创建人" width="110" />
        <el-table-column prop="assigneeName" label="处理人" width="110" />
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="300">
          <template #default="{ row }">
            <el-button text type="primary" @click="$router.push(`/bugs/${row.id}`)">详情</el-button>
            <el-button v-if="canEdit(row)" text @click="$router.push(`/bugs/${row.id}/edit`)">编辑</el-button>
            <el-button v-if="canChangeStatus(row)" text type="success" @click="openStatus(row)">状态</el-button>
            <el-button v-if="canAssign" text type="warning" @click="openAssign(row)">转交</el-button>
            <el-button v-if="user.role === 'ADMIN'" text type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="statusDialog" title="修改状态" width="420px">
      <el-form label-position="top">
        <el-form-item label="目标状态">
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option v-for="item in statuses" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="statusForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="statusDialog=false">取消</el-button><el-button type="primary" @click="submitStatus">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="assignDialog" title="转交处理人" width="420px">
      <el-form label-position="top">
        <el-form-item label="处理人">
          <el-select v-model="assignForm.assigneeId" style="width: 100%">
            <el-option v-for="item in users" :key="item.id" :label="item.nickname" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="assignDialog=false">取消</el-button><el-button type="primary" @click="submitAssign">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { bugApi, userApi } from '../api/modules'
import { getUser } from '../utils/auth'

const user = getUser()
const bugs = ref([])
const users = ref([])
const loading = ref(false)
const current = ref(null)
const query = reactive({ keyword: '', status: '', severity: '' })
const statuses = ['已提交', '已修复', '已关闭']
const severities = ['低', '中', '高', '严重']
const statusDialog = ref(false)
const assignDialog = ref(false)
const statusForm = reactive({ status: '', remark: '' })
const assignForm = reactive({ assigneeId: null, remark: '' })

const canCreate = computed(() => ['ADMIN', 'TESTER'].includes(user.role))
const canAssign = computed(() => ['ADMIN', 'DEVELOPER'].includes(user.role))

onMounted(() => {
  load()
  loadUsers()
})

async function load() {
  loading.value = true
  try {
    bugs.value = await bugApi.list(query)
  } finally {
    loading.value = false
  }
}

async function loadUsers() {
  if (user.role === 'ADMIN') {
    users.value = await userApi.list()
  } else {
    users.value = [{ id: 1, nickname: '管理员' }, { id: 2, nickname: '测试人员' }, { id: 3, nickname: '开发人员' }]
  }
}

function reset() {
  query.keyword = ''
  query.status = ''
  query.severity = ''
  load()
}

function canEdit(row) {
  return user.role === 'ADMIN' || (user.role === 'TESTER' && row.creatorId === user.id)
}

function canChangeStatus(row) {
  return user.role === 'ADMIN' || (user.role === 'DEVELOPER' && row.assigneeId === user.id) || (user.role === 'TESTER' && row.status === '已修复')
}

function openStatus(row) {
  current.value = row
  statusForm.status = row.status
  statusForm.remark = ''
  statusDialog.value = true
}

async function submitStatus() {
  await bugApi.updateStatus(current.value.id, statusForm)
  ElMessage.success('状态已更新')
  statusDialog.value = false
  load()
}

function openAssign(row) {
  current.value = row
  assignForm.assigneeId = row.assigneeId
  assignForm.remark = ''
  assignDialog.value = true
}

async function submitAssign() {
  await bugApi.assign(current.value.id, assignForm)
  ElMessage.success('处理人已转交')
  assignDialog.value = false
  load()
}

async function remove(row) {
  await ElMessageBox.confirm(`确认删除 Bug #${row.id}？`, '删除确认')
  await bugApi.remove(row.id)
  ElMessage.success('已删除')
  load()
}

function statusType(status) {
  return { 已提交: 'warning', 已修复: 'success', 已关闭: 'info' }[status] || ''
}

function severityType(severity) {
  return { 低: 'info', 中: 'primary', 高: 'warning', 严重: 'danger' }[severity] || ''
}
</script>
