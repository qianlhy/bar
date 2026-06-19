<template>
  <div class="settings">
    <el-card>
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
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)

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

