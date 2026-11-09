
import axios from 'axios'

// Use the local backend by default.
// In production, read the URL from the Vite environment variable.
export const API_BASE_URL = (
  import.meta.env.VITE_API_BASE_URL ||
  'http://localhost:8080/api'
).replace(/\/+$/, '')

// Base URL for product images and uploaded files.
export const SERVER_URL = API_BASE_URL.replace(/\/api$/, '')

const api = axios.create({
  baseURL: API_BASE_URL
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default api
