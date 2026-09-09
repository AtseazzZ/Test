<script setup>
import { ref, computed } from 'vue'
import {
  Search,
  Download,
  Document,
  Check,
  Edit,
  View,
  Filter
} from '@element-plus/icons-vue'
import {
  ElTable,
  ElTableColumn,
  ElTag,
  ElButton,
  ElInput,
  ElSelect,
  ElOption,
  ElDialog,
  ElForm,
  ElFormItem,
  ElDatePicker,
  ElMessage
} from 'element-plus'

const searchForm = ref({
  customerName: '',
  company: '',
  month: '',
  waybillNo: '',
  status: ''
})

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待确认', value: 'pending' },
  { label: '已确认', value: 'confirmed' },
  { label: '已开票', value: 'invoiced' },
  { label: '已完结', value: 'completed' }
]

const receivableList = ref([
  {
    id: 1,
    waybillNo: 'WB20240115001',
    customer: '上海XX物流有限公司',
    factory: '上海嘉定工厂',
    goods: '电子产品',
    tons: 25.5,
    freight: 12500,
    deduction: 500,
    demurrage: 0,
    netAmount: 12000,
    paymentTerm: 30,
    status: 'pending',
    businessStatus: 'completed'
  },
  {
    id: 2,
    waybillNo: 'WB20240115002',
    customer: '北京XX贸易有限公司',
    factory: '北京顺义工厂',
    goods: '服装面料',
    tons: 18.2,
    freight: 8600,
    deduction: 200,
    demurrage: 150,
    netAmount: 8550,
    paymentTerm: 45,
    status: 'confirmed',
    businessStatus: 'completed'
  },
  {
    id: 3,
    waybillNo: 'WB20240114003',
    customer: '广州XX制造有限公司',
    factory: '广州黄埔工厂',
    goods: '机械设备',
    tons: 32.8,
    freight: 15800,
    deduction: 0,
    demurrage: 0,
    netAmount: 15800,
    paymentTerm: 30,
    status: 'invoiced',
    businessStatus: 'completed'
  },
  {
    id: 4,
    waybillNo: 'WB20240114004',
    customer: '深圳XX电子有限公司',
    factory: '深圳南山工厂',
    goods: '电子元器件',
    tons: 12.5,
    freight: 6200,
    deduction: 100,
    demurrage: 0,
    netAmount: 6100,
    paymentTerm: 60,
    status: 'completed',
    businessStatus: 'completed'
  },
  {
    id: 5,
    waybillNo: 'WB20240113005',
    customer: '杭州XX科技有限公司',
    factory: '杭州滨江工厂',
    goods: '医疗器械',
    tons: 19.6,
    freight: 9800,
    deduction: 300,
    demurrage: 80,
    netAmount: 9580,
    paymentTerm: 30,
    status: 'pending',
    businessStatus: 'completed'
  }
])

const showGenerateDialog = ref(false)
const selectedRow = ref(null)

const filteredList = computed(() => {
  return receivableList.value.filter(item => {
    if (searchForm.value.customerName && !item.customer.includes(searchForm.value.customerName)) return false
    if (searchForm.value.company && !item.factory.includes(searchForm.value.company)) return false
    if (searchForm.value.waybillNo && !item.waybillNo.includes(searchForm.value.waybillNo)) return false
    if (searchForm.value.status && item.status !== searchForm.value.status) return false
    return true
  })
})

const getStatusText = (status) => {
  const map = {
    pending: '待确认',
    confirmed: '已确认',
    invoiced: '已开票',
    completed: '已完结'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    pending: 'warning',
    confirmed: 'primary',
    invoiced: 'info',
    completed: 'success'
  }
  return map[status] || 'default'
}

const handleGenerateBill = (row) => {
  if (row.businessStatus !== 'completed') {
    ElMessage.warning('只有业务已完结的运单才能生成对账单')
    return
  }
  selectedRow.value = row
  showGenerateDialog.value = true
}

const confirmGenerate = () => {
  ElMessage.success('对账单生成成功')
  showGenerateDialog.value = false
  selectedRow.value = null
}

const handleExport = () => {
  ElMessage.success('对账单导出成功')
}

const handleConfirm = (row) => {
  ElMessage.success('财务确认成功')
}

const handleInvoice = (row) => {
  ElMessage.success('开票申请已发起')
}

const handleViewVoucher = (row) => {
  ElMessage.info('查看凭证功能')
}
</script>

<template>
  <div class="receivable-page">
    <div class="page-header">
      <h1>应收账款管理</h1>
      <p>只有运单"业务已完结/已签收"才能显示在此页并生成应收</p>
    </div>
    
    <div class="search-bar">
      <div class="search-row">
        <ElInput
          v-model="searchForm.customerName"
          placeholder="客户名称"
          prefix-icon="Search"
          class="search-input"
        />
        <ElInput
          v-model="searchForm.company"
          placeholder="公司主体"
          prefix-icon="Building"
          class="search-input"
        />
        <ElDatePicker
          v-model="searchForm.month"
          type="month"
          placeholder="对账月份"
          class="search-input"
        />
        <ElInput
          v-model="searchForm.waybillNo"
          placeholder="运单号"
          prefix-icon="Document"
          class="search-input"
        />
        <ElSelect
          v-model="searchForm.status"
          placeholder="状态"
          class="search-input"
        >
          <ElOption v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </ElSelect>
        <!-- 查询按钮需要加事件，点击之后才进行查询 -->
        <ElButton type="primary" >
          <ElIcon color="#ffffff">
            <Search />
          </ElIcon>
          查询
        </ElButton>
        <!-- 重置按钮需要加事件，点击之后清空搜索框内容，刷新页面 -->
        <ElButton>
          <ElIcon color="#409eff">
            <Filter />
          </ElIcon>
          重置
        </ElButton>
      </div>
    </div>
    
    <div class="toolbar">
      <ElButton type="primary" @click="handleExport">
        <ElIcon color="#ffffff">
          <Document />
        </ElIcon>
        生成对账单
      </ElButton>
      <ElButton @click="handleExport">
        <ElIcon color="#409eff">
          <Download />
        </ElIcon>
        导出对账单
      </ElButton>
    </div>
    
    <ElTable :data="filteredList" border class="main-table">
      <ElTableColumn prop="waybillNo" label="运单号" />
      <ElTableColumn prop="customer" label="客户" />
      <ElTableColumn prop="factory" label="收货工厂" />
      <ElTableColumn prop="goods" label="货物" />
      <ElTableColumn prop="tons" label="吨数" />
      <ElTableColumn prop="freight" label="应收运费">
          <template #default="scope">¥ {{ scope.row.freight }}</template>
        </ElTableColumn>
        <ElTableColumn prop="deduction" label="扣款">
          <template #default="scope">¥ {{ scope.row.deduction }}</template>
        </ElTableColumn>
        <ElTableColumn prop="demurrage" label="滞期费">
          <template #default="scope">¥ {{ scope.row.demurrage }}</template>
        </ElTableColumn>
        <ElTableColumn prop="netAmount" label="应收净额">
          <template #default="scope">¥ {{ scope.row.netAmount }}</template>
        </ElTableColumn>
      <ElTableColumn prop="paymentTerm" label="账期(天)" />
      <ElTableColumn prop="status" label="状态">
        <template #default="scope">
          <ElTag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn label="操作">
        <template #default="scope">
          <ElButton
            size="small"
            @click="handleGenerateBill(scope.row)"
            :disabled="scope.row.businessStatus !== 'completed'"
          >
            <ElIcon color="#409eff">
              <Document />
            </ElIcon>
            生成对账单
          </ElButton>
          <ElButton
            size="small"
            @click="handleConfirm(scope.row)"
            :disabled="scope.row.status === 'completed'"
          >
            <ElIcon color="#409eff">
              <Check />
            </ElIcon>
            财务确认
          </ElButton>
          <ElButton
            size="small"
            @click="handleInvoice(scope.row)"
            :disabled="scope.row.status !== 'confirmed'"
          >
            <ElIcon color="#409eff">
              <Edit />
            </ElIcon>
            发起开票
          </ElButton>
          <ElButton size="small" @click="handleViewVoucher(scope.row)">
            <ElIcon color="#409eff">
              <View />
            </ElIcon>
            查看凭证
          </ElButton>
        </template>
      </ElTableColumn>
    </ElTable>
    
    <ElDialog title="生成对账单" v-model="showGenerateDialog">
      <ElForm :model="selectedRow" label-width="120px">
        <ElFormItem label="运单号">
          <span>{{ selectedRow?.waybillNo }}</span>
        </ElFormItem>
        <ElFormItem label="客户">
          <span>{{ selectedRow?.customer }}</span>
        </ElFormItem>
        <ElFormItem label="应收净额">
          <span>¥ {{ selectedRow?.netAmount }}</span>
        </ElFormItem>
        <ElFormItem label="对账月份">
          <ElDatePicker v-model="searchForm.month" type="month" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showGenerateDialog = false">取消</ElButton>
        <ElButton type="primary" @click="confirmGenerate">确认生成</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.receivable-page {
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
  color: #f59e0b;
  font-size: 14px;
}

.search-bar {
  background: white;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.search-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.search-input {
  width: 200px;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.main-table {
  background: white;
  border-radius: 12px;
}
</style>
