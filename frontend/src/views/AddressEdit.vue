<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/address" class="back-btn">← 返回</router-link>
      <span class="page-title">{{ isEdit ? '编辑地址' : '添加地址' }}</span>
      <span></span>
    </div>

    <div class="card">
      <!-- Map picker -->
      <div class="form-group">
        <label>在地图上选择位置</label>
        <div id="address-map" style="width:100%;height:220px;border-radius:8px;background:#f5f5f5;margin-bottom:8px"></div>
        <div class="map-hint">点击地图选择位置，或手动输入经纬度</div>
      </div>

      <div class="form-group">
        <label>收件人 *</label>
        <input v-model="form.receiver" class="form-input" placeholder="请输入收件人" />
      </div>
      <div class="form-group">
        <label>电话 *</label>
        <input v-model="form.phone" class="form-input" placeholder="请输入电话" />
      </div>
      <div class="form-group">
        <label>详细地址 *</label>
        <textarea v-model="form.addressDetail" class="form-input" rows="2" placeholder="请输入详细地址"></textarea>
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
        <label>标签</label>
        <div class="tag-select">
          <button
            v-for="t in ['家', '公司', '学校']"
            :key="t"
            :class="['tag-btn', { active: form.tag === t }]"
            @click="form.tag = form.tag === t ? '' : t"
          >{{ t }}</button>
        </div>
      </div>

      <button class="btn btn-primary btn-large" style="width:100%;margin-top:16px" @click="handleSubmit" :disabled="saving">
        {{ saving ? '保存中...' : '保存' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { addAddress, getAddressById, updateAddress } from '../api/address'
import { loadBMap } from '../utils/bmap'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const saving = ref(false)
const form = ref({
  receiver: '', phone: '', addressDetail: '',
  longitude: null, latitude: null, tag: ''
})
let map = null

onMounted(async () => {
  if (isEdit.value) {
    try {
      const res = await getAddressById(route.params.id)
      Object.assign(form.value, res.data)
    } catch (e) {
      alert(e.message)
      router.push('/address')
      return
    }
  }
  await nextTick()
  await loadBMap()
  initMap()
})

function initMap() {
  const el = document.getElementById('address-map')
  if (!el || !window.BMap) return
  const centerLng = form.value.longitude || 116.4
  const centerLat = form.value.latitude || 39.9
  map = new window.BMap.Map('address-map')
  const point = new window.BMap.Point(centerLng, centerLat)
  map.centerAndZoom(point, 15)
  map.enableScrollWheelZoom(true)

  const geocoder = new window.BMap.Geocoder()

  if (form.value.longitude && form.value.latitude) {
    const marker = new window.BMap.Marker(new window.BMap.Point(form.value.longitude, form.value.latitude))
    map.addOverlay(marker)
  }

  map.addEventListener('click', (e) => {
    const lng = e.point.lng
    const lat = e.point.lat
    form.value.longitude = lng
    form.value.latitude = lat
    map.clearOverlays()
    const marker = new window.BMap.Marker(new window.BMap.Point(lng, lat))
    map.addOverlay(marker)

    geocoder.getLocation(new window.BMap.Point(lng, lat), (result) => {
      if (result && result.address) {
        form.value.addressDetail = result.address
      } else {
        console.warn('地址解析失败')
      }
    })
  })
}

async function handleSubmit() {
  saving.value = true
  try {
    if (isEdit.value) {
      await updateAddress(route.params.id, form.value)
    } else {
      await addAddress(form.value)
    }
    alert('保存成功')
    router.push('/address')
  } catch (e) {
    alert(e.message)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.form-row {
  display: flex;
  gap: 12px;
}

.map-hint {
  font-size: 12px;
  color: var(--text-light);
}

.tag-select {
  display: flex;
  gap: 8px;
}

.tag-btn {
  padding: 6px 18px;
  border: 1px solid var(--border);
  background: #fff;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
}

.tag-btn.active {
  background: rgba(254, 44, 85, 0.1);
  color: var(--primary);
  border-color: var(--primary);
}
</style>