import request from '@/utils/request'

/**
 * 预约图书
 */
export const reserveBook = (data) => {
  return request({
    url: '/api/reservations',
    method: 'post',
    data
  })
}

/**
 * 取消预约
 */
export const cancelReservation = (id) => {
  return request({
    url: `/api/reservations/${id}/cancel`,
    method: 'put'
  })
}

/**
 * 获取我的预约记录
 */
export const getMyReservations = (params) => {
  return request({
    url: '/api/reservations/my',
    method: 'get',
    params
  })
}

/**
 * 获取所有预约记录（管理员）
 */
export const getAllReservations = (params) => {
  return request({
    url: '/api/reservations',
    method: 'get',
    params
  })
}

/**
 * 获取预约详情
 */
export const getReservationDetail = (id) => {
  return request({
    url: `/api/reservations/${id}`,
    method: 'get'
  })
}

/**
 * 审核通过预约（管理员）
 */
export const approveReservation = (id) => {
  return request({
    url: `/api/reservations/${id}/approve`,
    method: 'put'
  })
}

/**
 * 拒绝预约（管理员）
 */
export const rejectReservation = (id, remark) => {
  return request({
    url: `/api/reservations/${id}/reject`,
    method: 'put',
    params: { remark }
  })
}
