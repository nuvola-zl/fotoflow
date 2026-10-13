import api from './index'

export function saveOrUpdateShopConfig(data) {
  return api.post('/shop/config', data)
}

export function getMyShopConfig() {
  return api.get('/shop/config')
}

export function getShopConfigById(shopId) {
  return api.get(`/shop/config/${shopId}`)
}