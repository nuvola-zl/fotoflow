import axios from 'axios'

const api = axios.create({
    baseURL: '/api',
    timeout: 15000,
    headers: { 'Content-Type': 'application/json' }
})

api.interceptors.request.use(
    (config) => {
        const user = JSON.parse(localStorage.getItem('fotoflow_user') || 'null')
        const rider = JSON.parse(localStorage.getItem('fotoflow_rider') || 'null')

        const isRiderApi = config.url?.includes('/rider/')

        if (isRiderApi) {
            if (rider?.token) {
                config.headers.authentication = rider.token
            }
        } else {
            if (user?.token) {
                config.headers.authentication = user.token
            }
        }
        return config
    },
    (error) => Promise.reject(error)
)

api.interceptors.response.use(
    (response) => {
        const data = response.data
        if (data.code !== 1) {
            if (data.code === 0 && data.msg && data.msg.includes('登录')) {
                const isRider = response.config?.url?.includes('/rider/')
                if (isRider) {
                    localStorage.removeItem('fotoflow_rider')
                    window.location.href = '/rider/login'
                } else {
                    localStorage.removeItem('fotoflow_user')
                    window.location.href = '/login'
                }
            }
            return Promise.reject(new Error(data.msg || '请求失败'))
        }
        return data
    },
    (error) => {
        if (error.response?.status === 401) {
            const isRider = error.config?.url?.includes('/rider/')
            if (isRider) {
                localStorage.removeItem('fotoflow_rider')
                window.location.href = '/rider/login'
            } else {
                localStorage.removeItem('fotoflow_user')
                window.location.href = '/login'
            }
        }
        return Promise.reject(error)
    }
)

export default api