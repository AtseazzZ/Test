import request from '@/utils/request'

export const prepaymentPage = (params) => request({ url: '/prepayments', method: 'get', params })
export const prepaymentGet = (id) => request({ url: `/prepayments/${id}`, method: 'get' })
export const prepaymentCreate = (data) => request({ url: '/prepayments', method: 'post', data })
export const prepaymentGmApprove = (data) => request({ url: '/prepayments/gm-approve', method: 'put', data })
export const prepaymentFinanceVisible = () => request({ url: '/prepayments/finance-visible', method: 'get' })
export const prepaymentDelete = (id) => request({ url: `/prepayments/${id}`, method: 'delete' })

export const statementPage = (params) => request({ url: '/statements', method: 'get', params })
export const statementGet = (id) => request({ url: `/statements/${id}`, method: 'get' })
export const statementDetails = (id) => request({ url: `/statements/${id}/details`, method: 'get' })
export const statementGenerate = (data) => request({ url: '/statements/generate', method: 'post', data })
export const statementBusinessConfirm = (id, operator) => request({ url: `/statements/${id}/business-confirm`, method: 'put', params: { operator } })
export const statementRecordObjection = (id, params) => request({ url: `/statements/${id}/objection`, method: 'put', params })
export const statementPushFinance = (id) => request({ url: `/statements/${id}/push-finance`, method: 'post' })
export const statementDelete = (id) => request({ url: `/statements/${id}`, method: 'delete' })

export const dashboardSummary = () => request({ url: '/dashboard/summary', method: 'get' })

export const fileUpload = (formData) => request({ url: '/files/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
