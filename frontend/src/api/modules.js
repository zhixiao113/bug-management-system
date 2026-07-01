import http from './http'

export const authApi = {
  login: (data) => http.post('/auth/login', data),
  me: () => http.get('/auth/me')
}

export const userApi = {
  list: () => http.get('/users')
}

export const bugApi = {
  list: (params) => http.get('/bugs', { params }),
  detail: (id) => http.get(`/bugs/${id}`),
  create: (data) => http.post('/bugs', data),
  update: (id, data) => http.put(`/bugs/${id}`, data),
  remove: (id) => http.delete(`/bugs/${id}`),
  updateStatus: (id, data) => http.put(`/bugs/${id}/status`, data),
  assign: (id, data) => http.put(`/bugs/${id}/assign`, data),
  logs: (id) => http.get(`/bugs/${id}/logs`)
}

export const statisticsApi = {
  overview: () => http.get('/statistics/overview')
}
