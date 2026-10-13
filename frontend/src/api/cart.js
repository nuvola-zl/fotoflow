import api from './index'

export function addToCart(data) {
  return api.post('/cart/add', data)
}

export function getCartList() {
  return api.get('/cart/list')
}

export function updateCartQuantity(cartId, quantity) {
  return api.put(`/cart/${cartId}/quantity/${quantity}`)
}

export function deleteCartItem(cartId) {
  return api.delete(`/cart/${cartId}`)
}

export function toggleCartChecked(cartId) {
  return api.put(`/cart/${cartId}/checked`)
}