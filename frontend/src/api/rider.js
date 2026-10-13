import api from './index'

export function updateRiderStatus(data) {
  return api.put('/rider/status', data)
}

export function getRiderInfo() {
  return api.get('/rider/info')
}

export function getPendingOrders() {
  return api.get('/rider/orders/pending')
}

export function takeOrder(orderNo) {
  return api.post(`/rider/orders/${orderNo}/take`)
}

export function startRiderDelivery(orderNo) {
  return api.post(`/rider/orders/${orderNo}/start`)
}

export function arriveOrder(orderNo) {
  return api.post(`/rider/orders/${orderNo}/arrive`)
}

export function getTodayOrders(pageNum = 1, pageSize = 10) {
  return api.get('/rider/orders/today', { params: { pageNum, pageSize } })
}

export function uploadLocation(data) {
  return api.post('/rider/location', data)
}
// 查询骑手最新位置（用户端用）
export function getLatestLocation(orderNo) {
  return api.get('/rider/location/latest', { params: { orderNo } })
}