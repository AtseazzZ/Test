<script setup>
import { ref } from 'vue'
import { Download, Warning } from '@element-plus/icons-vue'
import {
  ElTable,
  ElTableColumn,
  ElTag,
  ElButton,
  ElTabs,
  ElTabPane,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage
} from 'element-plus'

const activeTab = ref('customer')
const showRejectDialog = ref(false)
const selectedRow = ref(null)

const customerReconciliation = ref([
  {
    id: 1,
    billNo: 'R20240115001',
    customer: '上海XX物流有限公司',
    month: '2024年1月',
    amount: 125000,
    marketStatus: 'confirmed',
    financeStatus: 'pending',
    hasDiff: false,
    diffAmount: 0
  },
  {
    id: 2,
    billNo: 'R20240115002',
    customer: '北京XX贸易有限公司',
    month: '2024年1月',
    amount: 86000,
    marketStatus: 'confirmed',
    financeStatus: 'pending',
    hasDiff: true,
    diffAmount: 500
  },
  {
    id: 3,
    billNo: 'R20240114003',
    customer: '广州XX制造有限公司',
    month: '2024年1月',
    amount: 158000,
    marketStatus: 'confirmed',
    financeStatus: 'confirmed',
    hasDiff: false,
    diffAmount: 0
  },
  {
    id: 4,
    billNo: 'R20240114004',
    customer: '深圳XX电子有限公司',
    month: '2024年1月',
    amount: 62000,
    marketStatus: 'pending',
    financeStatus: 'pending',
    hasDiff: false,
    diffAmount: 0
  }
])

const supplierReconciliation = ref([
  {
    id: 1,
    billNo: 'S20240115001',
    supplier: '中远海运集团',
    batch: '202401批次',
    amount: 114750,
    marketStatus: 'confirmed',
    financeStatus: 'confirmed',
    hasDiff: false,
    diffAmount: 0
  },
  {
    id: 2,
    billNo: 'S20240115002',
    supplier: '上海XX车队',
    batch: '202401批次',
    amount: 58240,
    marketStatus: 'confirmed',
    financeStatus: 'pending',
    hasDiff: true,
    diffAmount: 800
  },
  {
    id: 3,
    billNo: 'S20240114003',
    supplier: '中铁快运',
    batch: '202401批次',
    amount: 68800,
    marketStatus: 'pending',
    financeStatus: 'pending',
    hasDiff: false,
    diffAmount: 0
  }
])

const rejectReason = ref('')

const getStatusText = (status) => {
  const map = { pending: '待确认', confirmed: '已确认', rejected: '已驳回' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { pending: 'warning', confirmed: 'success', rejected: 'danger' }
  return map[status] || 'default'
}

const handleFinanceConfirm = (row) => {
  if (row.marketStatus !== 'confirmed') {
    ElMessage.warning('请先等待市场确认')
    return
  }
  ElMessage.success('财务确认成功')
}

const handleReject = (row) => {
  selectedRow.value = row
  showRejectDialog.value = true
}

const confirmReject = () => {
  if (!rejectReason.value.trim()) {
    ElMessage.warning('请填写驳回原因')
    return
  }
  ElMessage.success('驳回成功')
  showRejectDialog.value = false
  selectedRow.value = null
  rejectReason.value = ''
}

const handleExport = () => {
  ElMessage.success('对账明细导出成功')
}
</script>

<template>
  <div class="reconciliation-page">
    <div class="page-header">
      <h1>对账中心</h1>
      <p>两级确认流程：市场确认 → 财务确认</p>
    </div>
    
    <div class="toolbar">
      <ElButton @click="handleExport">
        <ElIcon color="#409eff">
          <Download />
        </ElIcon>
        导出对账明细
        </ElButton>
    </div>
    
    <ElTabs v-model="activeTab" type="card" class="tab-container">
      <ElTabPane label="客户对账" name="customer">
        <ElTable :data="customerReconciliation" border class="main-table">
          <ElTableColumn prop="billNo" label="对账单号" />
          <ElTableColumn prop="customer" label="客户" />
          <ElTableColumn prop="month" label="月份" />
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="marketStatus" label="市场确认状态">
            <template #default="scope">
              <ElTag :type="getStatusType(scope.row.marketStatus)">
                {{ getStatusText(scope.row.marketStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="financeStatus" label="财务确认状态">
            <template #default="scope">
              <ElTag :type="getStatusType(scope.row.financeStatus)">
                {{ getStatusText(scope.row.financeStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="hasDiff" label="差异记录">
            <template #default="scope">
              <div v-if="scope.row.hasDiff" class="diff-warning">
                <Warning />
                <span>差异 ¥{{ scope.row.diffAmount }}</span>
              </div>
              <span v-else class="no-diff">无差异</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="primary"
                @click="handleFinanceConfirm(scope.row)"
                :disabled="scope.row.marketStatus !== 'confirmed' || scope.row.financeStatus === 'confirmed'"
              >
                财务确认
              </ElButton>
              <ElButton
                size="small"
                type="danger"
                @click="handleReject(scope.row)"
                :disabled="scope.row.financeStatus === 'confirmed'"
              >
                驳回
              </ElButton>
              <ElButton size="small">查看明细</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
      
      <ElTabPane label="供应商对账" name="supplier">
        <ElTable :data="supplierReconciliation" border class="main-table">
          <ElTableColumn prop="billNo" label="对账单号" />
          <ElTableColumn prop="supplier" label="供应商" />
          <ElTableColumn prop="batch" label="批次" />
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="marketStatus" label="市场确认状态">
            <template #default="scope">
              <ElTag :type="getStatusType(scope.row.marketStatus)">
                {{ getStatusText(scope.row.marketStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="financeStatus" label="财务确认状态">
            <template #default="scope">
              <ElTag :type="getStatusType(scope.row.financeStatus)">
                {{ getStatusText(scope.row.financeStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="hasDiff" label="差异记录">
            <template #default="scope">
              <div v-if="scope.row.hasDiff" class="diff-warning">
                <Warning />
                <span>差异 ¥{{ scope.row.diffAmount }}</span>
              </div>
              <span v-else class="no-diff">无差异</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="primary"
                @click="handleFinanceConfirm(scope.row)"
                :disabled="scope.row.marketStatus !== 'confirmed' || scope.row.financeStatus === 'confirmed'"
              >
                财务确认
              </ElButton>
              <ElButton
                size="small"
                type="danger"
                @click="handleReject(scope.row)"
                :disabled="scope.row.financeStatus === 'confirmed'"
              >
                驳回
              </ElButton>
              <ElButton size="small">查看明细</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
    </ElTabs>
    
    <ElDialog title="驳回对账" v-model="showRejectDialog">
      <ElForm :model="rejectReason" label-width="100px">
        <ElFormItem label="驳回原因">
          <ElInput v-model="rejectReason" rows="4" placeholder="请输入驳回原因" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showRejectDialog = false">取消</ElButton>
        <ElButton type="danger" @click="confirmReject">确认驳回</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.reconciliation-page {
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

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.tab-container {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.main-table {
  margin-top: 20px;
}

.diff-warning {
  width: 80px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #f59e0b;
  font-size: 14px;
}

.no-diff {
  color: #10b981;
  font-size: 14px;
}
</style>
