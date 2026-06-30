<template>
  <div class="recharge-order-list">
    <el-card>
      <div class="toolbar">
        <el-select v-model="queryParams.status" placeholder="全部状态" clearable style="width: 160px" @change="handleSearch">
          <el-option label="待支付" :value="0" />
          <el-option label="已到账" :value="1" />
          <el-option label="已取消" :value="2" />
        </el-select>
      </div>

      <el-table :data="tableData" border style="margin-top: 20px">
        <el-table-column prop="orderNo" label="订单号" width="220" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="payAmount" label="实付(元)" width="110" />
        <el-table-column prop="balance" label="到账余额(元)" width="130" />
        <el-table-column prop="giftCoins" label="赠送币" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" type="success" text @click="handleConfirm(row)">确认到账</el-button>
            <span v-else>-</span>
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRechargeOrderPage, confirmRechargeOrder } from '@/api/recharge'

const queryParams = ref({ current: 1, size: 10, status: null })
const tableData = ref([])
const total = ref(0)

const statusText = (s) => ({ 0: '待支付', 1: '已到账', 2: '已取消' }[s] || '未知')
const statusTagType = (s) => ({ 0: 'warning', 1: 'success', 2: 'info' }[s] || 'info')

const fetchData = async () => {
  try {
    const res = await getRechargeOrderPage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  queryParams.value.current = 1
  fetchData()
}

const handleConfirm = (row) => {
  ElMessageBox.confirm(`确认订单「${row.orderNo}」已收款并为用户到账余额 ¥${row.balance}？`, '确认到账', {
    confirmButtonText: '确认到账',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await confirmRechargeOrder(row.id)
      ElMessage.success('已确认到账')
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
  justify-content: flex-start;
  align-items: center;
}
</style>
