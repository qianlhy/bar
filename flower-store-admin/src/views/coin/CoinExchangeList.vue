<template>
  <div class="coin-exchange-list">
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="用户昵称">
          <template #default="{ row }">
            <span>{{ row.nickname || ('用户' + row.userId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="兑换商品" />
        <el-table-column prop="coinPrice" label="消耗All In币" width="140" />
        <el-table-column prop="createTime" label="兑换时间" width="200" />
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
import { getCoinExchangePage } from '@/api/coin'

const queryParams = ref({ current: 1, size: 10 })
const tableData = ref([])
const total = ref(0)

const fetchData = async () => {
  try {
    const res = await getCoinExchangePage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
</style>
