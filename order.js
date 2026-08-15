import request from '@/utils/request'

export const orderPage = (params) => request({ url: '/orders', method: 'get', params })
export const orderDetail = (id) => request({ url: `/orders/${id}`, method: 'get' })
export const orderSegments = (id) => request({ url: `/orders/${id}/segments`, method: 'get' })
export const orderCreate = (data) => request({ url: '/orders', method: 'post', data })
export const orderUpdate = (data) => request({ url: '/orders', method: 'put', data })
export const orderDelete = (id) => request({ url: `/orders/${id}`, method: 'delete' })
export const orderUpdateStatus = (id, status) => request({ url: `/orders/${id}/status`, method: 'put', params: { status } })
export const orderPushDispatch = (id) => request({ url: `/orders/${id}/push-dispatch`, method: 'post' })
export const orderPricingPreview = (data) => request({ url: '/orders/pricing-preview', method: 'post', data })
