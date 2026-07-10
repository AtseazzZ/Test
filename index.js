import request from './request'

export const contractApi = {
  list: () => request.get('/contract/list'),
  getById: (id) => request.get(`/contract/${id}`),
  create: (data) => request.post('/contract', data),
  submit: (id) => request.post(`/contract/${id}/submit`),
  process: (id, comment) => request.post(`/contract/${id}/process`, { comment }),
  approve: (id, comment) => request.post(`/contract/${id}/approve`, { comment }),
  withdraw: (id) => request.post(`/contract/${id}/withdraw`),
  getRecords: (id) => request.get(`/contract/${id}/records`)
}

export const authApi = {
  login: (data) => request.post('/auth/login', data)
}

export const auditApi = {
  list: () => request.get('/audit/list')
}