<template>
  <div class="settings">
    <el-card>
      <template #header>
        <span>门店信息 / WiFi 配置</span>
      </template>
      <el-form :model="storeForm" label-width="120px">
        <el-form-item label="门店名称">
          <el-input v-model="storeForm.store_name" placeholder="如：梭哈酒馆 - 武昌店" />
        </el-form-item>
        <el-form-item label="门店地址">
          <el-input v-model="storeForm.store_address" type="textarea" :rows="2" placeholder="请输入门店地址" />
        </el-form-item>
        <el-form-item label="门店电话">
          <el-input v-model="storeForm.store_phone" placeholder="请输入门店联系电话" />
        </el-form-item>
        <el-form-item label="营业时间">
          <el-input v-model="storeForm.business_hours" placeholder="如：19:30-1:30" />
        </el-form-item>
        <el-form-item label="门店纬度">
          <el-input v-model="storeForm.store_latitude" placeholder="导航用，如 32.0618" />
        </el-form-item>
        <el-form-item label="门店经度">
          <el-input v-model="storeForm.store_longitude" placeholder="导航用，如 118.6286" />
        </el-form-item>
        <el-form-item label="WiFi名称">
          <el-input v-model="storeForm.wifi_name" placeholder="请输入WiFi名称" />
        </el-form-item>
        <el-form-item label="WiFi密码">
          <el-input v-model="storeForm.wifi_password" placeholder="请输入WiFi密码" />
        </el-form-item>
        <el-form-item label="充值说明">
          <el-input v-model="storeForm.recharge_tip" placeholder="会员充值页文案" />
        </el-form-item>
        <el-form-item label="店内环境图">
          <el-upload
            class="gallery-uploader"
            :action="uploadAction"
            :headers="uploadHeaders"
            :on-success="handleGallerySuccess"
            :before-upload="beforeGalleryUpload"
            :file-list="galleryFileList"
            list-type="picture-card"
            :on-remove="handleGalleryRemove"
          >
            <el-icon class="gallery-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div style="color: #999; font-size: 12px; margin-top: 5px">
            上传店内装修展示图，小程序「店内环境」页展示，建议横图 16:9
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveStoreConfig" :loading="storeLoading">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>积分抵扣设置</span>
      </template>
      <el-form label-width="160px">
        <el-form-item label="积分抵扣开关">
          <el-switch
            v-model="pointsEnabled"
            active-text="开启"
            inactive-text="关闭"
            @change="savePointsConfig"
          />
          <span style="margin-left: 12px; color: #999; font-size: 12px">
            关闭后小程序下单不可使用积分
          </span>
        </el-form-item>
        <el-form-item label="兑换比例">
          <span>100 积分 = 1 元（固定）</span>
        </el-form-item>
        <el-form-item label="单笔最高抵扣">
          <span>订单金额的 50%（固定）</span>
        </el-form-item>
        <el-form-item label="清零规则">
          <span>每日 0 点自动清零所有用户积分</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>微信小程序配置</span>
      </template>
      <el-form :model="wechatForm" label-width="120px">
        <el-form-item label="AppID">
          <el-input v-model="wechatForm.appid" placeholder="请输入微信小程序AppID" />
        </el-form-item>
        <el-form-item label="AppSecret">
          <el-input v-model="wechatForm.secret" type="password" placeholder="请输入微信小程序AppSecret" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveWechatConfig" :loading="loading">
            保存配置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>文件上传配置</span>
      </template>
      <el-form :model="fileForm" label-width="120px">
        <el-form-item label="上传路径">
          <el-input v-model="fileForm.uploadPath" placeholder="请输入文件上传路径" />
          <span style="color: #999; font-size: 12px">例如：./uploads/</span>
        </el-form-item>
        <el-form-item label="最大文件大小">
          <el-input v-model="fileForm.maxSize" placeholder="请输入最大文件大小（字节）">
            <template #append>字节</template>
          </el-input>
          <span style="color: #999; font-size: 12px">10MB = 10485760 字节</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveFileConfig" :loading="loading">
            保存配置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>订单通知配置</span>
      </template>
      <el-form :model="orderForm" label-width="120px">
        <el-form-item label="通知开关">
          <el-switch v-model="orderForm.enabled" />
        </el-form-item>
        <el-form-item label="语音播报">
          <el-switch v-model="orderForm.voiceEnabled" />
        </el-form-item>
        <el-form-item label="播报文本">
          <el-input v-model="orderForm.voiceText" placeholder="请输入语音播报文本" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveOrderConfig" :loading="loading">
            保存配置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getAllConfig, saveConfig } from '@/api/config'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const uploadAction = ref('/api/file/upload')
const uploadHeaders = computed(() => ({
  Authorization: userStore.token ? `Bearer ${userStore.token}` : ''
}))

const loading = ref(false)
const storeLoading = ref(false)
const galleryFileList = ref([])

const storeForm = ref({
  store_name: '',
  store_address: '',
  store_phone: '',
  business_hours: '19:30-1:30',
  store_latitude: '32.0618',
  store_longitude: '118.6286',
  wifi_name: '',
  wifi_password: '',
  recharge_tip: '',
  checkin_points: '500',
  store_gallery: ''
})

const pointsEnabled = ref(true)

const loadStoreConfig = async () => {
  try {
    const res = await getAllConfig()
    const data = res.data || {}
    Object.keys(storeForm.value).forEach((k) => {
      if (data[k] !== undefined) storeForm.value[k] = data[k]
    })
    pointsEnabled.value = data.points_enabled !== '0'
    if (data.store_gallery) {
      const urls = data.store_gallery.split(',').map((s) => s.trim()).filter(Boolean)
      galleryFileList.value = urls.map((url, index) => ({ name: `img-${index}`, url }))
    } else {
      galleryFileList.value = []
    }
  } catch (error) {
    console.error(error)
  }
}

const savePointsConfig = async (val) => {
  try {
    await saveConfig({ points_enabled: val ? '1' : '0' })
    ElMessage.success(val ? '已开启积分抵扣' : '已关闭积分抵扣')
  } catch (error) {
    pointsEnabled.value = !val
    console.error(error)
  }
}

const saveStoreConfig = async () => {
  try {
    storeLoading.value = true
    storeForm.value.store_gallery = galleryFileList.value.map((item) => item.url).join(',')
    await saveConfig(storeForm.value)
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error(error)
  } finally {
    storeLoading.value = false
  }
}

const beforeGalleryUpload = (file) => {
  const ok = ['image/jpeg', 'image/png', 'image/webp', 'image/gif'].includes(file.type)
  if (!ok) {
    ElMessage.error('只能上传 JPG/PNG/WEBP/GIF 格式的图片!')
    return false
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleGallerySuccess = (response) => {
  if (response && response.code === 200) {
    const url = typeof response.data === 'string' ? response.data : (response.data && response.data.url)
    if (url) {
      galleryFileList.value.push({ name: `img-${Date.now()}`, url })
      ElMessage.success('上传成功')
    }
  } else {
    ElMessage.error((response && response.message) || '上传失败')
  }
}

const handleGalleryRemove = (file) => {
  const index = galleryFileList.value.findIndex((item) => item.url === file.url)
  if (index > -1) galleryFileList.value.splice(index, 1)
}

onMounted(() => {
  loadStoreConfig()
})

const wechatForm = ref({
  appid: '',
  secret: ''
})

const fileForm = ref({
  uploadPath: './uploads/',
  maxSize: 10485760
})

const orderForm = ref({
  enabled: true,
  voiceEnabled: true,
  voiceText: '您有新的订单，请及时处理'
})

const saveWechatConfig = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('配置保存成功（提示：实际配置需在 application.yml 中修改）')
  }, 500)
}

const saveFileConfig = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('配置保存成功（提示：实际配置需在 application.yml 中修改）')
  }, 500)
}

const saveOrderConfig = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('配置保存成功（提示：实际配置需在 application.yml 中修改）')
  }, 500)
}
</script>

<style scoped>
.el-card {
  max-width: 800px;
}
.gallery-uploader :deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}
</style>

