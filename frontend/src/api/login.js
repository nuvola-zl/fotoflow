import api from './index'

export function loginUser(data) {
  return api.post('/Login/login', data)
}

export function registerUser(data) {
  return api.post('/Login/register', data)
}

export function loginRider(data) {
  return api.post('/Login/rider/login', data)
}

export function registerRider(data) {
  return api.post('/Login/rider/register', data)
}