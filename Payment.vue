<script setup>import { ref } from 'vue';
import { Download, WarnTriangleFilled  } from '@element-plus/icons-vue';
import { ElTable, ElTableColumn, ElTag, ElButton, ElTabs, ElTabPane, ElDialog, ElForm, ElFormItem, ElInput, ElMessage } from 'element-plus';
const activeTab = ref('pending');
const showDelayDialog = ref(false);
const showWriteoffDialog = ref(false);
const selectedRow = ref(null);
const pendingPayments = ref([
 {
 id: 1,
 applyNo: 'P20240115001',
 supplier: '中远海运集团',
 expenseType: '船运费',
 amount: 12507.75,
 account: '工商银行基本户',
 approveStatus: 'approved',
 paymentStatus: 'pending',
 hasLoss: false,
 lossAmount: 0
 },
 {
 id: 2,
 applyNo: 'P20240115002',
 supplier: '上海XX车队',
 expenseType: '汽运费',
 amount: 6348.16,
 account: '建设银行一般户',
 approveStatus: 'approved',
 paymentStatus: 'pending',
 hasLoss: true,
 lossAmount: 200
 },
 {
 id: 3,
 applyNo: 'P20240114003',
 supplier: '中铁快运',
 expenseType: '铁路费',
 amount: 6435.36,
 account: '工商银行基本户',
 approveStatus: 'pending',
 paymentStatus: 'pending',
 hasLoss: false,
 lossAmount: 0
 }
]);
const paidPayments = ref([
 {
 id: 1,
 applyNo: 'P20240113001',
 supplier: '广州XX码头',
 expenseType: '装卸费',
 amount: 1060,
 account: '工商银行基本户',
 approveStatus: 'approved',
 paymentStatus: 'paid',
 writeoffStatus: 'done'
 },
 {
 id: 2,
 applyNo: 'P20240112002',
 supplier: '上海港务局',
 expenseType: '装卸费',
 amount: 1929.2,
 account: '建设银行一般户',
 approveStatus: 'approved',
 paymentStatus: 'paid',
 writeoffStatus: 'pending'
 }
]);
const prepayments = ref([
 {
 id: 1,
 applyNo: 'PP20240115001',
 supplier: '天津XX船务',
 expenseType: '预付款',
 amount: 50000,
 account: '工商银行基本户',
 approveStatus: 'approved',
 paymentStatus: 'paid'
 },
 {
 id: 2,
 applyNo: 'PP20240114002',
 supplier: '青岛XX货代',
 expenseType: '预付款',
 amount: 30000,
 account: '建设银行一般户',
 approveStatus: 'pending',
 paymentStatus: 'pending'
 }
]);
const delayedPayments = ref([
 {
 id: 1,
 applyNo: 'P20240110001',
 supplier: '宁波XX车队',
 expenseType: '汽运费',
 amount: 8500,
 account: '工商银行基本户',
 delayReason: '货物损耗争议',
 delayDays: 15,
 canRecover: true
 },
 {
 id: 2,
 applyNo: 'P20240108002',
 supplier: '厦门XX码头',
 expenseType: '装卸费',
 amount: 2300,
 account: '建设银行一般户',
 delayReason: '发票问题',
 delayDays: 8,
 canRecover: false
 }
]);
const getApproveStatusText = (status) => {
 const map = { pending: '待审批', approved: '已批准', rejected: '已驳回' };
 return map[status] || status;
};
const getApproveStatusType = (status) => {
 const map = { pending: 'warning', approved: 'success', rejected: 'danger' };
 return map[status] || 'default';
};
const getPaymentStatusText = (status) => {
 const map = { pending: '待付款', paid: '已付款', delayed: '延迟付款' };
 return map[status] || status;
};
const handleDelayPayment = (row) => {
 selectedRow.value = row;
 showDelayDialog.value = true;
};
const confirmDelay = () => {
 ElMessage.success('已设置延迟付款');
 showDelayDialog.value = false;
 selectedRow.value = null;
};
const handleRecoverPayment = (row) => {
 ElMessage.success('已恢复付款');
};
const handleWriteoff = (row) => {
 selectedRow.value = row;
 showWriteoffDialog.value = true;
};
const confirmWriteoff = () => {
 ElMessage.success('销账成功');
 showWriteoffDialog.value = false;
 selectedRow.value = null;
};
const handleExportDaily = () => {
 ElMessage.success('当日日记账导出成功');
};
</script>

<template>
  <div class="payment-page">
    <div class="page-header">
      <h1>付款管理</h1>
      <p>付款申请、延迟付款、销账管理</p>
    </div>
    
    <div class="toolbar">
      <ElButton @click="handleExportDaily">
        <ElIcon color="409eff">
          <Download />
        </ElIcon>
        导出当日日记账
      </ElButton>
      <ElButton type="primary">新建付款申请</ElButton>
    </div>
    
    <ElTabs v-model="activeTab" type="card" class="tab-container">
      <ElTabPane label="待付款" name="pending">
        <ElTable :data="pendingPayments" border class="main-table">
          <ElTableColumn prop="applyNo" label="申请单号" />
          <ElTableColumn prop="supplier" label="供应商" />
          <ElTableColumn prop="expenseType" label="费用类型">
            <template #default="scope">
              <ElTag>{{ scope.row.expenseType }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="account" label="付款账户" />
          <ElTableColumn prop="approveStatus" label="审批状态">
            <template #default="scope">
              <ElTag :type="getApproveStatusType(scope.row.approveStatus)">
                {{ getApproveStatusText(scope.row.approveStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="paymentStatus" label="付款状态">
            <template #default="scope">
              <ElTag type="warning">{{ getPaymentStatusText(scope.row.paymentStatus) }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="hasLoss" label="损耗异常">
            <template #default="scope">
              <div v-if="scope.row.hasLoss" class="loss-warning">
                <WarnTriangleFilled  />
                <span>损耗 ¥{{ scope.row.lossAmount }}</span>
              </div>
              <span v-else class="no-loss">无异常</span>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="primary"
                :disabled="scope.row.approveStatus !== 'approved'"
              >
                出纳付款
              </ElButton>
              <ElButton
                size="small"
                type="warning"
                @click="handleDelayPayment(scope.row)"
                v-if="scope.row.hasLoss"
              >
                延迟付款
              </ElButton>
              <ElButton size="small">查看详情</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
      
      <ElTabPane label="已付款" name="paid">
        <ElTable :data="paidPayments" border class="main-table">
          <ElTableColumn prop="applyNo" label="申请单号" />
          <ElTableColumn prop="supplier" label="供应商" />
          <ElTableColumn prop="expenseType" label="费用类型" />
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="account" label="付款账户" />
          <ElTableColumn prop="paymentStatus" label="付款状态">
            <template #default="scope">
              <ElTag type="success">{{ getPaymentStatusText(scope.row.paymentStatus) }}</ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="writeoffStatus" label="销账状态">
            <template #default="scope">
              <ElTag :type="scope.row.writeoffStatus === 'done' ? 'success' : 'warning'">
                {{ scope.row.writeoffStatus === 'done' ? '已销账' : '待销账' }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="primary"
                @click="handleWriteoff(scope.row)"
                v-if="scope.row.writeoffStatus === 'pending'"
              >
                销账
              </ElButton>
              <ElButton size="small">查看凭证</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
      
      <ElTabPane label="预付款" name="prepayment">
        <ElTable :data="prepayments" border class="main-table">
          <ElTableColumn prop="applyNo" label="申请单号" />
          <ElTableColumn prop="supplier" label="供应商" />
          <ElTableColumn prop="expenseType" label="费用类型" />
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="account" label="付款账户" />
          <ElTableColumn prop="approveStatus" label="审批状态">
            <template #default="scope">
              <ElTag :type="getApproveStatusType(scope.row.approveStatus)">
                {{ getApproveStatusText(scope.row.approveStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn prop="paymentStatus" label="付款状态">
            <template #default="scope">
              <ElTag :type="scope.row.paymentStatus === 'paid' ? 'success' : 'warning'">
                {{ getPaymentStatusText(scope.row.paymentStatus) }}
              </ElTag>
            </template>
          </ElTableColumn>
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="primary"
                :disabled="scope.row.approveStatus !== 'approved'"
              >
                付款
              </ElButton>
              <ElButton size="small">查看详情</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
      
      <ElTabPane label="延迟付款" name="delayed">
        <ElTable :data="delayedPayments" border class="main-table">
          <ElTableColumn prop="applyNo" label="申请单号" />
          <ElTableColumn prop="supplier" label="供应商" />
          <ElTableColumn prop="expenseType" label="费用类型" />
          <ElTableColumn prop="amount" label="金额">
            <template #default="scope">¥ {{ scope.row.amount }}</template>
          </ElTableColumn>
          <ElTableColumn prop="delayReason" label="延迟原因" />
          <ElTableColumn prop="delayDays" label="延迟天数" />
          <ElTableColumn label="操作">
            <template #default="scope">
              <ElButton
                size="small"
                type="success"
                @click="handleRecoverPayment(scope.row)"
                v-if="scope.row.canRecover"
              >
                恢复付款
              </ElButton>
              <ElButton size="small" disabled v-else>待处理</ElButton>
            </template>
          </ElTableColumn>
        </ElTable>
      </ElTabPane>
    </ElTabs>
    
    <ElDialog title="延迟付款" v-model="showDelayDialog">
      <ElForm :model="selectedRow" label-width="100px">
        <ElFormItem label="申请单号">
          <span>{{ selectedRow?.applyNo }}</span>
        </ElFormItem>
        <ElFormItem label="供应商">
          <span>{{ selectedRow?.supplier }}</span>
        </ElFormItem>
        <ElFormItem label="延迟原因">
          <ElInput rows="3" placeholder="请输入延迟原因" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showDelayDialog = false">取消</ElButton>
        <ElButton type="warning" @click="confirmDelay">确认延迟</ElButton>
      </template>
    </ElDialog>
    
    <ElDialog title="销账" v-model="showWriteoffDialog">
      <ElForm :model="selectedRow" label-width="100px">
        <ElFormItem label="申请单号">
          <span>{{ selectedRow?.applyNo }}</span>
        </ElFormItem>
        <ElFormItem label="金额">
          <span>¥ {{ selectedRow?.amount }}</span>
        </ElFormItem>
        <ElFormItem label="销账备注">
          <ElInput placeholder="请输入销账备注（可选）" />
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showWriteoffDialog = false">取消</ElButton>
        <ElButton type="primary" @click="confirmWriteoff">确认销账</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.payment-page {
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

.loss-warning {
  width: 80px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #ef4444;
  font-size: 14px;
}

.no-loss {
  color: #10b981;
  font-size: 14px;
}
</style>
