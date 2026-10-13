<<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/goods" class="back-btn">← 返回</router-link>
      <span class="page-title">商品详情</span>
      <span></span>
    </div>

    <div v-if="goods">
      <div class="goods-img-wrap">
        <img v-if="goods.goodsImg" :src="goods.goodsImg" :alt="goods.goodsName" />
        <span v-else class="no-img">暂无图片</span>
      </div>

      <div class="card">
        <h2 class="name">{{ goods.goodsName }}</h2>
        <div class="price-row">
          <span class="price">¥{{ (goods.price / 100).toFixed(2) }}</span>
          <span class="unit">/{{ goods.unit }}</span>
          <span class="stock" v-if="goods.stock !== undefined">库存: {{ goods.stock }}</span>
        </div>
        <div class="sales" v-if="goods.sales">已售 {{ goods.sales }} 件</div>
        <div class="desc" v-if="goods.content">{{ goods.content }}</div>
      </div>

      <div class="action-bar">
        <button class="btn btn-outline btn-sm" @click="handleAddCart" :disabled="adding">加入购物车</button>
        <button class="btn btn-primary" @click="openBuyModal">立即购买</button>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state"><div>商品不存在</div></div>
    <div v-else class="loading-spinner"><div class="spinner"></div></div>

    <!-- ========== 确认订单弹窗（buyNow） ========== -->
    <div class="modal-mask" v-if="showBuy" @click.self="showBuy = false">
      <div class="modal-card">
        <h3>确认订单信息</h3>

        <!-- 商品摘要 -->
        <div class="order-goods-info">
          <span class="order-name">{{ goods?.goodsName }}</span>
          <span class="order-price">¥{{ (goods?.price / 100).toFixed(2) }}</span>
        </div>

        <!-- 数量 -->
        <div class="form-group">
          <label>购买数量</label>
          <input v-model.number="buyQuantity" class="form-input" type="number" min="1" :max="goods?.stock" />
        </div>

        <!-- 地址选择区 -->
        <div class="address-section">
          <div class="section-title">收货地址</div>

          <!-- 已有地址列表 -->
          <div v-if="addressList.length > 0 && !useManualAddress" class="address-list">
            <div
                v-for="addr in addressList"
                :key="addr.id"
                :class="['address-card', { active: selectedAddressId === addr.id }]"
                @click="selectedAddressId = addr.id"
            >
              <div class="addr-header">
                <span class="addr-tag" v-if="addr.tag">{{ addr.tag }}</span>
                <span class="addr-default-badge" v-if="addr.isDefault">默认</span>
              </div>
              <div class="addr-info">
                <span class="addr-name">{{ addr.receiver }}</span>
                <span class="addr-phone">{{ addr.phone }}</span>
              </div>
              <div class="addr-detail">{{ addr.addressDetail }}</div>
            </div>
          </div>

          <!-- 手动输入 -->
          <div v-if="useManualAddress" class="manual-address-form">
            <div class="form-group">
              <label>在地图上选择位置</label>
              <div id="buy-map" style="width:100%;height:180px;border-radius:8px;background:#f5f5f5;margin-bottom:4px"></div>
              <div class="map-hint">点击地图自动填充详细地址和经纬度</div>
            </div>

            <div class="form-row" v-if="manualLongitude && manualLatitude">
              <div class="form-group" style="flex:1">
                <label>经度</label>
                <input :value="manualLongitude" class="form-input" readonly />
              </div>
              <div class="form-group" style="flex:1">
                <label>纬度</label>
                <input :value="manualLatitude" class="form-input" readonly />
              </div>
            </div>

            <div class="form-group">
              <label>收件人 *</label>
              <input v-model="manualReceiver" class="form-input" placeholder="请输入收件人" />
            </div>
            <div class="form-group">
              <label>电话 *</label>
              <input v-model="manualPhone" class="form-input" placeholder="请输入电话" />
            </div>
            <div class="form-group">
              <label>详细地址 *</label>
              <textarea v-model="manualAddressDetail" class="form-input" rows="2" placeholder="点击地图自动填充，或手动输入"></textarea>
            </div>
          </div>

          <!-- 切换按钮 -->
          <div class="address-toggle">
            <button v-if="!useManualAddress" class="text-btn" @click="useManualAddress = true">
              + 使用新地址
            </button>
            <button v-else class="text-btn" @click="useManualAddress = false">
              ← 选择已有地址
            </button>
          </div>
        </div>

        <!-- 金额 -->
        <div class="total-row">
          <span>合计：</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>

        <div class="modal-actions">
          <button class="btn btn-outline" @click="showBuy = false">取消</button>
          <button class="btn btn-primary" @click="handleBuyNow" :disabled="buying">
            {{ buying ? '提交中...' : '提交订单' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ========== 支付弹窗（pay） ========== -->
    <div class="modal-mask" v-if="showPay" @click.self="showPay = false">
      <div class="modal-card">
        <h3>确认支付</h3>
        <div class="pay-info">
          <div class="pay-amount">¥{{ totalPrice }}</div>
          <div class="pay-order">订单号：{{ currentOrderNo }}</div>
        </div>
        <div class="form-group">
          <label>支付方式</label>
          <div class="pay-type-select">
            <button :class="['pay-type-btn', { active: payType === 1 }]" @click="payType = 1">微信支付</button>
            <button :class="['pay-type-btn', { active: payType === 2 }]" @click="payType = 2">支付宝</button>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-outline" @click="showPay = false">稍后支付</button>
          <button class="btn btn-primary" @click="handlePay" :disabled="paying">
            {{ paying ? '支付中...' : '确认支付' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, computed, nextTick, watch} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {getGoodsDetail} from '../api/goods'
import {addToCart} from '../api/cart'
import {buyNow, payOrder} from '../api/order'
import {getAddressList, addAddress} from '../api/address'
import {loadBMap} from '../utils/bmap'
import {useAuthStore} from '../stores/auth'
import { getLatestLocation } from '../api/rider'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const goods = ref(null)
const loading = ref(true)
const adding = ref(false)

/* ========== 购买弹窗状态 ========== */
const showBuy = ref(false)
const buyQuantity = ref(1)
const buying = ref(false)

const riderLocation = ref(null)
let locationPollTimer = null

/* ========== 地址状态 ========== */
const addressList = ref([])
const selectedAddressId = ref(null)
const useManualAddress = ref(false)
const manualReceiver = ref('')
const manualPhone = ref('')
const manualAddressDetail = ref('')
const manualLongitude = ref(null)
const manualLatitude = ref(null)

/* ========== 地图实例（手动地址模式用） ========== */
let buyMap = null
let buyGeocoder = null

/* ========== 支付状态 ========== */
const showPay = ref(false)
const currentOrderNo = ref('')
const payType = ref(1)   // 1=微信, 2=支付宝
const paying = ref(false)

/* ========== 计算属性 ========== */
const selectedAddress = computed(() =>
    addressList.value.find(a => a.id === selectedAddressId.value)
)

const totalPrice = computed(() => {
  if (!goods.value) return '0.00'
  return (goods.value.price * buyQuantity.value / 100).toFixed(2)
})

/* ========== 初始化 ========== */
onMounted(async () => {
  try {
    const res = await getGoodsDetail(route.params.id)
    goods.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }

  if (authStore.isLoggedIn) {
    try {
      const res = await getAddressList()
      addressList.value = res.data || []
      const defaultAddr = addressList.value.find(a => a.isDefault === 1)
      if (defaultAddr) {
        selectedAddressId.value = defaultAddr.id
      } else if (addressList.value.length > 0) {
        selectedAddressId.value = addressList.value[0].id
      }
    } catch (e) {
      console.error('加载地址失败', e)
    }
  }
})

/* ========== 监听：切到"新地址"时初始化地图 ========== */
watch(useManualAddress, async (val) => {
  if (val) {
    await initBuyMap()
  }
})

/* ========== 初始化购买弹窗地图 ========== */
async function initBuyMap() {
  await nextTick()
  if (!window.BMap) {
    try {
      await loadBMap()
    } catch (e) {
      alert('地图加载失败，请检查网络或 AK 配置')
      return
    }
  }

  const el = document.getElementById('buy-map')
  if (!el) return

  // 如果已经初始化过，只刷新中心点
  if (buyMap) {
    const lng = manualLongitude.value || 116.4
    const lat = manualLatitude.value || 39.9
    buyMap.centerAndZoom(new window.BMap.Point(lng, lat), 15)
    return
  }

  const centerLng = manualLongitude.value || 116.4
  const centerLat = manualLatitude.value || 39.9
  buyMap = new window.BMap.Map('buy-map')
  const point = new window.BMap.Point(centerLng, centerLat)
  buyMap.centerAndZoom(point, 15)
  buyMap.enableScrollWheelZoom(true)

  buyGeocoder = new window.BMap.Geocoder()

  if (manualLongitude.value && manualLatitude.value) {
    const marker = new window.BMap.Marker(
        new window.BMap.Point(manualLongitude.value, manualLatitude.value)
    )
    buyMap.addOverlay(marker)
  }

  buyMap.addEventListener('click', (e) => {
    const lng = e.point.lng
    const lat = e.point.lat
    manualLongitude.value = Number(lng.toFixed(6))
    manualLatitude.value = Number(lat.toFixed(6))

    buyMap.clearOverlays()
    const marker = new window.BMap.Marker(new window.BMap.Point(lng, lat))
    buyMap.addOverlay(marker)

    buyGeocoder.getLocation(new window.BMap.Point(lng, lat), (result) => {
      if (result && result.address) {
        manualAddressDetail.value = result.address
      }
    })
  })
}

/* ========== 打开购买弹窗 ========== */
function openBuyModal() {
  if (!authStore.isLoggedIn) {
    alert('请先登录')
    return
  }
  if (!goods.value || goods.value.stock <= 0) {
    alert('商品库存不足')
    return
  }

  showBuy.value = true
  buyQuantity.value = 1
  useManualAddress.value = false
  manualReceiver.value = ''
  manualPhone.value = ''
  manualAddressDetail.value = ''
  manualLongitude.value = null
  manualLatitude.value = null
  buyMap = null
  buyGeocoder = null

  if (addressList.value.length === 0) {
    useManualAddress.value = true   // 无地址时自动展开手动+地图模式
  }
}

/* ========== 提交预订单（buyNow） ========== */
async function handleBuyNow() {
  if (!goods.value) return

  if (!buyQuantity.value || buyQuantity.value <= 0) {
    buyQuantity.value = 1
  }
  if (goods.value.stock < buyQuantity.value) {
    alert('库存不足')
    return
  }

  let payload = {
    goodsId: goods.value.id,
    quantity: buyQuantity.value
  }

  if (useManualAddress.value) {
    if (!manualReceiver.value || !manualPhone.value || !manualAddressDetail.value) {
      alert('请填写完整的收货信息')
      return
    }

    buying.value = true
    try {
      // 【闭环1】先保存新地址到地址簿，并强制设为默认
      const addrRes = await addAddress({
        receiver: manualReceiver.value,
        phone: manualPhone.value,
        addressDetail: manualAddressDetail.value,
        longitude: manualLongitude.value,
        latitude: manualLatitude.value,
        isDefault: 1,   // ← 设为默认，后端会自动取消其他默认
        tag: ''
      })

      // 【闭环2】用刚保存的 addressId 下单（走后端优先级1）
      payload.addressId = addrRes.data.id
    } catch (e) {
      alert('保存地址失败：' + (e.message || '未知错误'))
      buying.value = false
      return
    }
  } else {
    if (!selectedAddressId.value) {
      alert('请选择收货地址')
      return
    }
    payload.addressId = selectedAddressId.value
  }

  try {
    const res = await buyNow(payload)
    currentOrderNo.value = res.data.orderNo
    showBuy.value = false
    showPay.value = true
  } catch (e) {
    alert(e.message || '下单失败')
  } finally {
    buying.value = false
  }
}

/* ========== 确认支付（pay） ========== */
async function handlePay() {
  if (!currentOrderNo.value) return

  paying.value = true
  try {
    await payOrder({
      orderNo: currentOrderNo.value,
      payType: payType.value
    })
    alert('支付成功')
    showPay.value = false
    router.push('/orders/' + currentOrderNo.value)
  } catch (e) {
    alert(e.message || '支付失败')
  } finally {
    paying.value = false
  }
}

/* ========== 加入购物车 ========== */
async function handleAddCart() {
  if (!authStore.isLoggedIn) {
    alert('请先登录');
    return
  }
  adding.value = true
  try {
    await addToCart({goodsId: goods.value.id, quantity: 1})
    alert('已加入购物车')
  } catch (e) {
    alert(e.message)
  } finally {
    adding.value = false
  }
}
</script>
<style scoped>
.goods-img-wrap {
  width: 100%;
  height: 300px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border-radius: 12px;
  margin-bottom: 12px;
}

.map-hint {
  font-size: 12px;
  color: var(--text-light);
  margin-bottom: 8px;
}

.goods-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.no-img {
  color: #ccc;
  font-size: 16px;
}

.name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.price {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary);
}

.unit {
  color: var(--text-light);
  font-size: 14px;
}

.stock {
  margin-left: auto;
  font-size: 13px;
  color: var(--text-light);
}

.sales {
  font-size: 13px;
  color: var(--text-light);
  margin-top: 4px;
}

.desc {
  margin-top: 12px;
  font-size: 14px;
  color: var(--text-secondary);
  line-height: 1.6;
}

.action-bar {
  display: flex;
  gap: 12px;
  margin-top: 24px;
  justify-content: center;
}

.modal-mask {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  width: 100%;
  max-width: 420px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

/* 订单商品摘要 */
.order-goods-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f8f8;
  border-radius: 8px;
  margin-bottom: 12px;
}

.order-name {
  font-size: 14px;
  font-weight: 500;
}

.order-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary);
}

/* 地址区域 */
.address-section {
  margin-bottom: 8px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--text-primary);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.address-card {
  border: 1.5px solid var(--border);
  border-radius: 8px;
  padding: 10px 12px;
  cursor: pointer;
  background: #fff;
  transition: all .2s;
}

.address-card.active {
  border-color: var(--primary);
  background: rgba(254, 44, 85, 0.04);
}

.addr-header {
  display: flex;
  gap: 6px;
  margin-bottom: 4px;
}

.addr-tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  background: var(--primary);
  color: #fff;
}

.addr-default-badge {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #e6f7ff;
  color: #1890ff;
}

.addr-info {
  display: flex;
  gap: 8px;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.addr-phone {
  color: var(--text-light);
  font-weight: normal;
  font-size: 13px;
}

.addr-detail {
  font-size: 13px;
  color: var(--text-secondary);
  line-height: 1.4;
}

.text-btn {
  background: none;
  border: none;
  color: var(--primary);
  font-size: 13px;
  cursor: pointer;
  padding: 8px 0;
}

.manual-address-form {
  margin-top: 4px;
}

/* 合计 */
.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid var(--border);
  margin-top: 12px;
  font-size: 15px;
}

.total-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary);
}

/* 支付弹窗 */
.pay-info {
  text-align: center;
  margin-bottom: 16px;
}

.pay-amount {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary);
}

.pay-order {
  font-size: 12px;
  color: var(--text-light);
  margin-top: 4px;
}

.pay-type-select {
  display: flex;
  gap: 12px;
}

.pay-type-btn {
  flex: 1;
  padding: 12px;
  border: 1.5px solid var(--border);
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  text-align: center;
  font-size: 14px;
}

.pay-type-btn.active {
  border-color: var(--primary);
  color: var(--primary);
  background: rgba(254, 44, 85, 0.05);
}
</style>