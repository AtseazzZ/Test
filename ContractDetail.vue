<template>
  <div class="contract-detail" v-loading="loading">
    <el-page-header @back="goBack" :content="'合同详情 #' + contractId" />

    <el-card v-if="contract" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>{{ contract.name }}</span>
          <el-tag :type="statusType(contract.status)">{{ contract.statusName }}</el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ contract.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="客户">{{ contract.customer }}</el-descriptions-item>
        <el-descriptions-item label="金额">
          <template v-if="contract.amount !== null && contract.amount !== undefined">
            ¥{{ contract.amount.toLocaleString() }}
          </template>
          <template v-else>-</template>
        </el-descriptions-item>
        <el-descriptions-item label="归属部门">{{ contract.deptName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ contract.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ contract.createTime }}</el-descriptions-item>
        <el-descriptions-item label="条款明细" :span="2">
          <template v-if="contract.clauseDetail !== null && contract.clauseDetail !== undefined">
            {{ contract.clauseDetail }}
          </template>
          <template v-else>
            <span class="hidden-field">[此字段不可见]</span>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="附件">
          <template v-if="contract.attachment !== null && contract.attachment !== undefined">
            {{ contract.attachment }}
          </template>
          <template v-else>
            <span class="hidden-field">[此字段不可见]</span>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="对方银行账号">
          <template v-if="contract.bankAccount !== null && contract.bankAccount !== undefined">
            {{ contract.bankAccount }}
          </template>
          <template v-else>
            <span class="hidden-field">[此字段不可见]</span>
          </template>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">
          <template v-if="contract.remark !== null && contract.remark !== undefined">
            {{ contract.remark }}
          </template>
          <template v-else>
            <span class="hidden-field">[此字段不可见]</span>
          </template>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 操作按钮 -->
      <div class="actions" style="margin-top: 20px">
        <el-button v-if="contract.status === 'DRAFT'"
          v-permission="'contract:create'" type="success" @click="doSubmit">
          提交
        </el-button>
        <el-button v-if="contract.status === 'PENDING_ADMIN'"
          v-permission="'contract:process'" type="warning" @click="doProcess">
          处理
        </el-button>
        <el-button v-if="contract.status === 'PENDING_MANAGER' || contract.status === 'PENDING_LEADER'"
          v-permission="'contract:approve'" type="primary" @click="doApprove">
          审批
        </el-button>
        <el-button v-if="contract.status === 'DRAFT'"
          v-permission="'contract:withdraw'" type="danger" @click="doWithdraw">
          撤回
        </el-button>
      </div>
    </el-card>

    <!-- 审批记录 -->
    <el-card v-if="records.length > 0" style="margin-top: 20px">
      <template #header><span>审批记录</span></template>
      <el-timeline>
        <el-timeline-item v-for="r in records" :key="r.id"
          :timestamp="r.createTime" placement="top">
          <p><b>{{ r.operatorName }}</b> 执行了 <el-tag size="small">{{ r.action }}</el-tag></p>
          <p v-if="r.comment">意见: {{ r.comment }}</p>
          <p style="color: #999">{{ r.fromStatus }} → {{ r.toStatus }}</p>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 审批对话框 -->
    <el-dialog v-model="showApproval" title="审批意见" width="400px">
      <el-input v-model="approvalComment" type="textarea" placeholder="请输入审批意见（可选）" />
      <template #footer>
        <el-button @click="showApproval = false">取消</el-button>
        <el-button type="primary" @click="confirmApproval" :loading="approving">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { contractApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const contractId = ref(route.params.id)
const contract = ref(null)
const records = ref([])
const loading = ref(false)
const showApproval = ref(false)
const approving = ref(false)
const approvalComment = ref('')
const currentAction = ref('')

function statusType(status) {
  const map = { DRAFT: 'info', PENDING_ADMIN: 'warning', PENDING_MANAGER: 'warning',
                PENDING_LEADER: 'warning', APPROVED: 'success' }
  return map[status] || 'info'
}

function goBack() { router.push('/contract') }

async function loadDetail() {
  loading.value = true
  try {
    const [detailRes, recordsRes] = await Promise.all([
      contractApi.getById(contractId.value),
      contractApi.getRecords(contractId.value)
    ])
    contract.value = detailRes.data
    records.value = recordsRes.data
  } finally {
    loading.value = false
  }
}

async function doSubmit() {
  try {
    await contractApi.submit(contractId.value)
    ElMessage.success('提交成功')
    loadDetail()
  } catch {}
}

function doProcess() {
  currentAction.value = 'process'
  approvalComment.value = ''
  showApproval.value = true
}

function doApprove() {
  currentAction.value = 'approve'
  approvalComment.value = ''
  showApproval.value = true
}

async function confirmApproval() {
  approving.value = true
  try {
    if (currentAction.value === 'process') {
      await contractApi.process(contractId.value, approvalComment.value)
    } else {
      await contractApi.approve(contractId.value, approvalComment.value)
    }
    ElMessage.success('操作成功')
    showApproval.value = false
    loadDetail()
  } finally {
    approving.value = false
  }
}

async function doWithdraw() {
  try {
    await ElMessageBox.confirm('确认撤回该合同？', '提示', { type: 'warning' })
    await contractApi.withdraw(contractId.value)
    ElMessage.success('已撤回')
    loadDetail()
  } catch {}
}

onMounted(loadDetail)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.hidden-field { color: #bbb; font-style: italic; }
.actions { display: flex; gap: 10px; }
</style>