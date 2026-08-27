import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/layouts/MainLayout.vue'),
      redirect: () => {
        try {
          const info = JSON.parse(localStorage.getItem('userInfo') || 'null')
          return info?.role === 3 ? '/order' : '/dashboard'
        } catch (e) {
          return '/dashboard'
        }
      },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页', roles: [1, 2] }
        },
        {
          path: '/category',
          name: 'Category',
          component: () => import('@/views/category/CategoryList.vue'),
          meta: { title: '分类管理', roles: [1, 2] }
        },
        {
          path: '/product',
          name: 'Product',
          component: () => import('@/views/product/ProductList.vue'),
          meta: { title: '商品管理', roles: [1, 2] }
        },
        {
          path: '/order',
          name: 'Order',
          component: () => import('@/views/order/OrderList.vue'),
          meta: { title: '订单管理', roles: [1, 2, 3] }
        },
        {
          path: '/order/:id',
          name: 'OrderDetail',
          component: () => import('@/views/order/OrderDetail.vue'),
          meta: { title: '订单详情', roles: [1, 2, 3] }
        },
        {
          path: '/points',
          name: 'Points',
          component: () => import('@/views/points/PointsManage.vue'),
          meta: { title: '积分录入', roles: [1, 2, 3] }
        },
        {
          path: '/user',
          name: 'User',
          component: () => import('@/views/user/UserList.vue'),
          meta: { title: '用户管理', roles: [1, 2] }
        },
        {
          path: '/rank',
          name: 'Rank',
          component: () => import('@/views/user/RankList.vue'),
          meta: { title: '大师分排行榜', roles: [1, 2] }
        },
        {
          path: '/coin/product',
          name: 'CoinProduct',
          component: () => import('@/views/coin/CoinProductList.vue'),
          meta: { title: 'All In币商品', roles: [1, 2] }
        },
        {
          path: '/coin/exchange',
          name: 'CoinExchange',
          component: () => import('@/views/coin/CoinExchangeList.vue'),
          meta: { title: 'All In币兑换记录', roles: [1, 2] }
        },
        {
          path: '/recharge/package',
          name: 'RechargePackage',
          component: () => import('@/views/recharge/RechargePackageList.vue'),
          meta: { title: '充值套餐', roles: [1, 2] }
        },
        {
          path: '/recharge/order',
          name: 'RechargeOrder',
          component: () => import('@/views/recharge/RechargeOrderList.vue'),
          meta: { title: '充值订单', roles: [1, 2] }
        },
        {
          path: '/settings',
          name: 'Settings',
          component: () => import('@/views/Settings.vue'),
          meta: { title: '系统设置', roles: [1, 2] }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.path === '/login') {
    if (userStore.token) {
      // 员工默认进订单页
      if (userStore.userInfo?.role === 3) {
        next('/order')
      } else {
        next('/')
      }
    } else {
      next()
    }
  } else {
    if (!userStore.token) {
      next('/login')
      return
    }
    const role = userStore.userInfo?.role || 1
    const roles = to.meta?.roles
    if (roles && !roles.includes(role)) {
      next(role === 3 ? '/order' : '/')
      return
    }
    next()
  }
})

export default router

