<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/user" class="back-btn">← 返回</router-link>
      <span class="page-title">店铺配置</span>
      <span></span>
    </div>

    <div class="card">
      <!-- Map -->
      <div class="form-group">
        <label>店铺位置（点击地图选择）</label>
        <div id="shop-map" style="width:100%;height:220px;border-radius:8px;background:#f5f5f5;margin-bottom:8px"></div>
      </div>

      <div class="form-group">
        <label>店铺名称 *</label>
        <input v-model="form.shopName" class="form-input" placeholder="请输入店铺名称" />
      </div>
      <div class="form-group">
        <label>店铺地址 *</label>
        <textarea v-model="form.shopAddress" class="form-input" rows="2" placeholder="请输入文字地址"></textarea>
      </div>
      <div class="form-row">
        <div class="form-group" style="flex:1">
          <label>经度</label>
          <input v-model.number="form.longitude" class="form-input" type="number" step="any" placeholder="经度" />
        </div>
        <div class="form-group" style="flex:1">
          <label>纬度</label>
          <input v-model.number="form.latitude" class="form-input" type="number" step="any" placeholder="纬度" />
        </div>
      </div>
      <div class="form-group">
        <label>营业状态</label>
        <div class="status-toggle">
          <button
            :class="['toggle-btn', { active: form.status === 1 }]"
            @click="form.status = form.status === 1 ? 0 : 1"
          >{{ form.status === 1 ? '营业中' : '休息中' }}</button>
        </div>
      </div>

      <button class="btn btn-primary btn-large" style="width:100%;margin-top:16px" @click="handleSubmit" :disabled="saving">
        {{ saving ? '保存中...' : (existing ? '更新店铺' : '创建店铺') }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { saveOrUpdateShopConfig, getMyShopConfig } from '../api/shop'
import { loadBMap } from '../utils/bmap'

const saving = ref(false)
const existing = ref(false)
const form = ref({
  shopName: '', shopAddress: '', longitude: null, latitude: null, status: 1
})
let map = null

onMounted(async () => {
  try {
    const res = await getMyShopConfig()
    if (res.data) {
      existing.value = true
      Object.assign(form.value, res.data)
    }
  } catch (e) {
    // no shop yet
  }
  await nextTick()
  await loadBMap()
  initMap()
})

function initMap() {
  const el = document.getElementById('shop-map')
  if (!el || !window.BMap) return

  const centerLng = form.value.longitude || 116.4
  const centerLat = form.value.latitude || 39.9

  map = new window.BMap.Map('shop-map')
  const point = new window.BMap.Point(centerLng, centerLat)
  map.centerAndZoom(point, 15)
  map.enableScrollWheelZoom(true)

  // 创建地理编码实例（用于点击后反查地址）
  const geocoder = new window.BMap.Geocoder()

  // 如果有已有坐标，初始化标记
  if (form.value.longitude && form.value.latitude) {
    const marker = new window.BMap.Marker(
        new window.BMap.Point(form.value.longitude, form.value.latitude)
    )
    map.addOverlay(marker)
  }

  // 点击地图选点
  map.addEventListener('click', (e) => {
    const lng = e.point.lng
    const lat = e.point.lat

    // 1. 更新坐标
    form.value.longitude = lng
    form.value.latitude = lat

    // 2. 清除旧标记，添加新标记
    map.clearOverlays()
    const marker = new window.BMap.Marker(new window.BMap.Point(lng, lat))
    map.addOverlay(marker)

    // 3. 反向地理编码：坐标 → 地址
    geocoder.getLocation(new window.BMap.Point(lng, lat), (result) => {
      if (result && result.address) {
        // 自动填充详细地址
        form.value.shopAddress = result.address
      } else {
        console.warn('地址解析失败')
      }
    })
  })
}

async function handleSubmit() {
  saving.value = true
  try {
    await saveOrUpdateShopConfig(form.value)
    alert('保存成功')
    existing.value = true
  } catch (e) {
    alert(e.message)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.form-row { display: flex; gap: 12px; }
.toggle-btn {
  padding: 8px 24px;
  border: 2px solid var(--border);
  background: #fff;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
}
.toggle-btn.active {
  background: var(--success);
  color: #fff;
  border-color: var(--success);
}
</style>