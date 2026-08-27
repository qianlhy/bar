<template>
  <el-container class="layout-container">
    <el-aside width="232px">
      <div class="logo">
        <div class="logo-mark">A</div>
        <div class="logo-copy">
          <h3>梭哈酒馆</h3>
          <span class="logo-en">ALL IN TAVERN</span>
        </div>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
      >
        <el-menu-item v-if="!isStaff" index="/dashboard">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item v-if="!isStaff" index="/category">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item v-if="!isStaff" index="/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><Document /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/points">
          <el-icon><Coin /></el-icon>
          <span>积分录入</span>
        </el-menu-item>
        <el-menu-item v-if="!isStaff" index="/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item v-if="!isStaff" index="/rank">
          <el-icon><Trophy /></el-icon>
          <span>大师分排行榜</span>
        </el-menu-item>
        <el-sub-menu v-if="!isStaff" index="coin">
          <template #title>
            <el-icon><Coin /></el-icon>
            <span>All In币商城</span>
          </template>
          <el-menu-item index="/coin/product">商品管理</el-menu-item>
          <el-menu-item index="/coin/exchange">兑换记录</el-menu-item>
        </el-sub-menu>
        <el-sub-menu v-if="!isStaff" index="recharge">
          <template #title>
            <el-icon><Wallet /></el-icon>
            <span>会员充值</span>
          </template>
          <el-menu-item index="/recharge/package">充值套餐</el-menu-item>
          <el-menu-item index="/recharge/order">充值订单</el-menu-item>
        </el-sub-menu>
        <el-menu-item v-if="!isStaff" index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-left">
          <span class="page-title">{{ currentPageTitle }}</span>
          <span class="page-subtitle">梭哈酒馆运营管理中心</span>
        </div>
        <div class="header-right">
          <span class="system-status"><i></i>系统运行正常</span>
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" :src="userStore.userInfo?.avatar">
                {{ (userStore.userInfo?.nickname || '管').slice(0, 1) }}
              </el-avatar>
              <span class="user-copy">
                <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
                <span class="user-role">{{ isStaff ? '员工账号' : '管理员账号' }}</span>
              </span>
              <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
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

const isStaff = computed(() => userStore.userInfo?.role === 3)

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
  background: #0d0e10;
}

.el-aside {
  background:
    radial-gradient(circle at 50% 0, rgba(232, 197, 71, 0.06), transparent 240px),
    #111214;
  height: 100vh;
  border-right: 1px solid rgba(255, 255, 255, 0.065);
  box-shadow: 8px 0 30px rgba(0, 0, 0, 0.18);
}

.el-aside .el-menu {
  border-right: none;
  padding: 14px 12px 24px;
  background: transparent;
}

.el-aside :deep(.el-menu-item),
.el-aside :deep(.el-sub-menu__title) {
  height: 48px;
  margin-bottom: 5px;
  border-radius: 9px;
  color: #9fa2aa !important;
  transition: all 0.18s ease;
}

.el-aside :deep(.el-menu-item:hover),
.el-aside :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.045) !important;
  color: #f2f2f3 !important;
}

.el-aside :deep(.el-menu-item.is-active) {
  position: relative;
  background: linear-gradient(90deg, rgba(232, 197, 71, 0.15), rgba(232, 197, 71, 0.035)) !important;
  color: #f3d97a !important;
  font-weight: 600;
}

.el-aside :deep(.el-menu-item.is-active::before) {
  content: '';
  position: absolute;
  left: 0;
  width: 3px;
  height: 22px;
  border-radius: 0 3px 3px 0;
  background: #e8c547;
  box-shadow: 0 0 12px rgba(232, 197, 71, 0.45);
}

.el-aside :deep(.el-menu-item .el-icon),
.el-aside :deep(.el-sub-menu__title .el-icon) {
  font-size: 18px;
}

.logo {
  height: 82px;
  display: flex;
  align-items: center;
  padding: 0 22px;
  color: #e8c547;
  border-bottom: 1px solid rgba(255, 255, 255, 0.065);
}

.logo-mark {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-right: 12px;
  border: 1px solid rgba(232, 197, 71, 0.55);
  border-radius: 50%;
  background: radial-gradient(circle at 35% 25%, rgba(232, 197, 71, 0.22), rgba(232, 197, 71, 0.04));
  color: #f4da7e;
  font-family: Georgia, serif;
  font-size: 21px;
  font-weight: 700;
  box-shadow: 0 0 20px rgba(232, 197, 71, 0.12);
}

.logo-copy {
  min-width: 0;
}

.logo h3 {
  color: #f2d777;
  font-size: 18px;
  letter-spacing: 3px;
  line-height: 1.2;
}

.logo-en {
  display: block;
  margin-top: 5px;
  color: #77705a;
  font-size: 9px;
  letter-spacing: 2.4px;
}

.el-header {
  height: 72px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 28px;
  background: rgba(20, 21, 24, 0.96);
  border-bottom: 1px solid rgba(255, 255, 255, 0.07);
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.16);
}

.page-title {
  color: #f3f3f4 !important;
  font-size: 20px;
  font-weight: 650;
  letter-spacing: 0.5px;
}

.page-subtitle {
  margin-left: 14px;
  padding-left: 14px;
  border-left: 1px solid rgba(255, 255, 255, 0.12);
  color: #6f727a;
  font-size: 12px;
}

.username {
  color: #f5f5f5 !important;
  font-size: 13px;
  font-weight: 600;
  line-height: 1.2;
}

.user-role {
  margin-top: 3px;
  color: #777a82;
  font-size: 10px;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.system-status {
  display: inline-flex;
  align-items: center;
  color: #777a82;
  font-size: 12px;
}

.system-status i {
  width: 7px;
  height: 7px;
  margin-right: 8px;
  border-radius: 50%;
  background: #5fc98b;
  box-shadow: 0 0 8px rgba(95, 201, 139, 0.6);
}

.user-info {
  display: flex;
  align-items: center;
  min-width: 146px;
  padding: 7px 10px 7px 8px;
  border: 1px solid rgba(255, 255, 255, 0.07);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.025);
  cursor: pointer;
  outline: none;
  transition: border-color 0.18s ease, background 0.18s ease;
}

.user-info:hover {
  border-color: rgba(232, 197, 71, 0.26);
  background: rgba(232, 197, 71, 0.035);
}

.user-info :deep(.el-avatar) {
  border: 1px solid rgba(232, 197, 71, 0.35);
  background: #292b30;
  color: #e8c547;
}

.user-copy {
  display: flex;
  flex: 1;
  flex-direction: column;
  margin-left: 9px;
}

.dropdown-arrow {
  color: #747780;
  font-size: 12px;
}

.el-main {
  padding: 24px;
  overflow: auto;
  background:
    radial-gradient(circle at 100% 0, rgba(232, 197, 71, 0.025), transparent 360px),
    #0d0e10;
}
</style>

