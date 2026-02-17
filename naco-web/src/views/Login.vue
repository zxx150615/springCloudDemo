<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>登录</span>
        </div>
      </template>
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleLogin">
            登录
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-link type="primary" :underline="false" @click="$router.push('/register')" style="margin-left: 20px">
            还没有账号？去注册
          </el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/modules/auth'
import { setToken, setUser } from '@/utils/auth'

const router = useRouter()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        
        // 处理响应：可能是字符串或对象
        if (typeof res === 'string') {
          if (res.includes('成功')) {
            // 从响应中提取 token（如果后端返回了）
            ElMessage.success('登录成功')
            // 注意：如果后端返回的是字符串，可能需要从其他地方获取 token
            // 这里假设后端会在响应头或响应体中返回 token
            router.push('/')
          } else {
            ElMessage.error(res)
          }
        } else if (res && res.data) {
          // 标准响应格式：{ success, msg, data: { token, loginId, role } }
          if (res.data.token) {
            setToken(res.data.token)
            setUser({
              loginId: res.data.loginId,
              username: loginForm.username,
              role: res.data.role
            })
            ElMessage.success('登录成功')
            router.push('/')
          } else {
            ElMessage.error(res.msg || '登录失败')
          }
        } else {
          ElMessage.error('登录失败，请检查用户名和密码')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  loginFormRef.value?.resetFields()
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
</style>
