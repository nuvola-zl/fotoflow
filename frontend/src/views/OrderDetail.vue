<<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/orders" class="back-btn">← 返回</router-link>
      <span class="page-title">订单详情</span>
      <span></span>
    </div>

    <div v-if="order">
      <!-- Status -->
      <div class="card">
        <div class="status-line">
          <span class="status-badge" :class="statusBadgeClass(order.status)">
            {{ order.statusDesc }}
          </span>
        </div>
        <div class="order-no-text">订单号: {{ order.orderNo }}</div>
        <div class="time-line">
          <div v-if="order.createTime">下单: {{ formatTime(order.createTime) }}</div>
          <div v-if="order.payTime">支付: {{ formatTime(order.payTime) }}</div>
          <div v-if="order.deliveryTime">发货: {{ formatTime(order.deliveryTime) }}</div>
          <div v-if="order.receiveTime">签收: {{ formatTime(order.receiveTime) }}</div>
        </div>
      </div>

      <!-- Rider info -->
      <div class="card" v-if="order.riderId">
        <h3>骑手信息</h3>
        <div>姓名: {{ order.riderName }}</div>
        <div>电话: {{ order.riderPhone }}</div>
        <div v-if="order.riderTakeTime">接单时间: {{ formatTime(order.riderTakeTime) }}</div>
      </div>

      <!-- 【新增】配送进度 -->
      <div class="card" v-if="order.status === 3 && order.riderId">
        <h3>配送进度</h3>
        <div class="delivery-progress">
          <div class="progress-row" v-if="riderLocation">
            <span>预计送达</span>
            <span class="highlight">{{ riderLocation.estimatedMinutes || '计算中' }} 分钟</span>
          </div>
          <div class="progress-row" v-if="riderLocation">
            <span>位置更新</span>
            <span>{{ formatTime(riderLocation.locationTime) || '刚刚' }}</span>
          </div>
          <div class="progress-row" v-else>
            <span>骑手位置</span>
            <span class="text-light">等待骑手更新位置...</span>
          </div>
        </div>
      </div>

      <!-- Map -->
      <div class="card" v-if="order.shopLatitude && order.buyerLatitude">
        <h3 style="margin-bottom:8px">配送地图</h3>
        <div id="order-map" style="width:100%;height:260px;border-radius:8px;background:#f5f5f5"></div>
        <div class="map-legend">
          <span>📍 店铺</span>
          <span>🏍️ 骑手</span>
          <span>🏠 买家</span>
        </div>
      </div>

      <!-- Address -->
      <div class="card">
        <h3>收货信息</h3>
        <div>收件人: {{ order.receiver }}</div>
        <div>电话: {{ order.phone }}</div>
        <div>地址: {{ order.addressDetail }}</div>
      </div>

      <!-- Items -->
      <div class="card">
        <h3>商品明细</h3>
        <div v-for="item in order.items" :key="item.goodsId" class="detail-item">
          <img v-if="item.goodsImg" :src="item.goodsImg" class="detail-img" />
          <span v-else class="detail-img-placeholder">📦</span>
          <div class="detail-info">
            <div>{{ item.goodsName }}</div>
            <div class="detail-price">¥{{ (item.price / 100).toFixed(2) }} x {{ item.quantity }}</div>
          </div>
          <div class="detail-subtotal">¥{{ (item.subtotal / 100).toFixed(2) }}</div>
        </div>
      </div>

      <!-- Amount -->
      <div class="card">
        <div class="amount-row">
          <span>商品金额</span><span>¥{{ (order.totalAmount / 100).toFixed(2) }}</span>
        </div>
        <div class="amount-row" v-if="order.freightAmount">
          <span>运费</span><span>¥{{ (order.freightAmount / 100).toFixed(2) }}</span>
        </div>
        <div class="amount-row" v-if="order.discountAmount">
          <span>优惠</span><span>-¥{{ (order.discountAmount / 100).toFixed(2) }}</span>
        </div>
        <div class="amount-row total">
          <span>实付</span><span>¥{{ (order.payAmount / 100).toFixed(2) }}</span>
        </div>
      </div>

      <!-- Actions -->
      <div class="action-bar">
        <button v-if="order.status === 0" class="btn btn-primary" @click="handlePay">模拟支付</button>
        <button v-if="order.status === 2 || order.status === 3" class="btn btn-success" @click="handleReceive">确认收货</button>
        <button v-if="order.status === 0" class="btn btn-outline" @click="handleCancel">取消订单</button>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state"><div>订单不存在</div></div>
    <div v-else class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, payOrder, confirmReceive, cancelOrder } from '../api/order'
import { getRiderLocation } from '../api/order'
import { loadBMap } from '../utils/bmap'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)
const riderLocation = ref(null)

let map = null
let riderMarker = null
let locationPollTimer = null

onMounted(async () => {
  try {
    const res = await getOrderDetail(route.params.orderNo)
    order.value = res.data
    if (order.value) {
      await nextTick()
      await loadBMap()
      initMap()

      // 配送中且已绑定骑手，启动位置轮询
      if (order.value.status === 3 && order.value.riderId) {
        loadRiderLocation()
        locationPollTimer = setInterval(loadRiderLocation, 15000)
      }
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

onUnmounted(() => {
  if (locationPollTimer) {
    clearInterval(locationPollTimer)
    locationPollTimer = null
  }
})

function initMap() {
  const el = document.getElementById('order-map')
  if (!el || !window.BMap) return

  map = new window.BMap.Map('order-map')
  map.enableScrollWheelZoom(true)

  const points = []

  // 店铺标记
  if (order.value.shopLongitude && order.value.shopLatitude) {
    const shopPoint = new window.BMap.Point(order.value.shopLongitude, order.value.shopLatitude)
    points.push(shopPoint)
    const shopMarker = new window.BMap.Marker(shopPoint)
    shopMarker.setLabel(new window.BMap.Label('店铺', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(shopMarker)
  }

  // 收货标记
  if (order.value.buyerLongitude && order.value.buyerLatitude) {
    const buyerPoint = new window.BMap.Point(order.value.buyerLongitude, order.value.buyerLatitude)
    points.push(buyerPoint)
    const buyerMarker = new window.BMap.Marker(buyerPoint)
    buyerMarker.setLabel(new window.BMap.Label('收货地址', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(buyerMarker)
  }

  // 自动调整视野
  if (points.length > 0) {
    if (points.length === 1) {
      map.centerAndZoom(points[0], 15)
    } else {
      map.setViewport(points, { zoomFactor: -1 })
    }
  } else {
    map.centerAndZoom(new window.BMap.Point(116.4, 39.9), 12)
  }

  // 配送路线（虚线）
  if (order.value.shopLongitude && order.value.shopLatitude &&
      order.value.buyerLongitude && order.value.buyerLatitude) {
    const polyline = new window.BMap.Polyline([
      new window.BMap.Point(order.value.shopLongitude, order.value.shopLatitude),
      new window.BMap.Point(order.value.buyerLongitude, order.value.buyerLatitude)
    ], {
      strokeColor: '#1890ff',
      strokeWeight: 2,
      strokeOpacity: 0.4,
      strokeStyle: 'dashed'
    })
    map.addOverlay(polyline)
  }
}

// 加载骑手位置
async function loadRiderLocation() {
  if (!order.value?.orderNo) return
  try {
    const res = await getRiderLocation(order.value.orderNo)
    if (res.data) {
      riderLocation.value = res.data
      updateRiderMarker(res.data.longitude, res.data.latitude)
    }
  } catch (e) {
    console.error('获取骑手位置失败:', e.message)
  }
}

// 更新骑手标记（平滑移动）
function updateRiderMarker(lng, lat) {
  if (!map || !lng || !lat) return

  const newPoint = new window.BMap.Point(lng, lat)

  if (riderMarker) {
    riderMarker.setPosition(newPoint)
  } else {
    riderMarker = new window.BMap.Marker(newPoint)
    const riderIcon = new window.BMap.Icon(
        'https://api.map.baidu.com/images/marker_red_sprite.png',
        new window.BMap.Size(25, 25),
        { anchor: new window.BMap.Size(12, 25) }
    )
    riderMarker.setIcon(riderIcon)
    riderMarker.setLabel(new window.BMap.Label('骑手', { offset: new window.BMap.Size(15, -10) }))
    map.addOverlay(riderMarker)
  }
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  return d.toLocaleString('zh-CN')
}

async function handlePay() {
  try {
    await payOrder({ orderNo: order.value.orderNo, payType: 1 })
    alert('支付成功')
    const res = await getOrderDetail(order.value.orderNo)
    order.value = res.data
  } catch (e) {
    alert(e.message)
  }
}

async function handleReceive() {
  try {
    await confirmReceive(order.value.orderNo)
    alert('已确认收货')
    const res = await getOrderDetail(order.value.orderNo)
    order.value = res.data
  } catch (e) {
    alert(e.message)
  }
}

async function handleCancel() {
  if (!confirm('确认取消订单？')) return
  try {
    await cancelOrder(order.value.orderNo)
    alert('订单已取消')
    const res = await getOrderDetail(order.value.orderNo)
    order.value = res.data
  } catch (e) {
    alert(e.message)
  }
}

function statusBadgeClass(status) {
  if (status === 0) return 'bg-warning'
  if (status === 4) return 'bg-success'
  if (status === 5) return 'bg-gray'
  return 'bg-primary'
}
</script>

<style scoped>
.status-line { margin-bottom: 8px; }
.status-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #fff;
}
.bg-primary { background: var(--primary); }
.bg-warning { background: var(--warning); }
.bg-success { background: var(--success); }
.bg-gray { background: #999; }
.order-no-text { font-size: 13px; color: var(--text-light); margin-bottom: 8px; }
.time-line { font-size: 13px; color: var(--text-secondary); line-height: 1.8; }

/* 覆盖百度地图标签默认样式 */
:deep(.BMap_label) {
  background: rgba(0,0,0,0.7) !important;
  color: #fff !important;
  border: none !important;
  border-radius: 4px !important;
  padding: 2px 6px !important;
  font-size: 12px !important;
  white-space: nowrap !important;
}

/* 【新增】配送进度样式 */
.delivery-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.progress-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: var(--text-secondary);
}
.progress-row .highlight {
  color: var(--primary);
  font-weight: 600;
}
.progress-row .text-light {
  color: var(--text-light);
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.detail-item:last-child { border-bottom: none; }
.detail-img { width: 48px; height: 48px; border-radius: 6px; object-fit: cover; }
.detail-img-placeholder {
  width: 48px; height: 48px;
  border-radius: 6px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-info { flex: 1; font-size: 14px; }
.detail-price { font-size: 13px; color: var(--text-light); margin-top: 2px; }
.detail-subtotal { font-size: 14px; font-weight: 500; color: var(--primary); }
.amount-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  padding: 4px 0;
}
.amount-row.total {
  font-size: 17px;
  font-weight: 700;
  border-top: 1px solid #eee;
  padding-top: 10px;
  margin-top: 4px;
}
.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  justify-content: center;
}
.map-legend {
  display: flex;
  gap: 16px;
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-light);
}
h3 { font-size: 15px; margin-bottom: 6px; }
</style>