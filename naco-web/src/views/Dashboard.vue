<template>
  <div class="dashboard">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>欢迎使用 Naco Web 管理平台</span>
        </div>
      </template>
      <div class="welcome-content">
        <h2>欢迎回来！</h2>
        <p>这是一个基于 Spring Cloud 的微服务管理平台</p>
        <el-row :gutter="20" style="margin-top: 30px">
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-item">
                <div class="stat-value">用户管理</div>
                <div class="stat-desc">查看和管理用户信息</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-item">
                <div class="stat-value">商家管理</div>
                <div class="stat-desc">查看和管理商家信息</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-item">
                <div class="stat-value">订单管理</div>
                <div class="stat-desc">查看和管理订单信息</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <div class="stat-item">
                <div class="stat-value">系统管理</div>
                <div class="stat-desc">系统配置和监控</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
// Dashboard 页面
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.welcome-content {
  text-align: center;
}

.welcome-content h2 {
  margin-bottom: 10px;
  color: #409eff;
}

.welcome-content p {
  color: #666;
  font-size: 14px;
}

.stat-item {
  text-align: center;
  padding: 20px 0;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 10px;
}

.stat-desc {
  font-size: 14px;
  color: #999;
}
</style>
