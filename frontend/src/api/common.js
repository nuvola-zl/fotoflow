import api from './index'


export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return api.post('/admin/common/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}