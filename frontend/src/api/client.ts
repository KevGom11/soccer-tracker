import axios from 'axios'

export const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
})

api.interceptors.response.use(
    r => r,
    err => {
        console.error('API ERR:', {
            url: err.config?.url,
            method: err.config?.method,
            status: err.response?.status,
            data: err.response?.data,
            message: err.message,
        })
        return Promise.reject(err)
    }
)

export async function getJson<T = any>(url: string): Promise<T> {
    const res = await api.get<T>(url)
    return res.data
}

