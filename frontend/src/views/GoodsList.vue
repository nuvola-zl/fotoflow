<template>
  <div class="page-container-wide">
    <div class="page-header">
      <router-link to="/home" class="back-btn">← 返回</router-link>
      <span class="page-title">商品商城</span>
      <span></span>
    </div>

    <!-- Search -->
    <div class="search-bar">
      <input v-model="searchName" class="form-input" placeholder="搜索商品..." @keyup.enter="doSearch" />
      <button class="btn btn-primary btn-sm" @click="doSearch">搜索</button>
    </div>

    <!-- Goods grid -->
    <div v-if="list.length > 0" class="goods-grid">
      <router-link v-for="g in list" :key="g.id" :to="'/goods/' + g.id" class="goods-card">
        <div class="goods-img">
          <img v-if="g.goodsImg" :src="g.goodsImg" :alt="g.goodsName" />
          <span v-else class="img-placeholder">暂无图片</span>
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ g.goodsName }}</div>
          <div class="goods-bottom">
            <span class="goods-price">¥{{ (g.price / 100).toFixed(2) }}</span>
            <span class="goods-unit">/{{ g.unit }}</span>
            <span class="goods-sales" v-if="g.sales">已售 {{ g.sales }}</span>
          </div>
        </div>
      </router-link>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="icon">📦</div>
      <div>暂无商品</div>
    </div>

    <div v-if="loading" class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserGoodsList } from '../api/goods'

const list = ref([])
const loading = ref(false)
const searchName = ref('')

onMounted(() => { loadGoods() })

async function loadGoods(name) {
  loading.value = true
  try {
    const params = { pageNum: 1, pageSize: 20 }
    if (name) params.goodsName = name
    const res = await getUserGoodsList(params)
    list.value = res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function doSearch() { loadGoods(searchName.value) }
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}
.search-bar .form-input { flex: 1; }
.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}
.goods-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.goods-img {
  width: 100%;
  height: 180px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.goods-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.img-placeholder { color: #ccc; font-size: 14px; }
.goods-info { padding: 10px 12px; }
.goods-name {
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.goods-bottom {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-top: 6px;
}
.goods-price { color: var(--primary); font-size: 16px; font-weight: 600; }
.goods-unit { color: var(--text-light); font-size: 12px; }
.goods-sales { margin-left: auto; font-size: 11px; color: var(--text-light); }
</style>