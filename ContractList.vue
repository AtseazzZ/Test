<template>
  <div class="contract-list">
    <div class="toolbar">
      <h2>合同列表</h2>
      <el-button v-permission="'contract:create'" type="primary" @click="showCreate = true">
        <el-icon><Plus /></el-icon> 新建合同
      </el-button>
    </div>

    <el-table :data="contracts" border stripe v-loading="loading" style="width: 100%">
      <el-table-column prop="contractNo" label="合同编号" width="140" />
      <el-table-column prop="name" label="合同名称" min-width="160" />
      <el-table-column prop="customer" label="客户" width="120" />
      <el-table-column prop="amount" label="金额" width="120">
        <template #default="{ row }">
          {{ row.amount ? '¥' + row.amount.toLocaleString() : '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="归属部门" width="100" />
      <el-table-column prop="applicantName" label="申请人" width="80" />
      <el-table-column prop="statusName" label="状态" width="160">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.statusName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button v-permission="'contract:view'" size="small" @click="viewDetail(row.id)">
            查看
          </el-button>
          <el-button v-if="row.status === 'DRAFT'"
            v-permission="'contract:create'" size="small" type="success"
            @click="doSubmit(row.id)">
            提交
          </el-button>
          <el-button v-if="row.status === 'PENDING_ADMIN'"
            v-permission="'contract:process'" size="small" type="warning"
            @click="doProcess(row)">
            处理
          </el-button>
          <el-button v-if="row.status === 'PENDING_MANAGER' || row.status === 'PENDING_LEADER'"
            v-permission="'contract:approve'" size="small" type="primary"
            @click="doApprove(row)">
            审批
          </el-button>
          <el-button v-if="row.status === 'DRAFT'"
            v-permission="'contract:withdraw'" size="small" type="danger"
            @click="doWithdraw(row.id)">
            撤回
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新建合同对话框 -->
    <el-dialog v-model="showCreate" title="新建合同" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="合同编号"><el-input v-model="createForm.contractNo" /></el-form-item>
        <el-form-item label="合同名称"><el-input v-model="createForm.name" /></el-form-item>
        <el-form-item label="客户"><el-input v-model="createForm.customer" /></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="createForm.amount" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="条款明细"><el-input v-model="createForm.clauseDetail" type="textarea" /></el-form-item>
        <el-form-item label="附件"><el-input v-model="createForm.attachment" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="createForm.remark" type="textarea" /></el-form-item>
        <el-form-item label="银行账号"><el-input v-model="createForm.bankAccount" /></el-form-item>
        <el-form-item label="归属部门">
          <el-select v-model="createForm.deptId" style="width:100%">
            <el-option label="研发部" :value="1" />
            <el-option label="运营部" :value="2" />
            <el-option label="市场部" :value="3" />
            <el-option label="财务部" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="createContract" :loading="creating">创建</el-button>
      </template>
    </el-dialog>

    <!-- 审批/处理对话框 -->
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
import { useRouter } from 'vue-router'
import { contractApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const contracts = ref([])
const loading = ref(false)
const showCreate = ref(false)
const creating = ref(false)
const showApproval = ref(false)
const approving = ref(false)
const approvalComment = ref('')
const currentContract = ref(null)
const approvalAction = ref('')

const createForm = ref({
  contractNo: '',
  name: '',
  customer: '',
  amount: null,
  clauseDetail: '',
  attachment: '',
  remark: '',
  bankAccount: '',
  deptId: 1
})

function statusType(status) {
  const map = { DRAFT: 'info', PENDING_ADMIN: 'warning', PENDING_MANAGER: 'warning',
                PENDING_LEADER: 'warning', APPROVED: 'success', WITHDRAWN: 'danger', DELETED: 'danger' }
  return map[status] || 'info'
}

async function loadContracts() {
  loading.value = true
  try {
    const res = await contractApi.list()
    contracts.value = res.data
  } finally {
    loading.value = false
  }
}

function viewDetail(id) {
  router.push(`/contract/${id}`)
}

async function doSubmit(id) {
  try {
    await contractApi.submit(id)
    ElMessage.success('提交成功')
    loadContracts()
  } catch {}
}

function doProcess(row) {
  currentContract.value = row
  approvalAction.value = 'process'
  approvalComment.value = ''
  showApproval.value = true
}

function doApprove(row) {
  currentContract.value = row
  approvalAction.value = 'approve'
  approvalComment.value = ''
  showApproval.value = true
}

async function confirmApproval() {
  approving.value = true
  try {
    if (approvalAction.value === 'process') {
      await contractApi.process(currentContract.value.id, approvalComment.value)
    } else {
      await contractApi.approve(currentContract.value.id, approvalComment.value)
    }
    ElMessage.success('操作成功')
    showApproval.value = false
    loadContracts()
  } finally {
    approving.value = false
  }
}

async function doWithdraw(id) {
  try {
    await ElMessageBox.confirm('确认撤回该合同？', '提示', { type: 'warning' })
    await contractApi.withdraw(id)
    ElMessage.success('已撤回')
    loadContracts()
  } catch {}
}

async function createContract() {
  creating.value = true
  try {
    await contractApi.create(createForm.value)
    ElMessage.success('创建成功')
    showCreate.value = false
    createForm.value = { contractNo: '', name: '', customer: '', amount: null,
      clauseDetail: '', attachment: '', remark: '', bankAccount: '', deptId: 1 }
    loadContracts()
  } finally {
    creating.value = false
  }
}

onMounted(loadContracts)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.toolbar h2 { margin: 0; }
</style>