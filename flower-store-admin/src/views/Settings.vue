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
        <el-form-item label="WiFi名称">
          <el-input v-model="storeForm.wifi_name" placeholder="请输入WiFi名称" />
        </el-form-item>
        <el-form-item label="WiFi密码">
          <el-input v-model="storeForm.wifi_password" placeholder="请输入WiFi密码" />
        </el-form-item>
        <el-form-item label="充值说明">
          <el-input v-model="storeForm.recharge_tip" placeholder="会员充值页文案" />
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
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllConfig, saveConfig } from '@/api/config'

const loading = ref(false)
const storeLoading = ref(false)

const storeForm = ref({
  store_name: '',
  store_address: '',
  store_phone: '',
  wifi_name: '',
  wifi_password: '',
  recharge_tip: ''
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
    await saveConfig(storeForm.value)
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error(error)
  } finally {
    storeLoading.value = false
  }
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
</style>

