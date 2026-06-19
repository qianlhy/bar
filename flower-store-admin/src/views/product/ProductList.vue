<template>
  <div class="product-list">
    <el-card>
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          <span>添加商品</span>
        </el-button>
        <el-input
          v-model="queryParams.name"
          placeholder="搜索商品名称"
          style="width: 300px"
          clearable
          @change="fetchData"
        />
      </div>

      <el-table :data="tableData" border style="margin-top: 20px">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="image" label="商品图片" width="100">
          <template #default="{ row }">
            <el-image :src="row.image" style="width: 60px; height: 60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="inventory" label="库存" width="100" />
        <el-table-column prop="sales" label="销量" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="原价" prop="originalPrice">
          <el-input-number v-model="form.originalPrice" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="库存" prop="inventory">
          <el-input-number v-model="form.inventory" :min="0" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="商品详情" prop="detail">
          <el-input v-model="form.detail" type="textarea" rows="4" />
        </el-form-item>
        <el-form-item label="主图" prop="image">
          <el-upload
            class="image-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleMainImageSuccess"
            :before-upload="beforeImageUpload"
          >
            <img v-if="form.image" :src="form.image" class="image-preview" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">
            建议尺寸：800x800px，支持 JPG/PNG/WEBP/GIF 格式，最大10MB
          </div>
        </el-form-item>
        <el-form-item label="轮播图" prop="images">
          <el-upload
            class="images-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :on-success="handleImagesSuccess"
            :before-upload="beforeImageUpload"
            :file-list="imageFileList"
            list-type="picture-card"
            :on-remove="handleImagesRemove"
          >
            <el-icon class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">
            可上传多张轮播图，建议尺寸：800x800px，支持 JPG/PNG/WEBP/GIF 格式，最大10MB
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            active-text="上架"
            inactive-text="下架"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getProductPage,
  addProduct,
  updateProduct,
  deleteProduct
} from '@/api/product'
import { getCategoryList } from '@/api/category'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const queryParams = ref({
  current: 1,
  size: 10,
  name: ''
})

const tableData = ref([])
const total = ref(0)
const categories = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('添加商品')
const form = ref({
  id: null,
  name: '',
  categoryId: null,
  price: 0,
  originalPrice: 0,
  inventory: 0,
  sales: 0,
  rating: 5.0,
  image: '',
  images: '',
  description: '',
  detail: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const formRef = ref(null)
const submitLoading = ref(false)

// 上传配置
const uploadAction = ref('/api/file/upload')
const uploadHeaders = computed(() => ({
  Authorization: userStore.token
}))

// 轮播图文件列表
const imageFileList = ref([])

const fetchData = async () => {
  try {
    const res = await getProductPage(queryParams.value)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error(error)
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '添加商品'
  form.value = {
    id: null,
    name: '',
    categoryId: null,
    price: 0,
    originalPrice: 0,
    inventory: 0,
    sales: 0,
    rating: 5.0,
    image: '',
    images: '',
    description: '',
    detail: '',
    status: 1
  }
  imageFileList.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑商品'
  form.value = { ...row }
  
  // 初始化轮播图列表
  if (row.images) {
    const imageUrls = row.images.split(',')
    imageFileList.value = imageUrls.map((url, index) => ({
      name: `image${index}`,
      url: url
    }))
  } else {
    imageFileList.value = []
  }
  
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        submitLoading.value = true
        if (form.value.id) {
          await updateProduct(form.value)
        } else {
          await addProduct(form.value)
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
  ElMessageBox.confirm('确定要删除该商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteProduct(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      console.error(error)
    }
  })
}

// 图片上传前验证
const beforeImageUpload = (file) => {
  const validTypes = ['image/jpeg', 'image/png', 'image/webp', 'image/gif', 'image/jpg']
  const isImage = validTypes.includes(file.type)
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG/WEBP/GIF 格式的图片!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

// 主图上传成功
const handleMainImageSuccess = (response) => {
  if (response.code === 200) {
    form.value.image = response.data.url
    ElMessage.success('上传成功') 
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 轮播图上传成功
const handleImagesSuccess = (response) => {
  if (response.code === 200) {
    imageFileList.value.push({
      name: Date.now().toString(),
      url: response.data.url
    })
    // 更新表单中的images字段
    form.value.images = imageFileList.value.map(item => item.url).join(',')
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

// 移除轮播图
const handleImagesRemove = (file) => {
  const index = imageFileList.value.findIndex(item => item.url === file.url)
  if (index > -1) {
    imageFileList.value.splice(index, 1)
  }
  // 更新表单中的images字段
  form.value.images = imageFileList.value.map(item => item.url).join(',')
}

onMounted(() => {
  fetchData()
  fetchCategories()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-uploader :deep(.el-upload) {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.image-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.image-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  line-height: 178px;
}

.image-preview {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.images-uploader :deep(.el-upload-list__item) {
  width: 148px;
  height: 148px;
}

.images-uploader :deep(.el-upload) {
  width: 148px;
  height: 148px;
}
</style>

