<template>
  <div class="page-stack" v-if="bug">
    <div class="panel-header">
      <div>
        <h2>#{{ bug.id }} {{ bug.title }}</h2>
        <p>{{ bug.moduleName }} · {{ bug.creatorName }} 创建 · {{ bug.updatedAt }}</p>
      </div>
      <div class="status-flow">
        <span class="flow-dot">已提交</span><span>→</span><span class="flow-dot">已修复</span><span>→</span><span class="flow-dot">已关闭</span>
      </div>
    </div>

    <div class="glass-card detail-grid">
      <div class="detail-item"><span>严重程度</span><el-tag>{{ bug.severity }}</el-tag></div>
      <div class="detail-item"><span>状态</span><el-tag>{{ bug.status }}</el-tag></div>
      <div class="detail-item"><span>创建人</span><strong>{{ bug.creatorName }}</strong></div>
      <div class="detail-item"><span>处理人</span><strong>{{ bug.assigneeName || '未指派' }}</strong></div>
      <div class="detail-item full"><span>问题描述</span><p>{{ bug.description || '暂无' }}</p></div>
      <div class="detail-item full"><span>复现步骤</span><p>{{ bug.reproduceSteps || '暂无' }}</p></div>
      <div class="detail-item full"><span>截图地址</span><p>{{ bug.screenshotUrl || '暂无' }}</p></div>
    </div>

    <div class="glass-card">
      <div class="panel-header" style="margin-bottom: 16px">
        <h2>操作记录</h2>
      </div>
      <el-timeline>
        <el-timeline-item v-for="log in logs" :key="log.id" :timestamp="log.createdAt" placement="top">
          <el-card shadow="never">
            <strong>{{ log.operationType }}</strong>
            <p class="muted">
              {{ log.operatorName }}：
              <template v-if="log.oldStatus || log.newStatus">{{ log.oldStatus || '-' }} → {{ log.newStatus || '-' }}</template>
              <template v-if="log.oldAssigneeName || log.newAssigneeName">，{{ log.oldAssigneeName || '未指派' }} → {{ log.newAssigneeName || '未指派' }}</template>
            </p>
            <p>{{ log.remark || '无备注' }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { bugApi } from '../api/modules'

const route = useRoute()
const bug = ref(null)
const logs = ref([])

onMounted(async () => {
  bug.value = await bugApi.detail(route.params.id)
  logs.value = await bugApi.logs(route.params.id)
})
</script>
