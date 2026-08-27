<template>
  <div class="login-container">
    <div class="ambient ambient-one"></div>
    <div class="ambient ambient-two"></div>
    <div class="login-box">
      <div class="brand-mark">A</div>
      <p class="login-kicker">ALL IN TAVERN</p>
      <h2 class="login-title">梭哈酒馆管理系统</h2>
      <p class="login-subtitle">登录运营管理中心</p>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <label class="field-label">账号</label>
          <el-input
            v-model="loginForm.username"
            placeholder="请输入管理账号"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <label class="field-label">密码</label>
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
            进入管理后台
          </el-button>
        </el-form-item>
      </el-form>
      <div class="tips">
        <span></span>
        <p>仅限授权工作人员使用</p>
        <span></span>
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
  overflow: hidden;
  background:
    linear-gradient(rgba(255, 255, 255, 0.016) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.016) 1px, transparent 1px),
    radial-gradient(circle at 50% 25%, #20201a, #0c0d0f 56%);
  background-size: 52px 52px, 52px 52px, auto;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 50% 50%, transparent 25%, rgba(0, 0, 0, 0.52) 100%);
}

.ambient {
  position: absolute;
  border-radius: 50%;
  filter: blur(2px);
  pointer-events: none;
}

.ambient-one {
  top: -240px;
  left: 50%;
  width: 560px;
  height: 560px;
  transform: translateX(-50%);
  background: radial-gradient(circle, rgba(232, 197, 71, 0.105), transparent 68%);
}

.ambient-two {
  right: -180px;
  bottom: -260px;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(196, 30, 58, 0.075), transparent 68%);
}

.login-box {
  width: 420px;
  padding: 42px 44px 34px;
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.025), transparent 48%),
    rgba(21, 22, 25, 0.94);
  border: 1px solid rgba(232, 197, 71, 0.24);
  border-radius: 18px;
  box-shadow: 0 28px 90px rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(18px);
  position: relative;
  z-index: 1;
}

.brand-mark {
  width: 58px;
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
  border: 1px solid rgba(232, 197, 71, 0.5);
  border-radius: 50%;
  background: radial-gradient(circle at 35% 25%, rgba(232, 197, 71, 0.2), rgba(232, 197, 71, 0.03));
  color: #f4da7e;
  font-family: Georgia, serif;
  font-size: 29px;
  font-weight: 700;
  box-shadow: 0 0 34px rgba(232, 197, 71, 0.14);
}

.login-kicker {
  margin-bottom: 8px;
  color: #9f8d50;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 5px;
  text-align: center;
}

.login-title {
  text-align: center;
  margin-bottom: 7px;
  color: #f2f2f3;
  font-size: 24px;
  font-weight: 650;
  letter-spacing: 1.5px;
}

.login-subtitle {
  text-align: center;
  margin-bottom: 32px;
  color: #73767e;
  font-size: 12px;
}

.login-box :deep(.el-form-item) {
  display: block;
  margin-bottom: 20px;
}

.field-label {
  display: block;
  margin-bottom: 8px;
  color: #a8aab0;
  font-size: 12px;
  font-weight: 500;
}

.login-box :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 9px;
}

.login-box :deep(.el-button) {
  height: 46px;
  margin-top: 6px;
  border-radius: 9px !important;
  letter-spacing: 2px;
}

.tips {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
  color: #62656d;
  font-size: 11px;
}

.tips span {
  height: 1px;
  flex: 1;
  background: rgba(255, 255, 255, 0.07);
}
</style>

