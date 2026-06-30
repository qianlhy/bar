<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <div class="logo">
        <h3>梭哈酒馆</h3>
        <span class="logo-en">All In Tavern</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#141414"
        text-color="#bfbfbf"
        active-text-color="#e8c547"
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/rank">
          <el-icon><Trophy /></el-icon>
          <span>大师分排行榜</span>
        </el-menu-item>
        <el-sub-menu index="coin">
          <template #title>
            <el-icon><Coin /></el-icon>
            <span>All In币商城</span>
          </template>
          <el-menu-item index="/coin/product">商品管理</el-menu-item>
          <el-menu-item index="/coin/exchange">兑换记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="recharge">
          <template #title>
            <el-icon><Wallet /></el-icon>
            <span>会员充值</span>
          </template>
          <el-menu-item index="/recharge/package">充值套餐</el-menu-item>
          <el-menu-item index="/recharge/order">充值订单</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left">
          <span class="page-title">{{ currentPageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const currentPageTitle = computed(() => {
  return route.meta.title || '首页'
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #141414;
  height: 100vh;
}

.el-aside .el-menu {
  border-right: none;
}

.logo {
  height: 70px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #e8c547;
  border-bottom: 1px solid #2a2a2a;
}

.logo h3 {
  font-size: 20px;
  letter-spacing: 2px;
  color: #e8c547;
}

.logo-en {
  font-size: 11px;
  letter-spacing: 3px;
  color: #8a7a3a;
  margin-top: 2px;
}

.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #1f1f1f;
  border-bottom: 2px solid #e8c547;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.page-title {
  font-size: 18px;
  font-weight: 500;
  color: #f5f5f5 !important;
}

.username {
  margin-left: 10px;
  color: #f5f5f5 !important;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>

