import api from './index'

export function addGoods(data) {
  return api.post('/goods', data)
}

export function updateGoods(goodsId, data) {
  return api.put(`/goods/${goodsId}`, data)
}

export function deleteGoods(goodsId) {
  return api.delete(`/goods/${goodsId}`)
}

export function updateGoodsStatus(goodsId, status) {
  return api.put(`/goods/${goodsId}/status/${status}`)
}

export function getMyGoodsList(params) {
  return api.get('/goods/my-list', { params })
}

export function getUserGoodsList(params) {
  return api.get('/goods/list', { params })
}

export function getGoodsDetail(goodsId) {
  return api.get(`/goods/${goodsId}`)
}