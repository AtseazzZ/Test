import request from '@/utils/request'

export const customerPage = (params) => request({ url: '/customers', method: 'get', params })
export const customerAll = () => request({ url: '/customers/all', method: 'get' })
export const customerGet = (id) => request({ url: `/customers/${id}`, method: 'get' })
export const customerCreate = (data) => request({ url: '/customers', method: 'post', data })
export const customerUpdate = (data) => request({ url: '/customers', method: 'put', data })
export const customerDelete = (id) => request({ url: `/customers/${id}`, method: 'delete' })
