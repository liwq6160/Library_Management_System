import request from '@/utils/request'

/**
 * 添加分类
 */
export const addCategory = (data) => {
  return request({
    url: '/api/categories',
    method: 'post',
    data
  })
}

/**
 * 删除分类
 */
export const deleteCategory = (id) => {
  return request({
    url: `/api/categories/${id}`,
    method: 'delete'
  })
}

/**
 * 更新分类
 */
export const updateCategory = (id, data) => {
  return request({
    url: `/api/categories/${id}`,
    method: 'put',
    data
  })
}

/**
 * 获取分类详情
 */
export const getCategoryById = (id) => {
  return request({
    url: `/api/categories/${id}`,
    method: 'get'
  })
}

/**
 * 分页查询分类列表
 */
export const getCategoryList = (params) => {
  return request({
    url: '/api/categories',
    method: 'get',
    params
  })
}

/**
 * 获取所有分类（不分页）
 */
export const getAllCategories = () => {
  return request({
    url: '/api/categories/all',
    method: 'get'
  })
}
