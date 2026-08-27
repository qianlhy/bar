<template>
  <div class="dashboard">
    <section class="welcome-panel">
      <div>
        <span class="welcome-kicker">ALL IN TAVERN</span>
        <h1>运营概览</h1>
        <p>实时查看订单进度与系统连接状态</p>
      </div>
      <div class="welcome-date">{{ currentDate }}</div>
    </section>

    <el-row :gutter="18">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pending || 0 }}<small>单</small></div>
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
              <div class="stat-value">{{ statistics.toShip || 0 }}<small>单</small></div>
              <div class="stat-label">待出品订单</div>
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
              <div class="stat-value">{{ statistics.shipped || 0 }}<small>单</small></div>
              <div class="stat-label">出品中订单</div>
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
              <div class="stat-value">{{ statistics.completed || 0 }}<small>单</small></div>
              <div class="stat-label">已完成订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="connection-card">
      <template #header>
        <div class="connection-header">
          <div>
            <strong>实时订单通知</strong>
            <span>新订单到达后将自动刷新并进行语音提醒</span>
          </div>
          <span :class="['connection-pill', wsConnected ? 'online' : 'offline']">
            {{ wsConnected ? '服务在线' : '连接中断' }}
          </span>
        </div>
      </template>
      <div class="notification-status">
        <div class="notification-copy" v-if="wsConnected">
          <div class="notification-icon online"><el-icon><CircleCheck /></el-icon></div>
          <div>
            <strong>订单通知通道已连接</strong>
            <p>后台将持续监听新订单，请保持当前页面开启</p>
          </div>
        </div>
        <div class="notification-copy" v-else>
          <div class="notification-icon offline"><el-icon><CircleClose /></el-icon></div>
          <div>
            <strong>订单通知通道未连接</strong>
            <p>请检查网络状态，或点击右侧按钮重新建立连接</p>
          </div>
          <el-button type="primary" @click="connectWebSocket">重新连接</el-button>
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
const currentDate = new Intl.DateTimeFormat('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}).format(new Date())
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
  const wsUrl = 'wss://bar.twst.work/api/ws/order/notification'
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
.welcome-panel {
  min-height: 132px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
  padding: 24px 28px;
  border: 1px solid rgba(232, 197, 71, 0.18);
  border-radius: 15px;
  background:
    radial-gradient(circle at 88% 10%, rgba(232, 197, 71, 0.13), transparent 260px),
    linear-gradient(135deg, #1b1c1f, #151619);
  box-shadow: 0 16px 38px rgba(0, 0, 0, 0.24);
}

.welcome-kicker {
  color: #a48d43;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 4px;
}

.welcome-panel h1 {
  margin: 8px 0 6px;
  color: #f3f3f4;
  font-size: 26px;
  font-weight: 650;
}

.welcome-panel p {
  color: #83868e;
  font-size: 13px;
}

.welcome-date {
  padding: 10px 16px;
  border: 1px solid rgba(232, 197, 71, 0.18);
  border-radius: 8px;
  background: rgba(232, 197, 71, 0.045);
  color: #c9b96f;
  font-size: 12px;
}

.stat-card {
  border-radius: 14px;
  transition: transform 0.18s ease, border-color 0.18s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  border-color: rgba(232, 197, 71, 0.22) !important;
}

.stat-content {
  display: flex;
  align-items: center;
  min-height: 76px;
}

.stat-icon {
  width: 58px;
  height: 58px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 23px;
  color: white;
}

.stat-icon.pending {
  background: linear-gradient(135deg, rgba(232, 197, 71, 0.24), rgba(232, 197, 71, 0.07));
  color: #f0d56e;
}

.stat-icon.to-ship {
  background: linear-gradient(135deg, rgba(83, 139, 225, 0.24), rgba(83, 139, 225, 0.07));
  color: #80adf0;
}

.stat-icon.shipped {
  background: linear-gradient(135deg, rgba(168, 126, 224, 0.24), rgba(168, 126, 224, 0.07));
  color: #bb93ed;
}

.stat-icon.completed {
  background: linear-gradient(135deg, rgba(95, 201, 139, 0.24), rgba(95, 201, 139, 0.07));
  color: #78d39d;
}

.stat-info {
  min-width: 0;
  margin-left: 16px;
}

.stat-value {
  color: #f3f3f4;
  font-size: 30px;
  font-weight: 700;
  font-variant-numeric: tabular-nums;
  line-height: 1;
}

.stat-value small {
  margin-left: 5px;
  color: #777a82;
  font-size: 11px;
  font-weight: 500;
}

.stat-label {
  margin-top: 9px;
  color: #8b8e96;
  font-size: 13px;
}

.connection-card {
  margin-top: 18px;
}

.connection-header {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.connection-header > div {
  display: flex;
  flex-direction: column;
}

.connection-header strong {
  color: #f3f3f4;
  font-size: 16px;
}

.connection-header span:not(.connection-pill) {
  margin-top: 5px;
  color: #777a82;
  font-size: 12px;
  font-weight: 400;
}

.connection-pill {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
}

.connection-pill.online {
  border: 1px solid rgba(95, 201, 139, 0.28);
  background: rgba(95, 201, 139, 0.09);
  color: #78d39d;
}

.connection-pill.offline {
  border: 1px solid rgba(237, 100, 107, 0.28);
  background: rgba(237, 100, 107, 0.09);
  color: #f1888e;
}

.notification-status {
  padding: 6px 0;
}

.notification-copy {
  display: flex;
  align-items: center;
}

.notification-copy > div:nth-child(2) {
  flex: 1;
}

.notification-copy strong {
  color: #d9dade;
  font-size: 14px;
}

.notification-copy p {
  margin-top: 5px;
  color: #777a82;
  font-size: 12px;
}

.notification-icon {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 13px;
  border-radius: 10px;
  font-size: 19px;
}

.notification-icon.online {
  background: rgba(95, 201, 139, 0.1);
  color: #78d39d;
}

.notification-icon.offline {
  background: rgba(237, 100, 107, 0.1);
  color: #f1888e;
}
</style>

