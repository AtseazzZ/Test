<script setup>
import { ref } from 'vue'
import { Files, Upload, Refresh, View } from '@element-plus/icons-vue'
import {
  ElTable,
  ElTableColumn,
  ElTag,
  ElButton,
  ElSelect,
  ElOption,
  ElDatePicker,
  ElMessage
} from 'element-plus'

const filterForm = ref({
  month: '',
  company: '',
  account: '',
  range: ''
})

const companies = [
  { label: '全部', value: '' },
  { label: '上海总部', value: 'shanghai' },
  { label: '北京分公司', value: 'beijing' },
  { label: '广州分公司', value: 'guangzhou' }
]

const accounts = [
  { label: '全部', value: '' },
  { label: '工商银行基本户', value: 'icbc_main' },
  { label: '建设银行一般户', value: 'ccb_normal' }
]

const voucherList = ref([
  {
    id: 1,
    voucherNo: 'V20240115001',
    type: 'income',
    amount: 125000,
    createTime: '2024-01-15 10:30',
    status: 'generated',
    pushStatus: 'pending'
  },
  {
    id: 2,
    voucherNo: 'V20240115002',
    type: 'cost',
    amount: 86000,
    createTime: '2024-01-15 11:20',
    status: 'generated',
    pushStatus: 'success'
  },
  {
    id: 3,
    voucherNo: 'V20240114003',
    type: 'expense',
    amount: 15800,
    createTime: '2024-01-14 14:45',
    status: 'generated',
    pushStatus: 'failed'
  },
  {
    id: 4,
    voucherNo: 'V20240114004',
    type: 'income',
    amount: 62000,
    createTime: '2024-01-14 16:00',
    status: 'generated',
    pushStatus: 'success'
  },
  {
    id: 5,
    voucherNo: 'V20240113005',
    type: 'cost',
    amount: 9800,
    createTime: '2024-01-13 09:15',
    status: 'pending',
    pushStatus: 'pending'
  }
])

const getTypeText = (type) => {
  const map = { income: '收入凭证', cost: '成本凭证', expense: '费用凭证' }
  return map[type] || type
}

const getTypeTag = (type) => {
  const map = { income: 'success', cost: 'primary', expense: 'info' }
  return map[type] || 'default'
}

const getStatusText = (status) => {
  const map = { pending: '待生成', generated: '已生成', pushing: '推送中' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { pending: 'warning', generated: 'success', pushing: 'info' }
  return map[status] || 'default'
}

const getPushStatusText = (status) => {
  const map = { pending: '待推送', success: '推送成功', failed: '推送失败' }
  return map[status] || status
}

const getPushStatusTag = (status) => {
  const map = { pending: 'warning', success: 'success', failed: 'danger' }
  return map[status] || 'default'
}

const handleGenerateIncome = () => {
  ElMessage.success('收入凭证生成成功')
}

const handleGenerateCost = () => {
  ElMessage.success('成本凭证生成成功')
}

const handleGenerateExpense = () => {
  ElMessage.success('费用凭证生成成功')
}

const handlePushToKingdee = () => {
  ElMessage.success('凭证已推送至金蝶云星空')
}

const handleView = (row) => {
  ElMessage.info('查看凭证详情')
}

const handlePush = (row) => {
  ElMessage.success('凭证推送成功')
}

const handleRetry = (row) => {
  ElMessage.success('推送重试成功')
}
</script>

<template>
  <div class="voucher-page">
    <div class="page-header">
      <h1>凭证生成</h1>
      <p>一键生成财务凭证，对接金蝶云星空</p>
    </div>
    
    <div class="filter-section">
      <div class="filter-row">
        <ElDatePicker
          v-model="filterForm.month"
          type="month"
          placeholder="选择月份"
          class="filter-input"
        />
        <ElSelect v-model="filterForm.company" placeholder="公司" class="filter-input">
          <ElOption v-for="opt in companies" :key="opt.value" :label="opt.label" :value="opt.value" />
        </ElSelect>
        <ElSelect v-model="filterForm.account" placeholder="账户" class="filter-input">
          <ElOption v-for="opt in accounts" :key="opt.value" :label="opt.label" :value="opt.value" />
        </ElSelect>
        <ElSelect v-model="filterForm.range" placeholder="运单/对账范围" class="filter-input">
          <ElOption label="全部" value="" />
          <ElOption label="本月运单" value="current_month" />
          <ElOption label="上月运单" value="last_month" />
          <ElOption label="已对账" value="reconciled" />
        </ElSelect>
        <ElButton type="primary">查询</ElButton>
      </div>
    </div>
    
    <div class="action-section">
      <div class="action-card">
        <div class="action-icon income">
          <Files />
        </div>
        <div class="action-info">
          <span class="action-title">收入凭证</span>
          <span class="action-desc">基于应收账款自动生成收入凭证</span>
        </div>
        <ElButton type="primary" @click="handleGenerateIncome">一键生成</ElButton>
      </div>
      
      <div class="action-card">
        <div class="action-icon cost">
          <Files />
        </div>
        <div class="action-info">
          <span class="action-title">成本凭证</span>
          <span class="action-desc">基于应付账款自动生成成本凭证</span>
        </div>
        <ElButton type="primary" @click="handleGenerateCost">一键生成</ElButton>
      </div>
      
      <div class="action-card">
        <div class="action-icon expense">
          <Files />
        </div>
        <div class="action-info">
          <span class="action-title">费用凭证</span>
          <span class="action-desc">基于费用报销自动生成费用凭证</span>
        </div>
        <ElButton type="primary" @click="handleGenerateExpense">一键生成</ElButton>
      </div>
    </div>
    
    <div class="push-section">
      <div class="push-card">
        <div class="push-icon">
          <Upload />
        </div>
        <div class="push-info">
          <span class="push-title">推送至金蝶云星空</span>
          <span class="push-desc">将生成的凭证同步至金蝶财务系统</span>
        </div>
        <ElButton type="success" size="large" @click="handlePushToKingdee">
          推送至金蝶
        </ElButton>
      </div>
    </div>
    
    <div class="list-section">
      <div class="section-header">
        <h2>凭证列表</h2>
      </div>
      <ElTable :data="voucherList" border class="main-table">
        <ElTableColumn prop="voucherNo" label="凭证号" />
        <ElTableColumn prop="type" label="类型">
          <template #default="scope">
            <ElTag :type="getTypeTag(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
        <ElTableColumn prop="createTime" label="生成时间" />
        <ElTableColumn prop="status" label="状态">
          <template #default="scope">
            <ElTag :type="getStatusTag(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="pushStatus" label="推送状态">
          <template #default="scope">
            <ElTag :type="getPushStatusTag(scope.row.pushStatus)">
              {{ getPushStatusText(scope.row.pushStatus) }}
            </ElTag>
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作">
          <template #default="scope">
            <ElButton size="small" @click="handleView(scope.row)">
              <ElIcon color="#409eff">
                <View />
              </ElIcon>
              查看
            </ElButton>
            <ElButton
              v-if="scope.row.pushStatus === 'pending'"
              size="small"
              type="primary"
              @click="handlePush(scope.row)"
            >
              <ElIcon color="#ffffff">
                <Upload />
              </ElIcon>
              推送
            </ElButton>
            <ElButton
              v-if="scope.row.pushStatus === 'failed'"
              size="small"
              type="warning"
              @click="handleRetry(scope.row)"
            >
              <ElIcon color="#ffffff">
                <Refresh />
              </ElIcon>
              重推
            </ElButton>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.voucher-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 8px;
}

.page-header p {
  color: #6b7280;
}

.filter-section {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.filter-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.filter-input {
  width: 200px;
}

.action-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.action-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.action-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  
  &.income {
    background: linear-gradient(135deg, #10b981, #059669);
  }
  
  &.cost {
    background: linear-gradient(135deg, #3b82f6, #2563eb);
  }
  
  &.expense {
    background: linear-gradient(135deg, #6366f1, #4f46e5);
  }
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  display: block;
  margin-bottom: 4px;
}

.action-desc {
  font-size: 14px;
  color: #6b7280;
}

.push-section {
  margin-bottom: 30px;
}

.push-card {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.push-icon {
  width: 64px;
  height: 64px;
  background: white;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #f59e0b;
}

.push-info {
  flex: 1;
}

.push-title {
  font-size: 18px;
  font-weight: 600;
  color: #92400e;
  display: block;
  margin-bottom: 4px;
}

.push-desc {
  font-size: 14px;
  color: #b45309;
}

.list-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: 600;
}

.main-table {
  margin-top: 0;
}
</style>
