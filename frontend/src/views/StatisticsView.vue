<template>
  <div class="page-stack">
    <div class="panel-header">
      <div>
        <h2>统计分析</h2>
        <p>用于项目书截图的缺陷状态和严重程度统计</p>
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="item in cards" :key="item.label" class="stat-card" :style="{ '--accent': item.color }">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </div>

    <div class="chart-grid">
      <div class="glass-card"><div ref="statusChart" class="chart-box"></div></div>
      <div class="glass-card"><div ref="severityChart" class="chart-box"></div></div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../api/modules'

const overview = ref({})
const statusChart = ref(null)
const severityChart = ref(null)

const cards = computed(() => [
  { label: 'Bug 总数', value: overview.value.total || 0, color: '#2563eb' },
  { label: '今日新增', value: overview.value.todayNew || 0, color: '#06b6d4' },
  { label: '未处理', value: overview.value.unresolved || 0, color: '#f59e0b' },
  { label: '已修复', value: overview.value.fixed || 0, color: '#10b981' },
  { label: '已关闭', value: overview.value.closed || 0, color: '#64748b' },
  { label: '严重 Bug', value: overview.value.serious || 0, color: '#ef4444' }
])

onMounted(async () => {
  overview.value = await statisticsApi.overview()
  await nextTick()
  draw(statusChart.value, 'Bug 状态统计', overview.value.statusStats || {})
  draw(severityChart.value, 'Bug 严重程度统计', overview.value.severityStats || {})
})

function draw(el, title, data) {
  echarts.init(el).setOption({
    title: { text: title, left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['42%', '70%'],
      center: ['50%', '56%'],
      data: Object.entries(data).map(([name, value]) => ({ name, value }))
    }]
  })
}
</script>
