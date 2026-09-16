<script setup>
import { ref } from 'vue'
import { Warning, View, Check, Close } from '@element-plus/icons-vue'
import {
  ElTable,
  ElTableColumn,
  ElTag,
  ElButton,
  ElDialog,
  ElForm,
  ElFormItem,
  ElInput,
  ElMessage,
  ElSteps,
  ElStep
} from 'element-plus'

const showDetailDialog = ref(false)
const showRejectDialog = ref(false)
const selectedRow = ref(null)
const rejectReason = ref('')

const approvalList = ref([
  {
    id: 1,
    type: 'ship_freight',
    typeText: '船运费',
    orderNo: 'AP20240115001',
    amount: 12507.75,
    applicant: '张财务',
    currentNode: 'accountant',
    currentNodeText: '会计',
    status: 'pending',
    flow: ['财务助理', '对账员', '会计', '出纳', '总经理'],
    currentStep: 2
  },
  {
    id: 2,
    type: 'truck_freight',
    typeText: '车队运费',
    orderNo: 'AP20240115002',
    amount: 6348.16,
    applicant: '李对账',
    currentNode: 'cashier',
    currentNodeText: '出纳',
    status: 'pending',
    flow: ['对账员', '会计', '出纳', '总经理'],
    currentStep: 2
  },
  {
    id: 3,
    type: 'prepayment',
    typeText: '预付款',
    orderNo: 'AP20240114003',
    amount: 50000,
    applicant: '王市场',
    currentNode: 'finance_assistant',
    currentNodeText: '财务助理',
    status: 'pending',
    flow: ['市场总监', '财务助理', '对账员', '出纳', '总经理'],
    currentStep: 1
  },
  {
    id: 4,
    type: 'detention',
    typeText: '压车费',
    orderNo: 'AP20240113004',
    amount: 2300,
    applicant: '赵客服',
    currentNode: 'market_director',
    currentNodeText: '市场总监',
    status: 'pending',
    flow: ['客服', '市场总监', '总经理'],
    currentStep: 1
  },
  {
    id: 5,
    type: 'railway_freight',
    typeText: '铁路运费',
    orderNo: 'AP20240112005',
    amount: 6435.36,
    applicant: '孙对账',
    currentNode: 'general_manager',
    currentNodeText: '总经理',
    status: 'pending',
    flow: ['对账员', '会计', '出纳', '总经理'],
    currentStep: 3
  }
])

const getTypeTag = (type) => {
  const map = {
    ship_freight: 'primary',
    truck_freight: 'info',
    railway_freight: 'info',
    port_freight: 'info',
    prepayment: 'warning',
    detention: 'danger'
  }
  return map[type] || 'default'
}

const getStatusText = (status) => {
  const map = { pending: '待审批', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

const getStatusTag = (status) => {
  const map = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'default'
}

const handleApprove = (row) => {
  if (row.currentStep < row.flow.length - 1) {
    row.currentStep++
    row.currentNodeText = row.flow[row.currentStep]
    ElMessage.success(`已同意，流转至${row.currentNodeText}`)
  } else {
    row.status = 'approved'
    ElMessage.success('审批通过')
  }
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
  selectedRow.value.status = 'rejected'
  ElMessage.success('已驳回')
  showRejectDialog.value = false
  selectedRow.value = null
  rejectReason.value = ''
}

const handleView = (row) => {
  selectedRow.value = row
  showDetailDialog.value = true
}
</script>

<template>
  <div class="approval-page">
    <div class="page-header">
      <h1>审批中心</h1>
      <p>财务审批流程管理</p>
    </div>
    
    <div class="flow-info">
      <div class="flow-card">
        <div class="flow-title">审批流程说明</div>
        <div class="flow-list">
          <div class="flow-item">
            <Warning class="flow-icon" />
            <span><strong>船运费：</strong>财务助理 → 对账员 → 会计 → 出纳 → 总经理</span>
          </div>
          <div class="flow-item">
            <Warning class="flow-icon" />
            <span><strong>车队/码头/铁路：</strong>对账员 → 会计 → 出纳 → 总经理</span>
          </div>
          <div class="flow-item">
            <Warning class="flow-icon" />
            <span><strong>预付款：</strong>市场总监 → 财务助理 → 对账员 → 出纳 → 总经理</span>
          </div>
          <div class="flow-item">
            <Warning class="flow-icon" />
            <span><strong>压车费：</strong>客服 → 市场总监 → 总经理</span>
          </div>
        </div>
      </div>
    </div>
    
    <ElTable :data="approvalList" border class="main-table">
      <ElTableColumn prop="typeText" label="审批类型">
        <template #default="scope">
          <ElTag :type="getTypeTag(scope.row.type)">
            {{ scope.row.typeText }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="orderNo" label="单号" />
      <ElTableColumn prop="amount" label="金额">
          <template #default="scope">¥ {{ scope.row.amount }}</template>
        </ElTableColumn>
      <ElTableColumn prop="applicant" label="申请人" />
      <ElTableColumn prop="currentNodeText" label="当前节点">
        <template #default="scope">
          <ElTag type="primary">{{ scope.row.currentNodeText }}</ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="status" label="状态">
        <template #default="scope">
          <ElTag :type="getStatusTag(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn label="操作">
        <template #default="scope">
          <ElButton
            size="small"
            type="success"
            @click="handleApprove(scope.row)"
            :disabled="scope.row.status !== 'pending'"
          >
            <ElIcon color="#ffffff">
              <Check />
            </ElIcon>
            同意
          </ElButton>
          <ElButton
            size="small"
            type="danger"
            @click="handleReject(scope.row)"
            :disabled="scope.row.status !== 'pending'"
          >
            <ElIcon color="#ffffff">
              <Close />
            </ElIcon>
            驳回
          </ElButton>
          <ElButton size="small" @click="handleView(scope.row)">
            <ElIcon color="#409eff">
              <View />
            </ElIcon>
            查看
          </ElButton>
        </template>
      </ElTableColumn>
    </ElTable>
    
    <ElDialog title="审批详情" v-model="showDetailDialog" width="600px">
      <div v-if="selectedRow">
        <div class="detail-info">
          <div class="detail-row">
            <span class="detail-label">审批类型</span>
            <ElTag :type="getTypeTag(selectedRow.type)">{{ selectedRow.typeText }}</ElTag>
          </div>
          <div class="detail-row">
            <span class="detail-label">单号</span>
            <span>{{ selectedRow.orderNo }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">金额</span>
            <span>¥ {{ selectedRow.amount }}</span>
          </div>
          <div class="detail-row">
            <span class="detail-label">申请人</span>
            <span>{{ selectedRow.applicant }}</span>
          </div>
        </div>
        
        <div class="flow-section">
          <div class="flow-title">审批流程</div>
          <ElSteps :active="selectedRow.currentStep" space="40">
            <ElStep v-for="(step, index) in selectedRow.flow" :key="index" :title="step" />
          </ElSteps>
        </div>
      </div>
      <template #footer>
        <ElButton @click="showDetailDialog = false">关闭</ElButton>
      </template>
    </ElDialog>
    
    <ElDialog title="驳回审批" v-model="showRejectDialog">
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
.approval-page {
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

.flow-info {
  margin-bottom: 20px;
}

.flow-card {
  background: #fffbeb;
  border: 1px solid #fef3c7;
  border-radius: 12px;
  padding: 20px;
}

.flow-title {
  font-size: 16px;
  font-weight: 600;
  color: #92400e;
  margin-bottom: 16px;
}

.flow-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.flow-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #78350f;
}

.flow-icon {
  color: #f59e0b;
  width: 30px;
}

.main-table {
  background: white;
  border-radius: 12px;
}

.detail-info {
  margin-bottom: 24px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
  
  &:last-child {
    border-bottom: none;
  }
}

.detail-label {
  color: #6b7280;
  font-weight: 500;
}

.flow-section {
  margin-top: 20px;
}

.flow-section .flow-title {
  margin-bottom: 16px;
}
</style>
