<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">梭哈酒馆 管理系统</h2>
      <p class="login-subtitle">All In Tavern</p>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <p>默认账号：admin</p>
        <p>默认密码：123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: 'admin',
  password: '123456'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const loginFormRef = ref(null)
const loading = ref(false)

const handleLogin = () => {
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const res = await login(loginForm.value)
        userStore.setToken(res.data.token)
        userStore.setUserInfo(res.data.adminInfo)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-image: url('https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=1920&q=80');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 0;
}

.login-box {
  width: 400px;
  padding: 40px;
  background: rgba(20, 20, 20, 0.92);
  border: 1px solid rgba(232, 197, 71, 0.35);
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.6);
  position: relative;
  z-index: 1;
}

.login-title {
  text-align: center;
  margin-bottom: 4px;
  color: #e8c547;
  font-size: 24px;
  letter-spacing: 2px;
}

.login-subtitle {
  text-align: center;
  margin-bottom: 30px;
  color: #8a7a3a;
  font-size: 13px;
  letter-spacing: 4px;
}

.tips {
  margin-top: 20px;
  text-align: center;
  color: #888;
  font-size: 12px;
}

.tips p {
  margin: 5px 0;
}
</style>

