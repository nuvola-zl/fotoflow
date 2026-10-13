<<template>
  <div class="page-container-wide">
    <div class="page-header">
      <span class="page-title">骑手中心</span>
      <div class="header-right">
        <div class="status-badge-wrap" v-if="riderInfo">
          <div
              :class="['status-badge', statusClass]"
              @click.stop="showStatusMenu = !showStatusMenu"
          >
            <span class="status-dot"></span>
            <span class="status-text">{{ statusLabel }}</span>
            <span class="status-arrow" v-if="riderInfo.status !== 2">▾</span>
          </div>
          <div class="status-menu" v-if="showStatusMenu && riderInfo.status !== 2">
            <div
                :class="['status-option', { active: riderInfo.status === 0 }]"
                @click="switchStatus(0)"
            >
              <span class="status-dot dot-rest"></span> 休息
            </div>
            <div
                :class="['status-option', { active: riderInfo.status === 1 }]"
                @click="switchStatus(1)"
            >
              <span class="status-dot dot-work"></span> 上班
            </div>
          </div>
        </div>
        <button class="btn btn-outline btn-sm" style="color:var(--danger)" @click="handleLogout">退出</button>
      </div>
    </div>

    <!-- Rider info -->
    <div class="card" v-if="riderInfo">
      <div class="rider-info-row">
        <div class="rider-avatar">{{ riderInfo.realName?.charAt(0) || 'R' }}</div>
        <div class="rider-detail">
          <div class="rider-name">{{ riderInfo.realName || riderInfo.phone }}</div>
          <div class="rider-stats">
            <span>今日: {{ riderInfo.todayOrders || 0 }}单</span>
            <span>评分: {{ riderInfo.rating || 5.0 }}</span>
          </div>
        </div>
        <span :class="['status-text-badge', statusClass]">{{ statusLabel }}</span>
      </div>
    </div>

    <!-- 位置上报区域（上班或配送中都显示） -->
    <div class="card" v-if="riderInfo?.status === 1 || riderInfo?.status === 2">
      <h3 style="margin-bottom:8px;font-size:15px">位置上报</h3>

      <!-- 模式切换 -->
      <div class="location-mode-toggle">
        <button :class="['mode-btn', { active: locationMode === 'gps' }]" @click="switchMode('gps')">
          📍 GPS定位
        </button>
        <button :class="['mode-btn', { active: locationMode === 'map' }]" @click="switchMode('map')">
          🗺️ 地图选点
        </button>
      </div>

      <!-- GPS模式提示 -->
      <div v-if="locationMode === 'gps'" class="mode-hint">
        <span v-if="currentPosition.lng">
          当前位置: {{ currentPosition.lng.toFixed(6) }}, {{ currentPosition.lat.toFixed(6) }}
          <span class="update-time">(更新于 {{ lastGpsUpdate }})</span>
        </span>
        <span v-else>正在获取GPS位置...</span>
      </div>

      <!-- 地图 -->
      <div id="rider-map" style="width:100%;height:250px;border-radius:8px;background:#f5f5f5;margin-top:8px"></div>

      <!-- 手动上报按钮 -->
      <div class="location-actions">
        <button class="btn btn-primary" @click="reportLocation" :disabled="reporting || !currentPosition.lng">
          {{ reporting ? '上报中...' : '立即上报位置' }}
        </button>
        <span class="auto-hint" v-if="currentDeliveryOrderNo">
          自动上报: {{ autoReportCountdown }}分钟后
        </span>
      </div>
    </div>

    <!-- Tabs -->
    <div class="tabs">
      <button :class="['tab', { active: tab === 'pending' }]" @click="tab = 'pending'">待接单</button>
      <button :class="['tab', { active: tab === 'today' }]" @click="tab = 'today'">今日订单</button>
    </div>

    <!-- Pending orders -->
    <div v-if="tab === 'pending'">
      <div v-if="pendingList.length > 0">
        <div v-for="order in pendingList" :key="order.orderNo" class="order-card card">
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
            <span class="tag tag-primary">待接单</span>
          </div>
          <div class="order-body">
            <div class="order-row">收件人: {{ order.receiver }} | {{ order.phone }}</div>
            <div class="order-row">地址: {{ order.addressDetail }}</div>
            <div class="order-row">金额: ¥{{ (order.payAmount / 100).toFixed(2) }}</div>
            <div class="order-row">店铺: {{ order.shopName || '未知' }}</div>
          </div>
          <div class="order-map-mini" v-if="order.shopLongitude && order.buyerLongitude">
            <div :id="'mini-map-' + order.orderNo" style="width:100%;height:180px;border-radius:8px;background:#f5f5f5"></div>
          </div>
          <button class="btn btn-primary" style="width:100%;margin-top:10px" @click="handleTakeOrder(order.orderNo)">
            抢单
          </button>
        </div>
      </div>
      <div v-else-if="!loadingPending" class="empty-state">
        <div class="icon">📭</div>
        <div>暂无可接订单</div>
      </div>
      <div v-if="loadingPending" class="loading-spinner"><div class="spinner"></div></div>
    </div>

    <!-- Today orders -->
    <div v-if="tab === 'today'">
      <div v-if="todayList.length > 0">
        <div v-for="order in todayList" :key="order.orderNo" class="order-card card">
          <div class="order-header">
            <span class="order-no">订单号: {{ order.orderNo }}</span>
          </div>
          <div class="order-body">
            <div class="order-row">收件人: {{ order.receiver }} | {{ order.phone }}</div>
            <div class="order-row">地址: {{ order.addressDetail }}</div>
            <div class="order-row">金额: ¥{{ (order.payAmount / 100).toFixed(2) }}</div>
          </div>
          <div class="today-actions" v-if="order.status === 2">
            <button class="btn btn-primary btn-sm" @click="handleStartDelivery(order.orderNo)">开始配送</button>
          </div>
          <div class="today-actions" v-if="order.status === 3">
            <button class="btn btn-success btn-sm" @click="handleArrive(order.orderNo)">确认送达</button>
          </div>
        </div>
      </div>
      <div v-else-if="!loadingToday" class="empty-state">
        <div class="icon">📋</div>
        <div>今日暂无订单</div>
      </div>
      <div v-if="loadingToday" class="loading-spinner"><div class="spinner"></div></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch, inject, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import {
  getRiderInfo, updateRiderStatus, getPendingOrders, takeOrder,
  startRiderDelivery, arriveOrder, getTodayOrders, uploadLocation
} from '../api/rider'
import { loadBMap } from '../utils/bmap'

const router = useRouter()
const authStore = useAuthStore()
const toast = inject('toast', console.log)

const showStatusMenu = ref(false)

const statusLabel = computed(() => {
  if (!riderInfo.value) return ''
  if (riderInfo.value.status === 0) return '休息中'
  if (riderInfo.value.status === 1) return '上班'
  if (riderInfo.value.status === 2) return '配送中'
  return '未知'
})

const statusClass = computed(() => {
  if (!riderInfo.value) return ''
  if (riderInfo.value.status === 0) return 'status-rest'
  if (riderInfo.value.status === 1) return 'status-work'
  if (riderInfo.value.status === 2) return 'status-deliver'
  return ''
})

const tab = ref('pending')
const riderInfo = ref(null)
const pendingList = ref([])
const todayList = ref([])
const loadingPending = ref(false)
const loadingToday = ref(false)
const reporting = ref(false)

// 位置相关
const locationMode = ref('gps')  // 'gps' | 'map'
const currentPosition = ref({ lng: null, lat: null })
const lastGpsUpdate = ref('')
const currentDeliveryOrderNo = ref('')  // 当前配送中的订单号
const autoReportCountdown = ref(0)      // 自动上报倒计时（分钟）

let riderMap = null
let pollTimer = null
let geoWatchId = null
let locationPollTimer = null      // 自动上报定时器
let countdownTimer = null         // 倒计时显示定时器

/* ========== 页面生命周期 ========== */
onMounted(async () => {
  await loadRiderInfo()

  if (riderInfo.value?.status === 1) {
    await nextTick()
    await loadBMap()
    initRiderMap()
    loadPendingOrders()
    startGeoTracking()
  } else if (riderInfo.value?.status === 2) {
    tab.value = 'today'
    loadTodayOrders()
    // 恢复配送状态
    await nextTick()
    await loadBMap()
    initRiderMap()
    startGeoTracking()
    // 查找配送中的订单
    const deliveringOrder = todayList.value.find(o => o.status === 3)
    if (deliveringOrder) {
      currentDeliveryOrderNo.value = deliveringOrder.orderNo
      startAutoReport(deliveringOrder.orderNo)
    }
  }

  pollTimer = setInterval(loadRiderInfo, 30000)
  document.addEventListener('click', onDocumentClick)
})

onUnmounted(() => {
  clearInterval(pollTimer)
  clearInterval(locationPollTimer)
  clearInterval(countdownTimer)
  if (geoWatchId) navigator.geolocation?.clearWatch(geoWatchId)
  document.removeEventListener('click', onDocumentClick)
})

function onDocumentClick() {
  showStatusMenu.value = false
}

/* ========== 加载骑手信息 ========== */
async function loadRiderInfo() {
  try {
    const res = await getRiderInfo()
    riderInfo.value = { ...(riderInfo.value || authStore.rider), ...res.data }
    authStore.updateRider(res.data)
  } catch (e) {
    if (!riderInfo.value) alert(e.response?.data?.message || e.message || '获取骑手信息失败')
  }
}

/* ========== 监听状态变化 ========== */
watch(() => riderInfo.value?.status, async (status, oldStatus) => {
  if (status === 1 && oldStatus !== 1) {
    await nextTick()
    await loadBMap()
    initRiderMap()
    loadPendingOrders()
    startGeoTracking()
  }
  if (status === 2 && tab.value === 'pending') {
    tab.value = 'today'
    pendingList.value = []
    loadTodayOrders()
  }
})

/* ========== 切换定位模式 ========== */
function switchMode(mode) {
  locationMode.value = mode
  if (mode === 'gps') {
    toast('已切换到GPS定位模式')
    // 立即获取一次最新位置
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
          (pos) => {
            currentPosition.value = {
              lng: Number(pos.coords.longitude.toFixed(6)),
              lat: Number(pos.coords.latitude.toFixed(6))
            }
            lastGpsUpdate.value = new Date().toLocaleTimeString('zh-CN')
            updateMapMarker(currentPosition.value.lng, currentPosition.value.lat)
          },
          (err) => toast('GPS获取失败: ' + err.message)
      )
    }
  } else {
    toast('已切换到地图选点模式，请点击地图选择位置')
  }
}

/* ========== 初始化地图 ========== */
function initRiderMap() {
  const el = document.getElementById('rider-map')
  if (!el || !window.BMap || riderMap) return

  riderMap = new window.BMap.Map('rider-map')
  const point = new window.BMap.Point(
      currentPosition.value.lng || 116.4,
      currentPosition.value.lat || 39.9
  )
  riderMap.centerAndZoom(point, 16)
  riderMap.enableScrollWheelZoom(true)

  // 地图点击事件
  riderMap.addEventListener('click', (e) => {
    if (locationMode.value !== 'map') {
      toast('请先切换到"地图选点"模式')
      return
    }
    currentPosition.value = {
      lng: Number(e.point.lng.toFixed(6)),
      lat: Number(e.point.lat.toFixed(6))
    }
    updateMapMarker(currentPosition.value.lng, currentPosition.value.lat)
    toast('已选择地图位置，点击"立即上报位置"按钮上传')
  })
}

/* ========== 更新地图标记 ========== */
function updateMapMarker(lng, lat) {
  if (!riderMap || !lng || !lat) return
  riderMap.clearOverlays()
  const marker = new window.BMap.Marker(new window.BMap.Point(lng, lat))
  riderMap.addOverlay(marker)
  riderMap.panTo(new window.BMap.Point(lng, lat))
}

/* ========== GPS追踪 ========== */
function startGeoTracking() {
  if (!navigator.geolocation) {
    toast('设备不支持GPS，已自动切换地图模式')
    locationMode.value = 'map'
    return
  }

  geoWatchId = navigator.geolocation.watchPosition(
      (pos) => {
        currentPosition.value = {
          lng: Number(pos.coords.longitude.toFixed(6)),
          lat: Number(pos.coords.latitude.toFixed(6))
        }
        lastGpsUpdate.value = new Date().toLocaleTimeString('zh-CN')

        // GPS模式下自动更新地图标记
        if (locationMode.value === 'gps') {
          updateMapMarker(currentPosition.value.lng, currentPosition.value.lat)
        }
      },
      (err) => {
        console.error('GPS error:', err)
        if (locationMode.value === 'gps') {
          toast('GPS信号弱，建议切换地图模式')
        }
      },
      { enableHighAccuracy: true, maximumAge: 60000, timeout: 30000 }
  )
}

/* ========== 切换上班/休息状态 ========== */
async function switchStatus(newStatus) {
  if (!riderInfo.value) return
  if (riderInfo.value.status === 2) {
    toast('配送中不能切换状态，请先完成当前订单')
    return
  }
  try {
    await updateRiderStatus({ status: newStatus })
    riderInfo.value.status = newStatus
    authStore.updateRider({ status: newStatus })
    showStatusMenu.value = false
    toast(newStatus === 1 ? '已上班' : '已休息')
  } catch (e) {
    alert(e.response?.data?.message || e.message || '切换状态失败')
  }
}

/* ========== 手动上报位置 ========== */
async function reportLocation() {
  if (!currentPosition.value.lng || !currentPosition.value.lat) {
    alert('请先获取位置（GPS自动定位或地图点击选点）')
    return
  }

  const orderNo = currentDeliveryOrderNo.value || ''

  reporting.value = true
  try {
    await uploadLocation({
      orderNo: orderNo,
      longitude: currentPosition.value.lng,
      latitude: currentPosition.value.lat
    })
    toast('位置上报成功' + (orderNo ? '（订单: ' + orderNo + '）' : ''))

    // 如果是配送中，重置自动上报倒计时
    if (currentDeliveryOrderNo.value) {
      startAutoReport(currentDeliveryOrderNo.value)
    }
  } catch (e) {
    alert(e.response?.data?.message || e.message || '上报失败')
  } finally {
    reporting.value = false
  }
}

/* ========== 自动上报（1小时一次） ========== */
function startAutoReport(orderNo) {
  stopAutoReport()
  currentDeliveryOrderNo.value = orderNo

  // 立即上报一次
  reportOrderLocation(orderNo)

  // 设置1小时（60分钟）定时器
  autoReportCountdown.value = 60
  locationPollTimer = setInterval(() => {
    reportOrderLocation(orderNo)
    autoReportCountdown.value = 60
  }, 3600000) // 60分钟 = 3600000毫秒

  // 倒计时显示（每分钟更新）
  countdownTimer = setInterval(() => {
    autoReportCountdown.value--
    if (autoReportCountdown.value < 0) autoReportCountdown.value = 0
  }, 60000)
}

function stopAutoReport() {
  if (locationPollTimer) {
    clearInterval(locationPollTimer)
    locationPollTimer = null
  }
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  currentDeliveryOrderNo.value = ''
  autoReportCountdown.value = 0
}

async function reportOrderLocation(orderNo) {
  if (!currentPosition.value.lng || !currentPosition.value.lat) return
  try {
    await uploadLocation({
      orderNo: orderNo,
      longitude: currentPosition.value.lng,
      latitude: currentPosition.value.lat
    })
    console.log('自动上报位置成功', orderNo, currentPosition.value)
  } catch (e) {
    console.error('自动上报位置失败:', e.message)
  }
}

/* ========== 加载待接单池 ========== */
async function loadPendingOrders() {
  if (riderInfo.value?.status !== 1) return
  loadingPending.value = true
  try {
    const res = await getPendingOrders()
    pendingList.value = res.data || []
    await nextTick()
    pendingList.value.forEach(order => initMiniMap(order))
  } catch (e) {
    const msg = e.response?.data?.message || e.message || '加载失败'
    if (!msg.includes('请先上班') && !msg.includes('配送中')) {
      alert('加载待接单失败: ' + msg)
    }
  } finally {
    loadingPending.value = false
  }
}

function initMiniMap(order) {
  const el = document.getElementById('mini-map-' + order.orderNo)
  if (!el || !window.BMap) return

  const map = new window.BMap.Map('mini-map-' + order.orderNo)

  if (order.shopLongitude && order.shopLatitude &&
      order.buyerLongitude && order.buyerLatitude) {

    const startPoint = new window.BMap.Point(order.shopLongitude, order.shopLatitude)
    const endPoint = new window.BMap.Point(order.buyerLongitude, order.buyerLatitude)

    // 添加起点（店铺）和终点（收货）标记
    const startMarker = new window.BMap.Marker(startPoint)
    startMarker.setLabel(new window.BMap.Label('取货', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(startMarker)

    const endMarker = new window.BMap.Marker(endPoint)
    endMarker.setLabel(new window.BMap.Label('送货', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(endMarker)

    // 【关键】使用驾车路线规划，而不是直线
    const driving = new window.BMap.DrivingRoute(map, {
      renderOptions: { map: map, autoViewport: true },
      policy: window.BMAP_DRIVING_POLICY_LEAST_TIME  // 最快路线
    })
    driving.search(startPoint, endPoint)

  } else if (order.shopLongitude && order.shopLatitude) {
    // 只有店铺位置
    const point = new window.BMap.Point(order.shopLongitude, order.shopLatitude)
    map.centerAndZoom(point, 15)
    const marker = new window.BMap.Marker(point)
    marker.setLabel(new window.BMap.Label('店铺', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(marker)
  }
}

/* ========== 抢单 ========== */
async function handleTakeOrder(orderNo) {
  try {
    await takeOrder(orderNo)
    toast('抢单成功')
    await loadRiderInfo()
    pendingList.value = []
    tab.value = 'today'
    await loadTodayOrders()
  } catch (e) {
    alert(e.response?.data?.message || e.message || '抢单失败')
  }
}

/* ========== 开始配送 ========== */
async function handleStartDelivery(orderNo) {
  try {
    await startRiderDelivery(orderNo)
    toast('已开始配送')
    currentDeliveryOrderNo.value = orderNo
    startAutoReport(orderNo)
    await loadTodayOrders()
  } catch (e) {
    alert(e.response?.data?.message || e.message || '操作失败')
  }
}

/* ========== 确认送达 ========== */
async function handleArrive(orderNo) {
  try {
    await arriveOrder(orderNo)
    toast('已确认送达')
    stopAutoReport()
    await loadRiderInfo()
    await loadTodayOrders()
  } catch (e) {
    alert(e.response?.data?.message || e.message || '操作失败')
  }
}

/* ========== 加载今日订单 ========== */
async function loadTodayOrders() {
  loadingToday.value = true
  try {
    const res = await getTodayOrders(1, 50)
    todayList.value = res.data?.records || []
  } catch (e) {
    alert(e.response?.data?.message || e.message || '加载今日订单失败')
  } finally {
    loadingToday.value = false
  }
}

/* ========== 退出登录 ========== */
function handleLogout() {
  authStore.logoutRider()
  router.push('/rider/login')
}

watch(tab, (val) => {
  if (val === 'today') loadTodayOrders()
})
</script>

<style scoped>
.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

:deep(.BMap_label) {
  background: rgba(0,0,0,0.7) !important;
  color: #fff !important;
  border: none !important;
  border-radius: 4px !important;
  padding: 2px 6px !important;
  font-size: 12px !important;
}

.status-badge-wrap {
  position: relative;
}

.status-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.status-rest { background: #f0f0f0; color: #999; }
.status-work { background: #e6f7e6; color: #07c160; }
.status-deliver { background: #e6f0ff; color: #2979ff; }

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: currentColor;
}

.status-arrow { font-size: 10px; }

.status-menu {
  position: absolute;
  top: 110%;
  right: 0;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  z-index: 200;
  min-width: 100px;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.15s;
}

.status-option:hover { background: #f5f5f5; }
.status-option.active { color: var(--primary); font-weight: 600; }

.dot-rest { background: #999; }
.dot-work { background: #07c160; }

.status-text-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.rider-info-row {
  display: flex;
  align-items: center;
  gap: 14px;
}

.rider-avatar {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f5a623, #f76b1c);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 600;
  flex-shrink: 0;
}

.rider-detail { flex: 1; }
.rider-name { font-size: 18px; font-weight: 600; }

.rider-stats {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: var(--text-light);
  margin-top: 4px;
}

/* 位置模式切换 */
.location-mode-toggle {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.mode-btn {
  flex: 1;
  padding: 8px;
  border: 1.5px solid var(--border);
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
  text-align: center;
  transition: all 0.2s;
}

.mode-btn.active {
  border-color: var(--primary);
  background: rgba(254,44,85,0.05);
  color: var(--primary);
}

.mode-hint {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.update-time {
  color: var(--text-secondary);
  font-size: 11px;
}

.location-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
}

.auto-hint {
  font-size: 12px;
  color: var(--text-light);
}

.tabs {
  display: flex;
  background: #fff;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}

.tab {
  flex: 1;
  padding: 10px;
  border: none;
  background: transparent;
  border-radius: 10px;
  font-size: 15px;
  cursor: pointer;
  color: var(--text-secondary);
  transition: all 0.2s;
}

.tab.active {
  background: var(--primary);
  color: #fff;
  font-weight: 500;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.order-no { font-size: 13px; color: var(--text-light); }
.order-body { margin: 6px 0; }

.order-row {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.8;
}

.order-map-mini { margin-top: 8px; }

.today-actions {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
</style>