<<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/user" class="back-btn">← 返回</router-link>
      <span class="page-title">收货地址</span>
      <router-link to="/address/add" class="btn btn-primary btn-sm">添加</router-link>
    </div>

    <div v-if="list.length > 0">
      <div v-for="addr in list" :key="addr.id" class="address-card card">
        <div class="addr-header">
          <span class="addr-tag" v-if="addr.tag">{{ addr.tag }}</span>
          <span class="addr-receiver">{{ addr.receiver }}</span>
          <span class="addr-phone">{{ addr.phone }}</span>
          <span class="default-tag" v-if="addr.isDefault">默认</span>
        </div>
        <div class="addr-detail">{{ addr.addressDetail }}</div>
        <div class="addr-actions">
          <router-link :to="'/address/' + addr.id + '/edit'" class="btn btn-outline btn-sm">编辑</router-link>
          <button class="btn btn-outline btn-sm" v-if="!addr.isDefault" @click="setDefault(addr.id)">设为默认</button>
          <button class="btn btn-outline btn-sm" style="color:var(--danger)" @click="remove(addr.id)">删除</button>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="icon">📍</div>
      <div>暂无收货地址</div>
    </div>

    <div v-if="loading" class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAddressList, setDefaultAddress, deleteAddress } from '../api/address'

const list = ref([])
const loading = ref(true)

onMounted(() => { loadAddresses() })

async function loadAddresses() {
  loading.value = true
  try {
    const res = await getAddressList()
    list.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function setDefault(id) {
  try {
    await setDefaultAddress(id)
    loadAddresses()
  } catch (e) {
    alert(e.message)
  }
}

async function remove(id) {
  if (!confirm('确认删除？')) return
  try {
    await deleteAddress(id)
    list.value = list.value.filter(a => a.id !== id)
  } catch (e) {
    alert(e.message)
  }
}
</script>

<style scoped>
/* ========== 页面整体布局 ========== */
.page-container {
  max-width: 720px;
  margin: 0 auto;
  padding: 0 16px 32px;
  background: #f5f7fa;
  min-height: 100vh;
}

/* ========== 顶部导航栏 ========== */
.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  margin: 0 -16px 20px;
  padding-left: 16px;
  padding-right: 16px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
}

.back-btn {
  font-size: 14px;
  color: #666;
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 8px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 4px;
}

.back-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.page-title {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
  letter-spacing: 0.5px;
}

/* 添加按钮 */
.btn-primary.btn-sm {
  background: linear-gradient(135deg, #fe2c55 0%, #ff6b7a 100%);
  color: #fff;
  border: none;
  padding: 7px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  box-shadow: 0 2px 8px rgba(254, 44, 85, 0.25);
  transition: all 0.25s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
}

.btn-primary.btn-sm:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(254, 44, 85, 0.35);
}

/* ========== 地址卡片 ========== */
.address-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04), 0 4px 12px rgba(0, 0, 0, 0.03);
  border: 1px solid rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.address-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06), 0 2px 6px rgba(0, 0, 0, 0.04);
  border-color: rgba(254, 44, 85, 0.08);
}

/* 默认地址左侧标识条 */
.address-card:has(.default-tag)::before {
  content: '';
  position: absolute;
  left: 0;
  top: 20px;
  bottom: 20px;
  width: 3px;
  background: linear-gradient(to bottom, #fe2c55, #ff6b7a);
  border-radius: 0 3px 3px 0;
}

/* ========== 卡片头部信息 ========== */
.addr-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.addr-receiver {
  font-weight: 700;
  font-size: 16px;
  color: #1a1a1a;
  letter-spacing: 0.3px;
}

.addr-phone {
  font-size: 13px;
  color: #888;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 标签样式 */
.addr-tag {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe8cc 100%);
  color: #d84315;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.5px;
  border: 1px solid rgba(216, 67, 21, 0.08);
}

/* 默认标签 */
.default-tag {
  background: linear-gradient(135deg, rgba(254, 44, 85, 0.08) 0%, rgba(255, 107, 122, 0.08) 100%);
  color: #fe2c55;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
  border: 1px solid rgba(254, 44, 85, 0.12);
}

/* ========== 地址详情 ========== */
.addr-detail {
  font-size: 14px;
  color: #555;
  line-height: 1.7;
  margin-bottom: 16px;
  padding-left: 2px;
  font-weight: 400;
}

/* ========== 操作按钮区 ========== */
.addr-actions {
  display: flex;
  gap: 10px;
  padding-top: 14px;
  border-top: 1px solid #f0f0f0;
}

.addr-actions .btn {
  flex: 1;
  justify-content: center;
  text-align: center;
  padding: 8px 0;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

/* 编辑/默认按钮 */
.addr-actions .btn-outline {
  background: #fafafa;
  color: #555;
  border: 1px solid #e8e8e8;
  text-decoration: none;
}

.addr-actions .btn-outline:hover {
  background: #f0f0f0;
  border-color: #d0d0d0;
  color: #333;
  transform: translateY(-1px);
}

/* 删除按钮特殊处理 */
.addr-actions .btn-outline[style*="danger"] {
  background: #fff5f5;
  color: #e53935 !important;
  border-color: #ffcdd2;
}

.addr-actions .btn-outline[style*="danger"]:hover {
  background: #ffebee;
  border-color: #ef9a9a;
  transform: translateY(-1px);
}

/* ========== 空状态 ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  color: #999;
  font-size: 14px;
  animation: fadeIn 0.5s ease;
}

.empty-state .icon {
  font-size: 56px;
  margin-bottom: 16px;
  opacity: 0.7;
  filter: grayscale(0.3);
}

.empty-state div:last-child {
  font-size: 15px;
  color: #aaa;
  font-weight: 400;
}

/* ========== 加载动画 ========== */
.loading-spinner {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 60px 0;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #f0f0f0;
  border-top-color: #fe2c55;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ========== 响应式优化 ========== */
@media (max-width: 480px) {
  .page-container {
    padding: 0 12px 24px;
  }

  .address-card {
    padding: 16px;
    border-radius: 14px;
  }

  .addr-actions .btn {
    font-size: 12px;
    padding: 7px 0;
  }

  .addr-header {
    gap: 8px;
  }
}
</style>