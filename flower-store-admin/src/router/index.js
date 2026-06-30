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
      redirect: '/dashboard',
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '首页' }
        },
        {
          path: '/category',
          name: 'Category',
          component: () => import('@/views/category/CategoryList.vue'),
          meta: { title: '分类管理' }
        },
        {
          path: '/product',
          name: 'Product',
          component: () => import('@/views/product/ProductList.vue'),
          meta: { title: '商品管理' }
        },
        {
          path: '/order',
          name: 'Order',
          component: () => import('@/views/order/OrderList.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: '/order/:id',
          name: 'OrderDetail',
          component: () => import('@/views/order/OrderDetail.vue'),
          meta: { title: '订单详情' }
        },
        {
          path: '/user',
          name: 'User',
          component: () => import('@/views/user/UserList.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: '/rank',
          name: 'Rank',
          component: () => import('@/views/user/RankList.vue'),
          meta: { title: '大师分排行榜' }
        },
        {
          path: '/coin/product',
          name: 'CoinProduct',
          component: () => import('@/views/coin/CoinProductList.vue'),
          meta: { title: 'All In币商品' }
        },
        {
          path: '/coin/exchange',
          name: 'CoinExchange',
          component: () => import('@/views/coin/CoinExchangeList.vue'),
          meta: { title: 'All In币兑换记录' }
        },
        {
          path: '/recharge/package',
          name: 'RechargePackage',
          component: () => import('@/views/recharge/RechargePackageList.vue'),
          meta: { title: '充值套餐' }
        },
        {
          path: '/recharge/order',
          name: 'RechargeOrder',
          component: () => import('@/views/recharge/RechargeOrderList.vue'),
          meta: { title: '充值订单' }
        },
        {
          path: '/settings',
          name: 'Settings',
          component: () => import('@/views/Settings.vue'),
          meta: { title: '系统设置' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.path === '/login') {
    // 如果已登录，跳转到首页
    if (userStore.token) {
      next('/')
    } else {
      next()
    }
  } else {
    // 其他页面需要登录
    if (userStore.token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router

