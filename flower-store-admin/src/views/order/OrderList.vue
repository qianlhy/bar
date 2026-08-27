<template>
  <div class="order-list">
    <el-card>
      <div class="toolbar">
        <el-select
          v-model="queryParams.status"
          placeholder="订单状态"
          clearable
          @change="fetchData"
          style="width: 200px"
        >
          <el-option label="待付款" :value="1" />
          <el-option label="待出品" :value="2" />
          <el-option label="出品中" :value="3" />
          <el-option label="已完成" :value="4" />
          <el-option label="已取消" :value="5" />
        </el-select>
        <el-input
          v-model="queryParams.orderNo"
          placeholder="搜索订单号"
          style="width: 300px"
          clearable
          @change="fetchData"
        />
      </div>

      <el-table :data="tableData" border style="margin-top: 20px">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="receiverName" label="桌台/顾客" width="110" />
        <el-table-column prop="receiverPhone" label="联系方式" width="120" />
        <el-table-column prop="actualPayment" label="实付" width="90">
          <template #default="{ row }">
            <span class="price-emphasis">¥{{ row.actualPayment }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="pointsUsed" label="积分抵扣" width="110">
          <template #default="{ row }">
            <span v-if="row.pointsUsed">{{ row.pointsUsed }}分/¥{{ row.pointsAmount }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120">
          <template #default="{ row }">
            {{ formatPayMethod(row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleView(row)">查看详情</el-button>
            <el-button
              v-if="!isStaff && row.status === 2"
              type="success"
              text
              @click="handleShip(row)"
            >
              开始出品
            </el-button>
            <el-button
              v-if="!isStaff && row.status === 3"
              type="success"
              text
              @click="handleComplete(row)"
            >
              完成
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 20px"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderPage, updateOrderStatus } from '@/api/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const isStaff = computed(() => userStore.userInfo?.role === 3)

const queryParams = ref({
  current: 1,
  size: 10,
  orderNo: '',
  status: null
})

const tableData = ref([])
const total = ref(0)

const fetchData = async () => {
  try {
    const res = await getOrderPage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
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

const handleView = (row) => {
  router.push(`/order/${row.id}`)
}

const handleShip = (row) => {
  ElMessageBox.confirm('确认开始出品该订单吗？', '操作确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus(row.id, 3)
      ElMessage.success('已开始出品')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

const handleComplete = (row) => {
  ElMessageBox.confirm('确定要完成订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateOrderStatus(row.id, 4)
      ElMessage.success('操作成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 10px;
  align-items: center;
}
</style>

