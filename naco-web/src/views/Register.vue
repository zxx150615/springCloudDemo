<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>注册</span>
        </div>
      </template>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleRegister">
            注册
          </el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-link type="primary" :underline="false" @click="$router.push('/login')" style="margin-left: 20px">
            已有账号？去登录
          </el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/modules/auth'
import { setToken, setUser } from '@/utils/auth'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = {
          username: registerForm.username,
          password: registerForm.password
        }
        
        const res = await register(data)
        
        // 处理响应
        if (typeof res === 'string') {
          if (res.includes('成功')) {
            ElMessage.success('注册成功')
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
              username: registerForm.username,
              role: res.data.role
            })
            ElMessage.success('注册成功')
            router.push('/')
          } else {
            ElMessage.error(res.msg || '注册失败')
          }
        } else {
          ElMessage.error('注册失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const handleReset = () => {
  registerFormRef.value?.resetFields()
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-card {
  width: 500px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}

.skills-container {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}
</style>
