import request from '@/utils/request'

export const supplierPage = (params) => request({ url: '/suppliers', method: 'get', params })
export const supplierAll = () => request({ url: '/suppliers/all', method: 'get' })
export const supplierByType = (type) => request({ url: `/suppliers/type/${type}`, method: 'get' })
export const supplierGet = (id) => request({ url: `/suppliers/${id}`, method: 'get' })
export const supplierCreate = (data) => request({ url: '/suppliers', method: 'post', data })
export const supplierUpdate = (data) => request({ url: '/suppliers', method: 'put', data })
export const supplierDelete = (id) => request({ url: `/suppliers/${id}`, method: 'delete' })
