<template>
  <div class="user-list">
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="avatar" label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" v-if="row.avatar" />
            <el-avatar v-else>{{ row.nickname ? row.nickname[0] : '用' }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <span>{{ getGenderText(row.gender) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="90" />
        <el-table-column prop="coins" label="All In币" width="90" />
        <el-table-column prop="masterScore" label="大师分" width="90" />
        <el-table-column prop="couponCount" label="优惠券" width="90" />
        <el-table-column prop="points" label="积分" width="90">
          <template #default="{ row }">
            <span style="color:#e8c547;font-weight:bold">{{ row.points || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEditAssets(row)">调整资产</el-button>
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

    <el-dialog v-model="dialogVisible" title="调整用户资产" width="420px">
      <el-form label-width="90px">
        <el-form-item label="昵称">
          <span>{{ form.nickname }}</span>
        </el-form-item>
        <el-form-item label="余额(元)">
          <el-input-number v-model="form.balance" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="All In币">
          <el-input-number v-model="form.coins" :min="0" />
        </el-form-item>
        <el-form-item label="大师分">
          <el-input-number v-model="form.masterScore" :min="0" />
        </el-form-item>
        <el-form-item label="优惠券">
          <el-input-number v-model="form.couponCount" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitAssets" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserPage, updateUserStatus, updateUserAssets } from '@/api/user'
import { ElMessage } from 'element-plus'

const queryParams = ref({
  current: 1,
  size: 10
})

const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const submitLoading = ref(false)
const form = ref({ id: null, nickname: '', balance: 0, coins: 0, masterScore: 0, couponCount: 0 })

const handleEditAssets = (row) => {
  form.value = {
    id: row.id,
    nickname: row.nickname,
    balance: Number(row.balance || 0),
    coins: row.coins || 0,
    masterScore: row.masterScore || 0,
    couponCount: row.couponCount || 0
  }
  dialogVisible.value = true
}

const handleSubmitAssets = async () => {
  try {
    submitLoading.value = true
    await updateUserAssets(form.value.id, {
      balance: form.value.balance,
      coins: form.value.coins,
      masterScore: form.value.masterScore,
      couponCount: form.value.couponCount
    })
    ElMessage.success('调整成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const fetchData = async () => {
  try {
    const res = await getUserPage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
    ElMessage.error('获取用户列表失败')
  }
}

const getGenderText = (gender) => {
  const map = {
    0: '未知',
    1: '男',
    2: '女'
  }
  return map[gender] || '未知'
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
</style>

