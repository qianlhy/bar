<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending || 0 }}</div>
              <div class="stat-label">待付款订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon to-ship">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.toShip || 0 }}</div>
              <div class="stat-label">待发货订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon shipped">
              <el-icon><Van /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.shipped || 0 }}</div>
              <div class="stat-label">已发货订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completed || 0 }}</div>
              <div class="stat-label">已完成订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card style="margin-top: 20px">
      <template #header>
        <span>新订单提醒</span>
      </template>
      <div class="notification-status">
        <div v-if="wsConnected">
          <el-icon color="#67c23a" :size="18"><CircleCheck /></el-icon>
          <span style="color: #67c23a; margin-left: 8px">订单通知已连接</span>
        </div>
        <div v-else>
          <el-icon color="#f56c6c" :size="18"><CircleClose /></el-icon>
          <span style="color: #f56c6c; margin-left: 8px">订单通知未连接</span>
          <el-button type="text" @click="connectWebSocket">重新连接</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElNotification } from 'element-plus'
import { getOrderStatistics } from '@/api/order'

const statistics = ref({})
const wsConnected = ref(false)
let ws = null

// 获取订单统计
const fetchStatistics = async () => {
  try {
    const res = await getOrderStatistics()
    statistics.value = res.data
  } catch (error) {
    console.error(error)
  }
}

// 连接WebSocket
const connectWebSocket = () => {
  const wsUrl = 'ws://localhost:8080/api/ws/order/notification'
  ws = new WebSocket(wsUrl)
  
  ws.onopen = () => {
    console.log('WebSocket连接已建立')
    wsConnected.value = true
  }
  
  ws.onmessage = (event) => {
    const message = JSON.parse(event.data)
    if (message.type === 'NEW_ORDER') {
      // 刷新统计数据
      fetchStatistics()
      
      // 显示通知
      ElNotification({
        title: '新订单提醒',
        message: `订单号：${message.data.orderNo}，金额：¥${message.data.actualPayment}`,
        type: 'success',
        duration: 0
      })
      
      // 播放语音（使用浏览器TTS）
      if (message.voiceText && 'speechSynthesis' in window) {
        const utterance = new SpeechSynthesisUtterance(message.voiceText)
        utterance.lang = 'zh-CN'
        window.speechSynthesis.speak(utterance)
      }
    }
  }
  
  ws.onerror = () => {
    console.error('WebSocket连接错误')
    wsConnected.value = false
  }
  
  ws.onclose = () => {
    console.log('WebSocket连接已关闭')
    wsConnected.value = false
  }
}

onMounted(() => {
  fetchStatistics()
  connectWebSocket()
})

onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style scoped>
.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.to-ship {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.shipped {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.completed {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  margin-left: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.notification-status {
  display: flex;
  align-items: center;
  padding: 10px;
}
</style>

