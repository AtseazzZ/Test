<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, Download } from '@element-plus/icons-vue'
import { payableApi } from '@/api/payAble'
import {
  ElTable,
  ElTableColumn,
  ElTag,
  ElButton,
  ElInput,
  ElSelect,
  ElOption,
  ElMessage
} from 'element-plus'

// 加载状态
const loading = ref(false)
// 筛选条件
const searchForm = ref({
  supplierType: '',
  supplierName: '',
  period: '',
  waybillNo: '',
  status: ''
})
// 重置按钮逻辑
const handleReset = () => {
  searchForm.value = {
    supplierType: '',
    supplierName: '',
    period: '',
    waybillNo: '',
    status: ''
  }
  fetchPayablesList()
}
const supplierTypes = [
  { label: '全部', value: '' },
  { label: '船东', value: 'ship' },
  { label: '码头', value: 'port' },
  { label: '车队', value: 'truck' },
  { label: '铁路', value: 'railway' }
]

const expenseTypes = [
  { label: '船运费', value: 'ship_freight' },
  { label: '汽运费', value: 'truck_freight' },
  { label: '铁路费', value: 'railway_freight' },
  { label: '装卸费', value: 'handling' }
]

const payableList = ref([])

const getSupplierTypeText = (type) => {
  const map = { ship: '船东', port: '码头', truck: '车队', railway: '铁路' }
  return map[type] || type
}

const getExpenseTypeText = (type) => {
  const map = {
    ship_freight: '船运费',
    truck_freight: '汽运费',
    railway_freight: '铁路费',
    handling: '装卸费'
  }
  return map[type] || type
}

const getStatusText = (status) => {
  const map = { pending: '待确认', confirmed: '已确认', completed: '已完结' }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = { pending: 'warning', confirmed: 'primary', completed: 'success' }
  return map[status] || 'default'
}

const getTaxRate = (expenseType, supplierType) => {
  if (supplierType === 'railway') {
    if (expenseType === 'railway_freight') return '9%'
    if (expenseType === 'handling') return '6%'
    return '0%'
  }
  return ''
}

// const filteredList = computed(() => {
//   return payableList.value.filter(item => {
//     if (searchForm.value.supplierType && item.supplierType !== searchForm.value.supplierType) return false
//     if (searchForm.value.supplierName && !item.supplier.includes(searchForm.value.supplierName)) return false
//     if (searchForm.value.waybillNo && !item.waybillNo.includes(searchForm.value.waybillNo)) return false
//     if (searchForm.value.status && item.status !== searchForm.value.status) return false
//     return true
//   })
// })

const fetchPayablesList = async () => {
  loading.value = true
  try {
    const response = await payableApi.list(searchForm.value)
    payableList.value = response.content || []
  } catch (e) {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
const handleSearch = (searchForm) => {
  const params = {
    supplierType: searchForm.supplierType,
    keyword: searchForm.supplierName || searchForm.waybillNo,
    status: searchForm.status
  }
  fetchPayablesList(params)
}
const handleExport = () => {
  ElMessage.success('导出成功')
}

onMounted(() => {
  fetchPayablesList()
})
</script>

<template>
  <div class="payable-page">
    <div class="page-header">
      <h1>应付账款管理</h1>
      <p>管理船东、码头、车队、铁路等应付成本</p>
    </div>
    
    <div class="search-bar">
      <div class="search-row">
        <ElSelect v-model="searchForm.supplierType" placeholder="供应商类型" class="search-input">
          <ElOption v-for="opt in supplierTypes" :key="opt.value" :label="opt.label" :value="opt.value" />
        </ElSelect>
        <ElInput
          v-model="searchForm.supplierName"
          placeholder="供应商名称"
          prefix-icon="Search"
          class="search-input"
        />
        <ElInput
          v-model="searchForm.waybillNo"
          placeholder="运单号"
          class="search-input"
        />
        <ElSelect v-model="searchForm.status" placeholder="状态" class="search-input">
          <ElOption label="全部" value="" />
          <ElOption label="待确认" value="pending" />
          <ElOption label="已确认" value="confirmed" />
          <ElOption label="已完结" value="completed" />
        </ElSelect>
        <ElButton type="primary" icon="Search" @click="handleSearch(searchForm)">查询</ElButton>
        <ElButton type="default" @click="handleReset">重置</ElButton>
      </div>
    </div>
    
    <div class="toolbar">
      <ElButton @click="handleExport">
        <ElIcon color="#409eff">
          <Download />
        </ElIcon>
        导出应付明细
      </ElButton>
    </div>
    
    <div class="tax-notice">
      <strong>税率说明：</strong>
      <span>铁路运费9%、装卸6%、铁路建设费0% | 船运费区分【实际运费】+【平台管理费】</span>
    </div>
    
    <ElTable :data="payableList" border class="main-table">
      <ElTableColumn prop="waybillNo" label="运单号" />
      <ElTableColumn prop="supplier" label="供应商" />
      <ElTableColumn prop="expenseType" label="费用类型">
        <template #default="scope">
          <ElTag>{{ getExpenseTypeText(scope.row.expenseType) }}</ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="supplierType" label="供应商类型">
        <template #default="scope">
          <ElTag type="info">{{ getSupplierTypeText(scope.row.supplierType) }}</ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="quantity" label="计费吨/箱">
        <template #default="scope">
          {{ scope.row.quantity }} {{ scope.row.unit }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="unitPrice" label="单价">
          <template #default="scope">¥ {{ scope.row.unitPrice }}</template>
        </ElTableColumn>
        <ElTableColumn prop="amount" label="金额">
          <template #default="scope">¥ {{ scope.row.amount }}</template>
        </ElTableColumn>
        <ElTableColumn prop="taxRate" label="税率">
        <template #default="scope">
          {{ scope.row.taxRate }}%
          <span v-if="getTaxRate(scope.row.expenseType, scope.row.supplierType)" class="tax-hint">
            ({{ getTaxRate(scope.row.expenseType, scope.row.supplierType) }})
          </span>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="totalAmount" label="含税金额">
          <template #default="scope">¥ {{ scope.row.totalAmount }}</template>
        </ElTableColumn>
      <!-- scope.row表示组件中的一行数据，在模板内获取scope-->
      <ElTableColumn label="实际运费">
        <template #default="scope">
          <span v-if="scope.row.supplierType === 'ship'">
            ¥ {{ scope.row.actualFreight }}
          </span>
        </template>
      </ElTableColumn>
      <ElTableColumn label="平台管理费">
        <template #default="scope">
          <span v-if="scope.row.supplierType === 'ship'">
            ¥ {{ scope.row.platformFee }}
          </span>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="status" label="状态">
        <template #default="scope">
          <ElTag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn label="操作">
        <template #default="scope">
          <ElButton size="small" type="primary">确认应付</ElButton>
          <ElButton size="small">查看详情</ElButton>
        </template>
      </ElTableColumn>
    </ElTable>
  </div>
</template>

<style lang="scss" scoped>
.payable-page {
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

.tax-notice {
  background: #fffbeb;
  border: 1px solid #fef3c7;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #92400e;
}

.main-table {
  background: white;
  border-radius: 12px;
}

.tax-hint {
  color: #f59e0b;
  font-size: 12px;
}
</style>
