import request from '@/utils/request'

export const nodePage = (params) => request({ url: '/logistics-nodes', method: 'get', params })
export const nodeListByOrder = (orderId) => request({ url: `/logistics-nodes/order/${orderId}`, method: 'get' })
export const nodeAdd = (data) => request({ url: '/logistics-nodes', method: 'post', data })
export const nodeDelete = (id) => request({ url: `/logistics-nodes/${id}`, method: 'delete' })

export const abnormalPage = (params) => request({ url: '/abnormals', method: 'get', params })
export const abnormalListByOrder = (orderId) => request({ url: `/abnormals/order/${orderId}`, method: 'get' })
export const abnormalListPending = () => request({ url: '/abnormals/pending', method: 'get' })
export const abnormalGet = (id) => request({ url: `/abnormals/${id}`, method: 'get' })
export const abnormalCreate = (data) => request({ url: '/abnormals', method: 'post', data })
export const abnormalDecide = (data) => request({ url: '/abnormals/decision', method: 'put', data })
export const abnormalPushFinance = (id) => request({ url: `/abnormals/${id}/push-finance`, method: 'post' })
export const abnormalDelete = (id) => request({ url: `/abnormals/${id}`, method: 'delete' })
