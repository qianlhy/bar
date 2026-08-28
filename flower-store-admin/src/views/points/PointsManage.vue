<template>
  <div class="points-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>手动录入积分</span>
          <el-tag type="info">100积分 = 1元 · 每日0点清零</el-tag>
        </div>
      </template>

      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="昵称">
          <el-input v-model="queryParams.nickname" clearable placeholder="搜索用户昵称" @keyup.enter="fetchUsers" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" clearable placeholder="搜索手机号" @keyup.enter="fetchUsers" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchUsers">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" v-if="row.avatar" />
            <el-avatar v-else>{{ row.nickname ? row.nickname[0] : '用' }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="points" label="当前积分" width="100">
          <template #default="{ row }">
            <span class="points-num">{{ row.points || 0 }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="openAddDialog(row)">录入积分</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px"
        @size-change="fetchUsers"
        @current-change="fetchUsers"
      />
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header>
        <span>最近积分流水</span>
      </template>
      <el-table :data="logData" border size="small">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="changePoints" label="变动" width="90">
          <template #default="{ row }">
            <span :style="{ color: row.changePoints > 0 ? '#67c23a' : '#f56c6c' }">
              {{ row.changePoints > 0 ? '+' : '' }}{{ row.changePoints }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="beforePoints" label="变动前" width="90" />
        <el-table-column prop="afterPoints" label="变动后" width="90" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="adminName" label="操作人" width="120" />
        <el-table-column prop="remark" label="备注" min-width="140" />
        <el-table-column prop="createTime" label="时间" width="170" />
      </el-table>
      <el-pagination
        v-model:current-page="logQuery.current"
        v-model:page-size="logQuery.size"
        :total="logTotal"
        layout="total, prev, pager, next"
        style="margin-top: 12px"
        @current-change="fetchLogs"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="录入积分"
      width="420px"
      :close-on-click-modal="false"
      @closed="onDialogClosed"
    >
      <el-alert
        v-if="lockHint"
        :title="lockHint"
        type="warning"
        show-icon
        :closable="false"
        style="margin-bottom: 12px"
      />
      <el-form label-width="90px">
        <el-form-item label="用户">
          <span>{{ currentUser?.nickname }}（ID: {{ currentUser?.id }}）</span>
        </el-form-item>
        <el-form-item label="当前积分">
          <span class="points-num">{{ currentUser?.points || 0 }}</span>
        </el-form-item>
        <el-form-item label="录入积分" required>
          <el-input-number v-model="form.points" :min="1" :max="999999" :step="100" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="如：开台录入 / 整局结算补录" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" :disabled="!lockedByMe" @click="submitAdd">
          确认录入
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserPage } from '@/api/user'
import { addPoints, getPointsLogs, lockUser, unlockUser } from '@/api/points'

const queryParams = ref({ current: 1, size: 10, nickname: '', phone: '' })
const tableData = ref([])
const total = ref(0)

const logQuery = ref({ current: 1, size: 10 })
const logData = ref([])
const logTotal = ref(0)

const dialogVisible = ref(false)
const submitLoading = ref(false)
const currentUser = ref(null)
const form = ref({ points: 100, remark: '' })
const lockedByMe = ref(false)
const lockHint = ref('')

const typeText = (type) => {
  const map = { 1: '手动录入', 2: '消费抵扣', 3: '取消退回', 4: '每日清零', 5: '每日签到' }
  return map[type] || '未知'
}

const fetchUsers = async () => {
  try {
    const res = await getUserPage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error('获取用户失败')
  }
}

const fetchLogs = async () => {
  try {
    const res = await getPointsLogs(logQuery.value)
    logData.value = res.data.records
    logTotal.value = res.data.total
  } catch (e) {
    console.error(e)
  }
}

const openAddDialog = async (row) => {
  currentUser.value = { ...row }
  form.value = { points: 100, remark: '' }
  lockedByMe.value = false
  lockHint.value = '正在锁定用户...'
  dialogVisible.value = true
  try {
    await lockUser(row.id)
    lockedByMe.value = true
    lockHint.value = '已锁定，其他员工暂时无法同时操作该用户'
  } catch (e) {
    lockedByMe.value = false
    lockHint.value = e?.message || e?.response?.data?.message || '锁定失败，可能已被其他员工占用'
  }
}

const onDialogClosed = async () => {
  if (currentUser.value?.id && lockedByMe.value) {
    try {
      await unlockUser(currentUser.value.id)
    } catch (e) {
      console.error(e)
    }
  }
  lockedByMe.value = false
  currentUser.value = null
}

const submitAdd = async () => {
  if (!form.value.points || form.value.points <= 0) {
    ElMessage.warning('请输入积分')
    return
  }
  try {
    submitLoading.value = true
    await addPoints({
      userId: currentUser.value.id,
      points: form.value.points,
      remark: form.value.remark
    })
    ElMessage.success('录入成功')
    dialogVisible.value = false
    fetchUsers()
    fetchLogs()
  } catch (e) {
    ElMessage.error(e?.message || '录入失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchUsers()
  fetchLogs()
})

onBeforeUnmount(() => {
  onDialogClosed()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.points-num {
  color: #e8c547;
  font-weight: bold;
}
</style>
