import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('fotoflow_user') || 'null'))
  const rider = ref(JSON.parse(localStorage.getItem('fotoflow_rider') || 'null'))

  const token = computed(() => user.value?.token || '')
  const riderToken = computed(() => rider.value?.token || '')
  const isLoggedIn = computed(() => !!user.value)
  const isRiderLoggedIn = computed(() => !!rider.value)
  const isShopOwner = computed(() => user.value?.role === 'shop' || user.value?.role === 'SHOP')

  function setUser(data) {
    user.value = data
    localStorage.setItem('fotoflow_user', JSON.stringify(data))
  }

  function setRider(data) {
    rider.value = data
    localStorage.setItem('fotoflow_rider', JSON.stringify(data))
  }

  function updateUser(partial) {
    if (user.value) {
      user.value = { ...user.value, ...partial }
      localStorage.setItem('fotoflow_user', JSON.stringify(user.value))
    }
  }

  function updateRider(partial) {
    if (rider.value) {
      rider.value = { ...rider.value, ...partial }
      localStorage.setItem('fotoflow_rider', JSON.stringify(rider.value))
    }
  }

  function logout() {
    user.value = null
    localStorage.removeItem('fotoflow_user')
  }

  function logoutRider() {
    rider.value = null
    localStorage.removeItem('fotoflow_rider')
  }

  return {
    user, rider, token, riderToken,
    isLoggedIn, isRiderLoggedIn, isShopOwner,
    setUser, setRider, updateUser, updateRider, logout, logoutRider
  }
})