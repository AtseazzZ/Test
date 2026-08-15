import request from '@/utils/request'

export const contractPage = (params) => request({ url: '/contracts', method: 'get', params })
export const contractGet = (id) => request({ url: `/contracts/${id}`, method: 'get' })
export const contractCreate = (data) => request({ url: '/contracts', method: 'post', data })
export const contractUpdate = (data) => request({ url: '/contracts', method: 'put', data })
export const contractDelete = (id) => request({ url: `/contracts/${id}`, method: 'delete' })
