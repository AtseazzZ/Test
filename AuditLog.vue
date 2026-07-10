<template>
  <div class="audit-log">
    <h2>审计日志</h2>
    <p class="hint">记录所有操作: who / what / when / outcome</p>
    <el-table :data="logs" border stripe v-loading="loading">
      <el-table-column prop="who" label="操作人" width="100" />
      <el-table-column prop="action" label="动作" width="120" />
      <el-table-column prop="targetType" label="目标类型" width="100" />
      <el-table-column prop="targetId" label="目标ID" width="80" />
      <el-table-column prop="outcome" label="结果" width="80">
        <template #default="{ row }">
          <el-tag :type="row.outcome === 'SUCCESS' ? 'success' : 'danger'">
            {{ row.outcome }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="detail" label="详情" min-width="160" />
      <el-table-column prop="createTime" label="时间" width="180" />
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { auditApi } from '../api'

const logs = ref([])
const loading = ref(false)

async function loadLogs() {
  loading.value = true
  try {
    const res = await auditApi.list()
    logs.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>

<style scoped>
.hint { color: #999; margin-bottom: 16px; }
</style>