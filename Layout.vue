<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="header-left">
        <h3>企业级权限管理系统</h3>
      </div>
      <div class="header-right">
        <el-tag type="success" style="margin-right: 12px">
          {{ permStore.realName }} ({{ permStore.username.toUpperCase() }})
        </el-tag>
        <el-tag v-for="role in permStore.roles" :key="role" style="margin-right: 4px" size="small">
          {{ roleNameMap[role] || role }}
        </el-tag>
        <el-button type="danger" size="small" @click="logout" style="margin-left: 16px">退出</el-button>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px" class="aside">
        <el-menu router :default-active="route.path" class="menu">
          <el-menu-item index="/contract">
            <el-icon><Document /></el-icon>
            <span>合同列表</span>
          </el-menu-item>
          <el-menu-item v-if="permStore.hasRole('LEADER')" index="/audit">
            <el-icon><Monitor /></el-icon>
            <span>审计日志</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { usePermissionStore } from '../stores/permission'

const router = useRouter()
const route = useRoute()
const permStore = usePermissionStore()

const roleNameMap = {
  APPLICANT: '申请人',
  CONTRACT_ADMIN: '合同管理员',
  DEPT_MANAGER: '部门经理',
  LEADER: '领导'
}

function logout() {
  permStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout { height: 100vh; }
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #409eff;
  color: #fff;
  padding: 0 20px;
}
.header h3 { margin: 0; }
.header-right { display: flex; align-items: center; }
.aside { background: #f5f7fa; border-right: 1px solid #e4e7ed; }
.menu { border-right: none; }
.main { background: #f0f2f5; padding: 20px; }
</style>