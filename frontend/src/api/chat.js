import api from './index'

export function chatSend(query) {
  return api.get('/chat/send', { params: { query } })
}