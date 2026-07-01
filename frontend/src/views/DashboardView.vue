<template>
  <div class="page-stack">
    <div class="panel-header">
      <div>
        <h2>今日概览</h2>
        <p>按状态和严重程度快速了解缺陷处理进度</p>
      </div>
      <el-button type="primary" @click="$router.push('/bugs/new')" v-if="canCreate">新增 Bug</el-button>
    </div>

    <div class="stat-grid">
      <div v-for="item in cards" :key="item.label" class="stat-card" :style="{ '--accent': item.color }">
        <span>{{ item.label }}</span>
        <strong>{{ item.value }}</strong>
      </div>
    </div>

    <div class="chart-grid">
      <div class="glass-card">
        <div class="panel-header"><h2>状态统计</h2></div>
        <div ref="statusChart" class="chart-box"></div>
      </div>
      <div class="glass-card">
        <div class="panel-header"><h2>严重程度统计</h2></div>
        <div ref="severityChart" class="chart-box"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { statisticsApi } from '../api/modules'
import { getUser } from '../utils/auth'

const overview = ref({})
const statusChart = ref(null)
const severityChart = ref(null)
const user = getUser()
const canCreate = computed(() => ['ADMIN', 'TESTER'].includes(user?.role))

const cards = computed(() => [
  { label: 'Bug 总数', value: overview.value.total || 0, color: '#2563eb' },
  { label: '今日新增', value: overview.value.todayNew || 0, color: '#06b6d4' },
  { label: '未处理', value: overview.value.unresolved || 0, color: '#f59e0b' },
  { label: '已修复', value: overview.value.fixed || 0, color: '#10b981' },
  { label: '已关闭', value: overview.value.closed || 0, color: '#64748b' },
  { label: '严重 Bug', value: overview.value.serious || 0, color: '#ef4444' }
])

onMounted(load)

async function load() {
  overview.value = await statisticsApi.overview()
  await nextTick()
  renderCharts()
}

function renderCharts() {
  renderBar(statusChart.value, overview.value.statusStats || {}, ['#2563eb', '#10b981', '#64748b'])
  renderPie(severityChart.value, overview.value.severityStats || {})
}

function renderBar(el, data, colors) {
  const chart = echarts.init(el)
  chart.setOption({
    color: colors,
    grid: { top: 24, left: 36, right: 18, bottom: 28 },
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: Object.values(data), barWidth: 34, itemStyle: { borderRadius: [8, 8, 0, 0] } }]
  })
}

function renderPie(el, data) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['45%', '72%'],
      data: Object.entries(data).map(([name, value]) => ({ name, value })),
      label: { formatter: '{b}: {c}' }
    }]
  })
}
</script>
