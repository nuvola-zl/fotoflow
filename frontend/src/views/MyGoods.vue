<template>
  <div class="page-container-wide">
    <div class="page-header">
      <router-link to="/user" class="back-btn">← 返回</router-link>
      <span class="page-title">我的商品</span>
      <router-link to="/goods/add" class="btn btn-primary btn-sm">添加</router-link>
    </div>

    <div v-if="list.length > 0" class="goods-grid">
      <div v-for="g in list" :key="g.id" class="goods-card">
        <div class="goods-img">
          <img v-if="g.goodsImg" :src="g.goodsImg" :alt="g.goodsName" />
          <span v-else class="img-placeholder">暂无图片</span>
        </div>
        <div class="goods-info">
          <div class="goods-name">{{ g.goodsName }}</div>
          <div class="goods-bottom">
            <span class="goods-price">¥{{ (g.price / 100).toFixed(2) }}</span>
            <span class="tag" :class="g.status === 1 ? 'tag-success' : 'tag-warning'">
              {{ g.status === 1 ? '上架' : '下架' }}
            </span>
          </div>
          <div class="goods-actions">
            <button class="btn btn-outline btn-sm" @click="toggleStatus(g)">
              {{ g.status === 1 ? '下架' : '上架' }}
            </button>
            <button class="btn btn-outline btn-sm" @click="handleDelete(g.id)">删除</button>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="icon">📦</div>
      <div>暂无商品，点击右上角添加</div>
    </div>

    <div v-if="loading" class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyGoodsList, updateGoodsStatus, deleteGoods } from '../api/goods'

const list = ref([])
const loading = ref(true)

onMounted(() => { loadGoods() })

async function loadGoods() {
  loading.value = true
  try {
    const res = await getMyGoodsList({ pageNum: 1, pageSize: 50 })
    list.value = res.data?.records || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function toggleStatus(g) {
  try {
    const newStatus = g.status === 1 ? 0 : 1
    await updateGoodsStatus(g.id, newStatus)
    g.status = newStatus
  } catch (e) {
    alert(e.message)
  }
}

async function handleDelete(id) {
  if (!confirm('确认删除该商品？')) return
  try {
    await deleteGoods(id)
    list.value = list.value.filter(g => g.id !== id)
  } catch (e) {
    alert(e.message)
  }
}
</script>

<style scoped>
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
  height: 150px;
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
.goods-name { font-size: 14px; font-weight: 500; }
.goods-bottom { display: flex; align-items: center; gap: 8px; margin-top: 6px; }
.goods-price { color: var(--primary); font-size: 15px; font-weight: 600; }
.goods-actions { display: flex; gap: 6px; margin-top: 8px; }
</style>