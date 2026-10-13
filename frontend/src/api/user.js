import api from './index'

export function getUserInfo() {
  return api.get('/space/userInfo')
}

export function showInfo() {
  return api.get('/space/showInfo')
}

export function updateUserInfo(data) {
  return api.post('/space/updateInfo', data)
}