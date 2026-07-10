<template>
  <div class="login-container">
    <div class="login-card">
      <h2>企业级权限管理系统</h2>
      <p class="subtitle">合同审批流 Demo</p>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-select v-model="form.username" placeholder="选择用户" style="width: 100%">
            <el-option label="U1 - 张三 (申请人)" value="u1" />
            <el-option label="U2 - 李四 (合同管理员)" value="u2" />
            <el-option label="U3 - 王五 (部门经理)" value="u3" />
            <el-option label="U4 - 赵六 (领导)" value="u4" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="123456" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="login" :loading="loading" style="width: 100%">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="hint">
        <p>Demo 密码统一为 <b>123456</b></p>
        <p>下拉选择不同用户模拟不同角色</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../api'
import { usePermissionStore } from '../stores/permission'

const router = useRouter()
const permStore = usePermissionStore()
const loading = ref(false)
const form = ref({ username: 'u1', password: '123456' })

async function login() {
  loading.value = true
  try {
    const res = await authApi.login({ username: form.value.username, password: form.value.password })
    permStore.setLoginInfo(res.data)
    router.push('/contract')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  width: 420px;
}
.login-card h2 {
  text-align: center;
  margin-bottom: 4px;
  color: #333;
}
.subtitle {
  text-align: center;
  color: #999;
  margin-bottom: 30px;
}
.hint {
  margin-top: 20px;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
  font-size: 13px;
  color: #666;
  text-align: center;
}
.hint p { margin: 4px 0; }
</style>