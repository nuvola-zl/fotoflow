import api from './index'

export function buyNow(data) {
  return api.post('/order/buy-now', data)
}

export function payOrder(data) {
  return api.post('/order/pay', data)
}

export function getUserOrderList(params) {
  return api.get('/order/my-list', { params })
}

export function getShopOrderList(params) {
  return api.get('/order/shop-list', { params })
}

export function getOrderDetail(orderNo) {
  return api.get(`/order/${orderNo}`)
}

export function startDelivery(orderNo) {
  return api.post(`/order/${orderNo}/delivery/start`)
}

export function confirmReceive(orderNo) {
  return api.post(`/order/${orderNo}/receive`)
}

export function cancelOrder(orderNo) {
  return api.post(`/order/${orderNo}/cancel`)
}

export function getRiderLocation(orderNo) {
  return api.get(`/order/${orderNo}/rider-location`)
}

export function getDeliveryPath(orderNo) {
  return api.get(`/order/${orderNo}/delivery-path`)
}
