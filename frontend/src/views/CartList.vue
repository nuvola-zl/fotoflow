<template>
  <div class="page-container">
    <div class="page-header">
      <router-link to="/home" class="back-btn">← 返回</router-link>
      <span class="page-title">购物车</span>
      <span></span>
    </div>

    <div v-if="list.length > 0">
      <div v-for="item in list" :key="item.cartId" class="cart-item card">
        <div class="cart-check" @click="toggleCheck(item)">
          <span :style="{ color: item.isChecked === 1 ? 'var(--primary)' : '#ddd', fontSize: '22px' }">
            {{ item.isChecked === 1 ? '☑' : '☐' }}
          </span>
        </div>
        <img v-if="item.goodsImg" :src="item.goodsImg" class="cart-img" />
        <span v-else class="cart-img-placeholder">📦</span>
        <div class="cart-info">
          <div class="cart-name">{{ item.goodsName }}</div>
          <div class="cart-price">¥{{ (item.price / 100).toFixed(2) }}/{{ item.unit }}</div>
          <div class="cart-controls">
            <button class="qty-btn" @click="changeQty(item, -1)">−</button>
            <span class="qty-val">{{ item.quantity }}</span>
            <button class="qty-btn" @click="changeQty(item, 1)">+</button>
          </div>
        </div>
        <button class="cart-del" @click="removeItem(item.cartId)">🗑️</button>
      </div>
    </div>

    <div v-else-if="!loading" class="empty-state">
      <div class="icon">🛍️</div>
      <div>购物车空空如也</div>
      <router-link to="/goods" class="btn btn-primary" style="margin-top:16px">去逛逛</router-link>
    </div>

    <div v-if="loading" class="loading-spinner"><div class="spinner"></div></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCartList, updateCartQuantity, deleteCartItem, toggleCartChecked } from '../api/cart'

const list = ref([])
const loading = ref(true)

onMounted(() => { loadCart() })

async function loadCart() {
  loading.value = true
  try {
    const res = await getCartList()
    list.value = res.data || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function changeQty(item, delta) {
  const nq = item.quantity + delta
  if (nq < 1) return
  try {
    await updateCartQuantity(item.cartId, nq)
    item.quantity = nq
  } catch (e) {
    alert(e.message)
  }
}

async function removeItem(cartId) {
  try {
    await deleteCartItem(cartId)
    list.value = list.value.filter(i => i.cartId !== cartId)
  } catch (e) {
    alert(e.message)
  }
}

async function toggleCheck(item) {
  try {
    await toggleCartChecked(item.cartId)
    item.isChecked = item.isChecked === 1 ? 0 : 1
  } catch (e) {
    alert(e.message)
  }
}
</script>

<style scoped>
.cart-item {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cart-check { cursor: pointer; flex-shrink: 0; }
.cart-img {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}
.cart-img-placeholder {
  width: 72px;
  height: 72px;
  border-radius: 8px;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  flex-shrink: 0;
}
.cart-info { flex: 1; min-width: 0; }
.cart-name { font-size: 14px; font-weight: 500; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cart-price { font-size: 14px; color: var(--primary); font-weight: 600; margin-top: 4px; }
.cart-controls { display: flex; align-items: center; gap: 10px; margin-top: 8px; }
.qty-btn {
  width: 26px; height: 26px;
  border-radius: 50%;
  border: 1px solid var(--border);
  background: #fff;
  font-size: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.qty-val { font-size: 15px; font-weight: 500; min-width: 20px; text-align: center; }
.cart-del {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  flex-shrink: 0;
}
</style>