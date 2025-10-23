import request from '@/utils/request'

/**
 * 添加图书
 */
export const addBook = (data) => {
  return request({
    url: '/api/books',
    method: 'post',
    data
  })
}

/**
 * 删除图书
 */
export const deleteBook = (id) => {
  return request({
    url: `/api/books/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除图书
 */
export const deleteBatchBooks = (ids) => {
  return request({
    url: '/api/books/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 更新图书
 */
export const updateBook = (id, data) => {
  return request({
    url: `/api/books/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取图书详情
 */
export const getBookById = (id) => {
  return request({
    url: `/api/books/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询图书列表
 */
export const getBookList = (params) => {
  return request({
    url: '/api/books',
    method: 'get',
    params
  })
}

/**
 * 更新图书状态
 */
export const updateBookStatus = (id, status) => {
  return request({
    url: `/api/books/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

/**
 * 上传图书封面
 */
export const uploadCover = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/books/upload-cover',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
