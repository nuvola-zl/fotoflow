import api from './index'

export function publishVideo(data) {
  const formData = new FormData()
  formData.append('title', data.title || '')
  formData.append('content', data.content || '')
  formData.append('videoUrl', data.videoUrl)
  return api.post('/video/publish', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function getLatestVideo() {
  return api.get('/video/getLatestOne')
}

export function sendBarrage(videoId, data) {
  return api.post(`/video/sendBarrage/${videoId}`, data)
}

export function getBarrages(videoId) {
  return api.get(`/video/getBarrages/${videoId}`)
}

export function likeVideo(id) {
  return api.post(`/video/like/${id}`)
}

export function collectVideo(id) {
  return api.post(`/video/collect/${id}`)
}