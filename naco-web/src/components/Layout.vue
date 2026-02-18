<template>
  <el-container class="layout-root">
    <el-header>
      <div class="header-content">
        <h2>Naco Web 管理平台</h2>
        <div class="user-info">
          <span v-if="user">
            {{ user.username || '用户' }}
            <span v-if="user.role" class="role-tag">（{{ user.role === 'admin' ? '管理员' : '普通用户' }}）</span>
          </span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </div>
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/dashboard">
            <el-icon><House /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item v-if="isAdmin" index="/users">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/merchants">
            <el-icon><Shop /></el-icon>
            <span>商家管理</span>
          </el-menu-item>
          <el-menu-item index="/orders">
            <el-icon><Document /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-sub-menu index="/tools">
            <template #title>
              <el-icon><Tools /></el-icon>
              <span>辅助工具</span>
            </template>
            <el-menu-item index="/tools/json">
              <el-icon><Files /></el-icon>
              <span>JSON转换</span>
            </el-menu-item>
            <el-menu-item index="/tools/regex">
              <el-icon><Document /></el-icon>
              <span>正则匹配</span>
            </el-menu-item>
            <el-menu-item index="/tools/timestamp">
              <el-icon><Clock /></el-icon>
              <span>时间戳转换</span>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getUser, clearToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'
import { House, User, Shop, Document, Tools, Files, Clock } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const user = computed(() => getUser())

const isAdmin = computed(() => {
  return user.value && user.value.role === 'admin'
})

const activeMenu = computed(() => {
  return route.path
})

const handleLogout = () => {
  clearToken()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.el-header {
  background-color: #409eff;
  color: #fff;
  line-height: 60px;
  padding: 0 20px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h2 {
  margin: 0;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role-tag {
  font-size: 12px;
  margin-left: 4px;
  opacity: 0.85;
}

.el-aside {
  background-color: #545c64;
}

.el-main {
  background-color: #f5f5f5;
  padding: 20px;
}

.layout-root {
  min-height: 100vh;
}
</style>
