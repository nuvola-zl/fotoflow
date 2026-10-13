import api from './index'

export function addAddress(data) {
  return api.post('/address', data)
}

export function getAddressList() {
  return api.get('/address/list')
}

export function getAddressById(id) {
  return api.get(`/address/${id}`)
}

export function updateAddress(id, data) {
  return api.put(`/address/${id}`, data)
}

export function deleteAddress(id) {
  return api.delete(`/address/${id}`)
}

export function setDefaultAddress(id) {
  return api.put(`/address/${id}/default`)
}