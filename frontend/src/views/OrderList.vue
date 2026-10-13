<template>
  <div class="page-container">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <router-link to="/user" class="back-btn">← 返回个人中心</router-link>
      <span class="page-title">我的订单</span>
      <span class="header-placeholder"></span>
    </div>

    <div class="order-layout">
      <!-- 左侧状态导航栏 -->
      <aside class="sidebar">
        <div class="sidebar-title">订单状态</div>
        <nav class="status-nav">
          <button
              v-for="s in statusOptions"
              :key="s.value"
              :class="['nav-item', { active: currentStatus === s.value }]"
              @click="filterByStatus(s.value)"
          >
            <span class="nav-label">{{ s.label }}</span>
            <span v-if="getStatusCount(s.value) > 0" class="nav-badge">{{ getStatusCount(s.value) }}</span>
          </button>
        </nav>
      </aside>

      <!-- 右侧订单列表 -->
      <main class="main-content">
        <!-- 加载中 -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>正在加载订单...</p>
        </div>

        <!-- 空状态 -->
        <div v-else-if="list.length === 0" class="empty-state">
          <div class="empty-icon">📋</div>
          <h3>暂无订单</h3>
          <p>您当前没有{{ currentStatusLabel }}的订单</p>
          <router-link to="/" class="btn-primary">去逛逛</router-link>
        </div>

        <!-- 订单列表 -->
        <div v-else class="order-list">
          <router-link
              v-for="order in list"
              :key="order.orderNo"
              :to="'/orders/' + order.orderNo"
              class="order-card"
          >
            <!-- 订单头部 -->
            <div class="order-header">
              <div class="order-meta">
                <span class="order-no">订单号：{{ order.orderNo }}</span>
                <span class="order-time" v-if="order.createTime">{{ order.createTime }}</span>
              </div>
              <span class="status-tag" :class="statusTagClass(order.status)">
                {{ order.statusDesc }}
              </span>
            </div>

            <!-- 商品列表 -->
            <div class="order-body">
              <div v-if="order.items && order.items.length > 0" class="goods-list">
                <div
                    v-for="(item, index) in order.items.slice(0, 3)"
                    :key="item.goodsId"
                    class="goods-item"
                    :class="{ 'more-items': index === 2 && order.items.length > 3 }"
                >
                  <div class="goods-img-wrap">
                    <img v-if="item.goodsImg" :src="item.goodsImg" :alt="item.goodsName" />
                    <div v-else class="goods-img-placeholder">📦</div>
                  </div>
                  <div class="goods-info">
                    <p class="goods-name">{{ item.goodsName }}</p>
                    <p class="goods-qty">x{{ item.quantity }}</p>
                  </div>
                </div>
                <div v-if="order.items.length > 3" class="more-hint">
                  等共 {{ order.items.length }} 件商品
                </div>
              </div>
              <div v-else class="goods-list">
                <div class="goods-item">
                  <div class="goods-img-wrap">
                    <div class="goods-img-placeholder">📦</div>
                  </div>
                  <div class="goods-info">
                    <p class="goods-name">订单商品</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- 订单底部 -->
            <div class="order-footer">
              <div class="footer-left">
                <span class="item-count">共 {{ order.itemCount || order.items?.length || 1 }} 件商品</span>
              </div>
              <div class="footer-right">
                <span class="total-label">实付金额：</span>
                <span class="total-amount">¥{{ ((order.payAmount || order.totalAmount) / 100).toFixed(2) }}</span>
              </div>
            </div>

            <!-- 操作按钮区 -->
            <div class="order-actions">
              <button class="btn-outline">查看详情</button>
              <button v-if="order.status === 0" class="btn-primary">立即付款</button>
              <button v-if="order.status === 2 || order.status === 3" class="btn-primary">确认收货</button>
              <button v-if="order.status === 4" class="btn-outline">申请售后</button>
            </div>
          </router-link>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserOrderList } from '../api/order'

const list = ref([])
const loading = ref(true)
const currentStatus = ref('')

const statusOptions = [
  {label: '全部订单', value: ''},
  {label: '待付款', value: 0},
  {label: '已付款', value: 1},
  {label: '已发货', value: 2},
  {label: '配送中', value: 3},
  {label: '已完成', value: 4},
  {label: '已取消', value: 5}
]

const currentStatusLabel = computed(() => {
  const opt = statusOptions.find(s => s.value === currentStatus.value)
  return opt ? opt.label : ''
})

// 简单统计各状态数量（基于当前已加载数据，实际可接后端接口）
function getStatusCount(status) {
  if (status === '') return 0
  return list.value.filter(o => o.status === status).length
}

onMounted(() => {
  loadOrders()
})

async function loadOrders(status) {
  loading.value = true
  try {
    const params = {pageNum: 1, pageSize: 50}
    if (status !== undefined && status !== '') params.status = status
    const res = await getUserOrderList(params)
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

function statusTagClass(status) {
  if (status === 0) return 'status-warning'
  if (status === 1 || status === 2) return 'status-info'
  if (status === 3) return 'status-primary'
  if (status === 4) return 'status-success'
  if (status === 5) return 'status-gray'
  return 'status-info'
}
</script>

<style scoped>
/* ========== 基础变量与布局 ========== */
.page-container {
  --primary: #ff5000;
  --primary-light: #fff0e8;
  --success: #52c41a;
  --warning: #faad14;
  --info: #1890ff;
  --danger: #ff4d4f;
  --text: #333;
  --text-secondary: #666;
  --text-light: #999;
  --border: #e8e8e8;
  --bg: #f5f5f5;
  --card-bg: #fff;
  --radius: 12px;
  --shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  --shadow-hover: 0 8px 24px rgba(0, 0, 0, 0.08);

  min-height: 100vh;
  background: var(--bg);
}

/* ========== 顶部导航 ========== */
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.back-btn:hover {
  color: var(--primary);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--text);
}

.header-placeholder {
  width: 80px;
}

/* ========== 主体两栏布局 ========== */
.order-layout {
  display: flex;
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 24px;
  gap: 20px;
  align-items: flex-start;
}

/* ========== 左侧边栏 ========== */
.sidebar {
  width: 200px;
  flex-shrink: 0;
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  padding: 20px 0;
  position: sticky;
  top: 80px;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text);
  padding: 0 20px 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
}

.status-nav {
  display: flex;
  flex-direction: column;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-secondary);
  transition: all 0.2s;
  position: relative;
}

.nav-item:hover {
  color: var(--primary);
  background: var(--primary-light);
}

.nav-item.active {
  color: var(--primary);
  background: var(--primary-light);
  font-weight: 600;
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: var(--primary);
  border-radius: 0 2px 2px 0;
}

.nav-badge {
  background: var(--danger);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

/* ========== 右侧主内容 ========== */
.main-content {
  flex: 1;
  min-width: 0;
}

/* ========== 加载状态 ========== */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}

.loading-state p {
  margin-top: 16px;
  color: var(--text-light);
  font-size: 14px;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* ========== 空状态 ========== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.6;
}

.empty-state h3 {
  font-size: 18px;
  color: var(--text);
  margin-bottom: 8px;
}

.empty-state p {
  font-size: 14px;
  color: var(--text-light);
  margin-bottom: 24px;
}

.btn-primary {
  display: inline-block;
  padding: 10px 32px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: opacity 0.2s;
}

.btn-primary:hover {
  opacity: 0.9;
}

/* ========== 订单卡片 ========== */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  display: block;
  background: var(--card-bg);
  border-radius: var(--radius);
  box-shadow: var(--shadow);
  text-decoration: none;
  color: inherit;
  transition: box-shadow 0.3s, transform 0.3s;
  overflow: hidden;
}

.order-card:hover {
  box-shadow: var(--shadow-hover);
  transform: translateY(-2px);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f5f5f5;
}

.order-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.order-no {
  font-size: 13px;
  color: var(--text-light);
}

.order-time {
  font-size: 12px;
  color: var(--text-light);
}

.status-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.status-warning {
  background: #fff7e6;
  color: #fa8c16;
}

.status-info {
  background: #e6f7ff;
  color: #1890ff;
}

.status-primary {
  background: #f0f5ff;
  color: #2f54eb;
}

.status-success {
  background: #f6ffed;
  color: #52c41a;
}

.status-gray {
  background: #f5f5f5;
  color: var(--text-light);
}

/* 订单商品区 */
.order-body {
  padding: 16px 20px;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.goods-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.goods-img-wrap {
  width: 64px;
  height: 64px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
}

.goods-img-wrap img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.goods-img-placeholder {
  font-size: 28px;
}

.goods-info {
  flex: 1;
  min-width: 0;
}

.goods-name {
  font-size: 14px;
  color: var(--text);
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin: 0;
}

.goods-qty {
  font-size: 13px;
  color: var(--text-light);
  margin: 4px 0 0;
}

.more-hint {
  font-size: 13px;
  color: var(--text-light);
  padding-left: 76px;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fafafa;
  border-top: 1px solid #f5f5f5;
}

.item-count {
  font-size: 13px;
  color: var(--text-secondary);
}

.footer-right {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.total-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.total-amount {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary);
}

/* 操作按钮 */
.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid #f5f5f5;
}

.order-actions button {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-outline {
  background: #fff;
  border: 1px solid var(--border);
  color: var(--text-secondary);
}

.btn-outline:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.order-actions .btn-primary {
  padding: 8px 20px;
  font-size: 13px;
}

/* ========== 响应式 ========== */
@media (max-width: 768px) {
  .order-layout {
    flex-direction: column;
    padding: 0 12px;
  }

  .sidebar {
    width: 100%;
    position: static;
    padding: 12px 0;
  }

  .status-nav {
    flex-direction: row;
    overflow-x: auto;
    padding: 0 12px;
    gap: 8px;
  }

  .nav-item {
    padding: 8px 16px;
    border-radius: 20px;
    white-space: nowrap;
    background: #f5f5f5;
  }

  .nav-item.active::before {
    display: none;
  }

  .nav-item.active {
    background: var(--primary);
    color: #fff;
  }

  .nav-item:hover {
    background: #eee;
    color: var(--text-secondary);
  }

  .nav-item.active:hover {
    background: var(--primary);
    color: #fff;
  }
}
</style>