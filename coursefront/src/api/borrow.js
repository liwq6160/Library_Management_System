import request from '@/utils/request'

/**
 * 借阅图书
 */
export const borrowBook = (data) => {
  return request({
    url: '/api/borrows',
    method: 'post',
    data
  })
}

/**
 * 归还图书
 */
export const returnBook = (id) => {
  return request({
    url: `/api/borrows/${id}/return`,
    method: 'put'
  })
}

/**
 * 续借图书
 */
export const renewBook = (id) => {
  return request({
    url: `/api/borrows/${id}/renew`,
    method: 'put'
  })
}

/**
 * 获取我的借阅记录
 */
export const getMyBorrowRecords = (params) => {
  return request({
    url: '/api/borrows/my',
    method: 'get',
    params
  })
}

/**
 * 获取所有借阅记录（管理员）
 */
export const getAllBorrowRecords = (params) => {
  return request({
    url: '/api/borrows',
    method: 'get',
    params
  })
}

/**
 * 获取借阅详情
 */
export const getBorrowDetail = (id) => {
  return request({
    url: `/api/borrows/${id}`,
    method: 'get'
  })
}

/**
 * 获取逾期记录（管理员）
 */
export const getOverdueRecords = (params) => {
  return request({
    url: '/api/borrows/overdue',
    method: 'get',
    params
  })
}

/**
 * 获取借阅统计数据（管理员）
 */
export const getBorrowStatistics = () => {
  return request({
    url: '/api/borrows/statistics',
    method: 'get'
  })
}
