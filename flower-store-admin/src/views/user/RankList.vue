<template>
  <div class="rank-list">
    <el-card>
      <div class="toolbar">
        <el-input v-model="queryParams.nickname" placeholder="按昵称搜索" clearable style="width: 200px" @keyup.enter="handleSearch" />
        <el-button type="primary" style="margin-left: 10px" @click="handleSearch">搜索</el-button>
      </div>

      <el-table :data="tableData" border style="margin-top: 20px">
        <el-table-column label="排名" width="80">
          <template #default="{ $index }">
            <span>{{ (queryParams.current - 1) * queryParams.size + $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="avatar" label="头像" width="100">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" v-if="row.avatar" />
            <el-avatar v-else>{{ row.nickname ? row.nickname[0] : '用' }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="masterScore" label="大师分" width="120" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">修改分数</el-button>
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

    <el-dialog v-model="dialogVisible" title="修改大师分" width="400px">
      <el-form label-width="80px">
        <el-form-item label="昵称">
          <span>{{ form.nickname }}</span>
        </el-form-item>
        <el-form-item label="大师分">
          <el-input-number v-model="form.masterScore" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRankPage, updateUserAssets } from '@/api/user'

const queryParams = ref({ current: 1, size: 10, nickname: '' })
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const form = ref({ id: null, nickname: '', masterScore: 0 })
const submitLoading = ref(false)

const fetchData = async () => {
  try {
    const res = await getRankPage(queryParams.value)
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

const handleEdit = (row) => {
  form.value = { id: row.id, nickname: row.nickname, masterScore: row.masterScore || 0 }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    submitLoading.value = true
    await updateUserAssets(form.value.id, { masterScore: form.value.masterScore })
    ElMessage.success('修改成功')
    dialogVisible.value = false
    fetchData()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
}
</style>
