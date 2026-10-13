<template>
  <div id="app-root">
    <div class="app-content">
      <router-view />
    </div>

    <div v-if="toast.show" :class="['toast', 'toast-' + toast.type]">
      {{ toast.message }}
    </div>
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'

const toast = ref({ show: false, message: '', type: 'success' })
let toastTimer = null

function showToast(message, type = 'success') {
  toast.value = { show: true, message, type }
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toast.value.show = false
  }, 2000)
}

provide('toast', showToast)
</script>

<style>
#app-root {
  min-height: 100vh;
  background: #f8f8f8;
}

.app-content {
  /* 导航栏已删除，不再需要顶部 padding */
}
</style>