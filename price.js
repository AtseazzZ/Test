import request from '@/utils/request'

export const pricePage = (params) => request({ url: '/prices', method: 'get', params })
export const priceGet = (id) => request({ url: `/prices/${id}`, method: 'get' })
export const priceCreate = (data) => request({ url: '/prices', method: 'post', data })
export const priceUpdate = (data) => request({ url: '/prices', method: 'put', data })
export const priceDelete = (id) => request({ url: `/prices/${id}`, method: 'delete' })
export const priceAdjustmentPage = (params) => request({ url: '/prices/adjustments', method: 'get', params })
export const priceSubmitAdjustment = (data) => request({ url: '/prices/adjustments', method: 'post', data })
export const priceApproveAdjustment = (id, approved) => request({ url: `/prices/adjustments/${id}`, method: 'put', params: { approved } })
