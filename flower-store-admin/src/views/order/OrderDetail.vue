<template>
  <div class="order-detail">
    <el-card v-if="order">
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center">
          <span>订单详情</span>
          <el-tag v-if="lockHint" :type="lockedByMe ? 'success' : 'warning'" size="small">{{ lockHint }}</el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(order.status)">
            {{ getStatusText(order.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="桌台/顾客">{{ order.receiverName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ order.receiverPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">
          {{ order.province }} {{ order.city }} {{ order.district }} {{ order.address }}
        </el-descriptions-item>
        <el-descriptions-item label="商品总价">¥{{ order.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="运费">¥{{ order.freight }}</el-descriptions-item>
        <el-descriptions-item label="积分抵扣">
          {{ order.pointsUsed || 0 }} 积分（¥{{ order.pointsAmount || 0 }}）
        </el-descriptions-item>
        <el-descriptions-item label="All In 币抵扣">
          {{ order.coinsUsed || 0 }} 币（¥{{ order.coinsAmount || 0 }}）
        </el-descriptions-item>
        <el-descriptions-item label="微信实付">
          ¥{{ order.wechatAmount != null ? order.wechatAmount : (order.actualPayment || 0) }}
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="price-emphasis">¥{{ order.actualPayment }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付方式">
          {{ formatPayMethod(order.paymentMethod) }}
        </el-descriptions-item>
        <el-descriptions-item label="订单备注" :span="2">
          {{ order.remark || '无' }}
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ order.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="出品时间">{{ order.deliveryTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ order.finishTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>商品明细</span>
      </template>

      <el-table :data="items" border>
        <el-table-column prop="productImage" label="商品图片" width="100">
          <template #default="{ row }">
            <el-image :src="row.productImage" style="width: 60px; height: 60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="count" label="数量" width="100" />
        <el-table-column prop="subtotal" label="小计" width="100">
          <template #default="{ row }">
            ¥{{ row.subtotal }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div style="margin-top: 20px; text-align: center">
      <el-button @click="$router.back()">返回</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getOrderById } from '@/api/order'
import { lockOrder, unlockOrder } from '@/api/points'

const route = useRoute()
const order = ref(null)
const items = ref([])
const lockHint = ref('')
const lockedByMe = ref(false)

const fetchData = async () => {
  try {
    const res = await getOrderById(route.params.id)
    order.value = res.data.order
    items.value = res.data.items
  } catch (error) {
    console.error(error)
  }
}

const tryLock = async () => {
  try {
    await lockOrder(route.params.id)
    lockedByMe.value = true
    lockHint.value = '已锁定本订单，其他员工暂时无法同时查看/处理'
  } catch (e) {
    lockedByMe.value = false
    lockHint.value = e?.message || '该订单正被其他员工查看'
    ElMessage.warning(lockHint.value)
  }
}

const releaseLock = async () => {
  if (!lockedByMe.value) return
  try {
    await unlockOrder(route.params.id)
  } catch (e) {
    console.error(e)
  }
  lockedByMe.value = false
}

const getStatusType = (status) => {
  const map = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info',
    5: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    1: '待付款',
    2: '待出品',
    3: '出品中',
    4: '已完成',
    5: '已取消'
  }
  return map[status] || '未知'
}

const formatPayMethod = (method) => {
  const map = {
    wechat: '微信支付',
    coins: 'All In 币',
    mixed: '混合支付',
    online: '微信支付',
    delivery: '店内支付'
  }
  return map[method] || method || '-'
}

onMounted(async () => {
  await tryLock()
  fetchData()
})

onBeforeUnmount(() => {
  releaseLock()
})
</script>

<style scoped>
.order-detail {
  padding: 20px;
}
</style>

