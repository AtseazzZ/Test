<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import { ElTable, ElTableColumn, ElTag, ElButton, ElDialog, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElSwitch, ElMessage } from 'element-plus'
import { accountApi } from '@/api/accounts'

const showAddDialog = ref(false)
const showEditDialog = ref(false)
const selectedRow = ref(null)
// 控制加载状态
const loading = ref(false)
// 每个表单项的字段
const formData = ref({
 accountName: '',
 accountNo: '',
 bank: '',
 accountType: '',
 isDefault: false,
 status: 'active'
});
// 页面展示的账户列表
const accountList = ref([])
onMounted(()=>{
  fetchAccounts()
})
// 获取账户列表数据
const fetchAccounts = async () => {
 loading.value = true
 try {
   const response = await accountApi.list()
   accountList.value = response;
 } catch (e) {
  // 错误已在 request 拦截器中处理
 } finally {
   loading.value = false;
 }
};
const getAccountTypeText = (type) => {
 const map = { basic: '基本户', normal: '一般户' };
 return map[type] || type;
};
const getAccountTypeTag = (type) => {
 const map = { basic: 'success', normal: 'info' };
 return map[type] || 'default';
};
const getStatusText = (status) => {
 const map = { active: '启用', inactive: '停用' };
 return map[status] || status;
};
const getStatusTag = (status) => {
 const map = { active: 'success', inactive: 'danger' };
 return map[status] || 'default';
};
const resetForm = () => {
 formData.value = {
 accountName: '',
 accountNo: '',
 bank: '',
 accountType: '',
 isDefault: false,
 status: 'active'
 };
};
const handleAdd = () => {
 resetForm();
 showAddDialog.value = true;
};
const confirmAdd = async() => {
 if (!formData.value.accountName || !formData.value.accountNo || !formData.value.bank) {
 ElMessage.warning('请填写必填项')
 return
 }
 try{
  // 1.组装要发送的数据
  const payload = { ...formData.value }
  // 2.调用接口
  await accountApi.add(payload)
  // 3.根据返回结果判断是否成功 拦截器已经判断了
  // 没有捕获错误，说明新增成功
  // 4.更新本地数据
  await fetchAccounts()
  ElMessage.success('账户新增成功')
 }catch (e) {
  // 错误已在 request 拦截器中处理
 } finally {
  showAddDialog.value = false
}
}
const handleEdit = (row) => {
 selectedRow.value = row;
 formData.value = { ...row };
 showEditDialog.value = true;
};
const confirmEdit = async() => {
  // 1.基础表单校验
 if (!formData.value.accountName || !formData.value.accountNo || !formData.value.bank) {
 ElMessage.warning('请填写必填项');
 return
 }
 try {
  // 2.组装要发送的数据
  const payload = { ...formData.value }
  // 3.调用接口
  await accountApi.update(selectedRow.value.id, payload)
  // 4.根据返回结果判断是否成功 拦截器已经判断了
  // 没有捕获错误，说明更新成功
  // 5.更新本地数据
  await fetchAccounts()
  ElMessage.success('账户更新成功')
 }catch (e) {
  // 错误已在 request 拦截器中处理
 } finally {
  showEditDialog.value = false
 }
}

const handleDelete = async(row) => {
 if (row.isDefault) {
  ElMessage.warning('默认账户不能删除');
  return;
 }
 try{
  await accountApi.remove(row.id)
  await fetchAccounts()
  ElMessage.success('账户删除成功')
 } catch (e) {
  // 错误已在 request 拦截器中处理
 }
}
const handleToggleDefault = (row) => {
 if (row.isDefault)
 return;
 accountList.value.forEach(item => item.isDefault = false);
 row.isDefault = true;
 ElMessage.success('已设为默认账户');
}
</script>

<template>
  <div class="accounts-page">
    <div class="page-header">
      <h1>账户管理</h1>
      <p>维护公司银行账户信息</p>
    </div>
    
    <div class="toolbar">
      <ElButton type="primary" @click="handleAdd">
        <ElIcon color="#ffffff">
          <Plus />
        </ElIcon>
        新增账户
      </ElButton>
    </div>
    
    <ElTable :data="accountList" v-loading="loading" border class="main-table">
      <ElTableColumn prop="accountName" label="账户名称" />
      <ElTableColumn prop="accountNo" label="账号">
        <template #default="scope">
          {{ scope.row.accountNo.slice(0, 4) }}****{{ scope.row.accountNo.slice(-4) }}
        </template>
      </ElTableColumn>
      <ElTableColumn prop="bank" label="开户行" />
      <ElTableColumn prop="accountType" label="账户类型">
        <template #default="scope">
          <ElTag :type="getAccountTypeTag(scope.row.accountType)">
            {{ getAccountTypeText(scope.row.accountType) }}
          </ElTag>
        </template>
      </ElTableColumn>
      <ElTableColumn prop="isDefault" label="默认使用">
        <template #default="scope">
          <div class="default-badge" @click="handleToggleDefault(scope.row)">
            <Check v-if="scope.row.isDefault" />
            <Close v-else />
          </div>
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
          <ElButton size="small" @click="handleEdit(scope.row)">
            <ElIcon color="#409eff">
              <Edit />
            </ElIcon>
            编辑
          </ElButton>
          <ElButton
            size="small"
            type="danger"
            @click="handleDelete(scope.row)"
            :disabled="scope.row.isDefault"
          >
            <ElIcon color="#ffffff">
              <Delete />
            </ElIcon>
            删除
          </ElButton>
        </template>
      </ElTableColumn>
    </ElTable>
    
    <ElDialog title="新增账户" v-model="showAddDialog">
      <ElForm :model="formData" label-width="120px">
        <ElFormItem label="账户名称" required>
          <ElInput v-model="formData.accountName" placeholder="请输入账户名称" />
        </ElFormItem>
        <ElFormItem label="账号" required>
          <ElInput v-model="formData.accountNo" placeholder="请输入银行账号" />
        </ElFormItem>
        <ElFormItem label="开户行" required>
          <ElInput v-model="formData.bank" placeholder="请输入开户银行" />
        </ElFormItem>
        <ElFormItem label="账户类型">
          <ElSelect v-model="formData.accountType" placeholder="请选择账户类型">
            <ElOption label="基本户" value="basic" />
            <ElOption label="一般户" value="normal" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="默认使用">
          <ElSwitch v-model="formData.isDefault" />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSelect v-model="formData.status">
            <ElOption label="启用" value="active" />
            <ElOption label="停用" value="inactive" />
          </ElSelect>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showAddDialog = false">取消</ElButton>
        <ElButton type="primary" @click="confirmAdd">确认新增</ElButton>
      </template>
    </ElDialog>
    
    <ElDialog title="编辑账户" v-model="showEditDialog">
      <ElForm :model="formData" label-width="120px">
        <ElFormItem label="账户名称" required>
          <ElInput v-model="formData.accountName" placeholder="请输入账户名称" />
        </ElFormItem>
        <ElFormItem label="账号" required>
          <ElInput v-model="formData.accountNo" placeholder="请输入银行账号" />
        </ElFormItem>
        <ElFormItem label="开户行" required>
          <ElInput v-model="formData.bank" placeholder="请输入开户银行" />
        </ElFormItem>
        <ElFormItem label="账户类型">
          <ElSelect v-model="formData.accountType" placeholder="请选择账户类型">
            <ElOption label="基本户" value="basic" />
            <ElOption label="一般户" value="normal" />
          </ElSelect>
        </ElFormItem>
        <ElFormItem label="默认使用">
          <ElSwitch v-model="formData.isDefault" />
        </ElFormItem>
        <ElFormItem label="状态">
          <ElSelect v-model="formData.status">
            <ElOption label="启用" value="active" />
            <ElOption label="停用" value="inactive" />
          </ElSelect>
        </ElFormItem>
      </ElForm>
      <template #footer>
        <ElButton @click="showEditDialog = false">取消</ElButton>
        <ElButton type="primary" @click="confirmEdit">确认修改</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<style lang="scss" scoped>
.accounts-page {
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

.main-table {
  background: white;
  border-radius: 12px;
}

.default-badge {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  
  &:hover {
    background: rgba(59, 130, 246, 0.1);
  }
  
  :deep(.el-icon-check) {
    color: #10b981;
  }
  
  :deep(.el-icon-x) {
    color: #9ca3af;
  }
}
</style>
