<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/user" class="back-btn">← 返回</router-link>
      <span class="page-title">店铺订单</span>
      <span></span>
    </div>

    <div class="status-filter">
      <button
        v-for="s in statusOptions"
        :key="s.value"
        :class="['filter-btn', { active: currentStatus === s.value }]"
        @click="filterByStatus(s.value)"
      >{{ s.label }}</button>
    </div>

    <div v-if="list.length > 0">
      <div v-for="order in list" :key="order.orderNo" class="order-card card" @click="goDetail(order.orderNo)">
        <div class="order-header">
          <span class="order-no">订单号: {{ order.orderNo }}</span>
          <span class="tag" :class="statusTagClass(order.status)">{{ order.statusDesc }}</span>
        </div>
        <div class="order-footer">
          <span>收件人: {{ order.receiver }}</span>
          <span class="order-amount">¥{{ ((order.payAmount || order.totalAmount) / 100).toFixed(2) }}</span>
        </div>
        <div class="shop-actions" v-if="order.status === 1">
          <button class="btn btn-primary btn-sm" @click.stop="handleDelivery(order.orderNo)">发货</button>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="icon">📊</div>
      <div>暂无订单</div>
    </div>

    <div v-if="loading" class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getShopOrderList, startDelivery } from '../api/order'

const router = useRouter()
const list = ref([])
const loading = ref(true)
const currentStatus = ref('')

const statusOptions = [
  { label: '全部', value: '' },
  { label: '待付款', value: 0 },
  { label: '已付款', value: 1 },
  { label: '已发货', value: 2 },
  { label: '配送中', value: 3 },
  { label: '已完成', value: 4 }
]

onMounted(() => { loadOrders() })

async function loadOrders(status) {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 50 }
    if (status !== undefined && status !== '') params.status = status
    const res = await getShopOrderList(params)
    list.value = res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function filterByStatus(s) {
  currentStatus.value = s
  loadOrders(s)
}

function goDetail(orderNo) {
  router.push('/orders/' + orderNo)
}

async function handleDelivery(orderNo) {
  try {
    await startDelivery(orderNo)
    alert('已发货')
    loadOrders(currentStatus.value)
  } catch (e) {
    alert(e.message)
  }
}

function statusTagClass(status) {
  if (status === 0) return 'tag-warning'
  if (status === 1) return 'tag-primary'
  if (status === 4) return 'tag-success'
  return 'tag-primary'
}
</script>

<style scoped>
.status-filter {
  display: flex;
  gap: 6px;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 8px;
}
.filter-btn {
  padding: 6px 14px;
  border: 1px solid var(--border);
  background: #fff;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  white-space: nowrap;
  color: var(--text-secondary);
}
.filter-btn.active {
  background: var(--primary);
  color: #fff;
  border-color: var(--primary);
}
.order-card {
  cursor: pointer;
}
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.order-no { font-size: 13px; color: var(--text-light); }
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  margin-top: 4px;
}
.order-amount { font-size: 16px; font-weight: 600; color: var(--primary); }
.shop-actions { margin-top: 12px; display: flex; gap: 8px; justify-content: flex-end; }
</style>