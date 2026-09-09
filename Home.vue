<script setup>
import { ref } from 'vue'
import {
  Document,
  Check,
  Clock,
  Warning,
  ArrowUp,
  ArrowDown
} from '@element-plus/icons-vue'
import { ElCard, ElButton, ElTable, ElTableColumn, ElTag, ElProgress } from 'element-plus'

const todoList = ref([
  { id: 1, label: '待对账', count: 12, status: 'warning' },
  { id: 2, label: '待审核付款', count: 8, status: 'danger' },
  { id: 3, label: '待开票', count: 5, status: 'info' },
  { id: 4, label: '待生成凭证', count: 15, status: 'primary' }
])

const dataCards = ref([
  { title: '本月应收', value: '¥ 1,258,600', trend: 12.5, icon: Check, color: '#3b82f6' },
  { title: '本月应付', value: '¥ 986,400', trend: -3.2, icon: ArrowDown, color: '#ef4444' },
  { title: '已收', value: '¥ 856,200', trend: 8.7, icon: ArrowUp, color: '#10b981' },
  { title: '已付', value: '¥ 723,800', trend: 5.3, icon: ArrowDown, color: '#f59e0b' },
  { title: '待销账', value: '¥ 234,500', trend: -2.1, icon: Clock, color: '#6366f1' }
])

const recentRecords = ref([
  { id: 1, waybillNo: 'WB20240115001', customer: '上海XX物流', amount: 12500, status: '已确认', date: '2024-01-15' },
  { id: 2, waybillNo: 'WB20240115002', customer: '北京XX贸易', amount: 8600, status: '待确认', date: '2024-01-15' },
  { id: 3, waybillNo: 'WB20240114003', customer: '广州XX制造', amount: 15800, status: '已开票', date: '2024-01-14' },
  { id: 4, waybillNo: 'WB20240114004', customer: '深圳XX电子', amount: 6200, status: '已完结', date: '2024-01-14' },
  { id: 5, waybillNo: 'WB20240113005', customer: '杭州XX科技', amount: 9800, status: '待确认', date: '2024-01-13' }
])

const handleViewAll = () => {
  console.log('查看全部')
}
</script>

<template>
  <div class="dashboard">
    <div class="page-header">
      <h1>财务工作台</h1>
      <p>欢迎回来，财务管理员</p>
    </div>
    
    <div class="todo-section">
      <div class="section-header">
        <h2 class="section-title">待办事项</h2>
        <ElButton size="small" text @click="handleViewAll">查看全部</ElButton>
      </div>
      <div class="todo-grid">
        <div
          v-for="item in todoList"
          :key="item.id"
          class="todo-card"
          :class="item.status"
        >
          <div class="todo-icon">
            <Warning v-if="item.status === 'danger'" />
            <Clock v-else-if="item.status === 'warning'" />
            <Document v-else-if="item.status === 'info'" />
            <Check v-else />
          </div>
          <div class="todo-info">
            <span class="todo-label">{{ item.label }}</span>
            <span class="todo-count">{{ item.count }} 条</span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="cards-section">
      <div class="section-header">
        <h2 class="section-title">数据总览</h2>
      </div>
      <div class="cards-grid">
        <ElCard
          v-for="card in dataCards"
          :key="card.title"
          class="data-card"
        >
          <div class="card-content">
            <div class="card-icon" :style="{ background: card.color }">
              <component :is="card.icon" />
            </div>
            <div class="card-info">
              <span class="card-title">{{ card.title }}</span>
              <span class="card-value">{{ card.value }}</span>
              <span :class="['card-trend', card.trend > 0 ? 'up' : 'down']">
                {{ card.trend > 0 ? '+' : '' }}{{ card.trend }}%
              </span>
            </div>
          </div>
          <ElProgress :percentage="Math.min(Math.abs(card.trend) * 10, 100)" :color="card.color" :show-text="false" />
        </ElCard>
      </div>
    </div>
    
    <div class="recent-section">
      <div class="section-header">
        <h2 class="section-title">最近运单对账记录</h2>
        <ElButton size="small" text @click="handleViewAll">查看全部</ElButton>
      </div>
      <ElTable :data="recentRecords" border class="recent-table">
        <ElTableColumn prop="waybillNo" label="运单号" />
        <ElTableColumn prop="customer" label="客户" />
        <ElTableColumn prop="amount" label="金额">
          <template #default="scope">
            ¥ {{ scope.row.amount }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="status" label="状态">
          <template #default="scope">
            <ElTag :type="scope.row.status === '已完结' ? 'success' : scope.row.status === '已确认' ? 'primary' : scope.row.status === '已开票' ? 'info' : 'warning'">
              {{ scope.row.status }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="date" label="日期" />
      </ElTable>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.dashboard {
  padding: 20px;
}

.page-header {
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 8px;
}

.page-header p {
  color: #6b7280;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
}

.todo-section, .cards-section, .recent-section {
  margin-bottom: 30px;
}

.todo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.todo-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  
  &.danger {
    border-left: 4px solid #ef4444;
    .todo-icon { color: #ef4444; }
  }
  
  &.warning {
    border-left: 4px solid #f59e0b;
    .todo-icon { color: #f59e0b; }
  }
  
  &.info {
    border-left: 4px solid #3b82f6;
    .todo-icon { color: #3b82f6; }
  }
  
  &.primary {
    border-left: 4px solid #6366f1;
    .todo-icon { color: #6366f1; }
  }
}

.todo-icon {
  font-size: 24px;
}

.todo-info {
  display: flex;
  flex-direction: column;
}

.todo-label {
  font-size: 14px;
  color: #374151;
}

.todo-count {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.data-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-content {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.card-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.card-info {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #6b7280;
  display: block;
  margin-bottom: 4px;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  color: #1f2937;
  display: block;
}

.card-trend {
  font-size: 12px;
  margin-top: 4px;
  
  &.up {
    color: #10b981;
  }
  
  &.down {
    color: #ef4444;
  }
}

.recent-table {
  background: white;
  border-radius: 12px;
}
</style>
