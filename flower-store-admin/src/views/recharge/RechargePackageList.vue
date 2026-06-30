<template>
  <div class="recharge-package-list">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>添加套餐</span>
        </el-button>
      </div>

      <el-table :data="tableData" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="套餐名称" />
        <el-table-column prop="payAmount" label="实付金额(元)" width="130" />
        <el-table-column prop="balance" label="到账余额(元)" width="130" />
        <el-table-column prop="giftCoins" label="赠送All In币" width="120" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" text @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="如：充100送20" />
        </el-form-item>
        <el-form-item label="实付金额(元)" prop="payAmount">
          <el-input-number v-model="form.payAmount" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="到账余额(元)" prop="balance">
          <el-input-number v-model="form.balance" :min="0" :precision="2" :step="10" />
        </el-form-item>
        <el-form-item label="赠送All In币">
          <el-input-number v-model="form.giftCoins" :min="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getRechargePackagePage,
  addRechargePackage,
  updateRechargePackage,
  deleteRechargePackage
} from '@/api/recharge'

const queryParams = ref({ current: 1, size: 10 })
const tableData = ref([])
const total = ref(0)

const dialogVisible = ref(false)
const dialogTitle = ref('添加套餐')
const form = ref({ id: null, name: '', payAmount: 0, balance: 0, giftCoins: 0, sort: 0, status: 1 })
const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  payAmount: [{ required: true, message: '请输入实付金额', trigger: 'blur' }],
  balance: [{ required: true, message: '请输入到账余额', trigger: 'blur' }]
}
const formRef = ref(null)
const submitLoading = ref(false)

const fetchData = async () => {
  try {
    const res = await getRechargePackagePage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加套餐'
  form.value = { id: null, name: '', payAmount: 0, balance: 0, giftCoins: 0, sort: 0, status: 1 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑套餐'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        if (form.value.id) {
          await updateRechargePackage(form.value)
        } else {
          await addRechargePackage(form.value)
        }
        ElMessage.success('操作成功')
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        console.error(error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该套餐吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRechargePackage(row.id)
      ElMessage.success('删除成功')
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
  justify-content: space-between;
  align-items: center;
}
</style>
