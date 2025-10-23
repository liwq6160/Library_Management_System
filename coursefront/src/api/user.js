import request from '@/utils/request'

/**
 * 用户API接口
 */
export default {
  /**
   * 用户注册
   */
  register(data) {
    return request({
      url: '/api/users/register',
      method: 'post',
      data
    })
  },

  /**
   * 用户登录
   */
  login(data) {
    return request({
      url: '/api/users/login',
      method: 'post',
      data
    })
  },

  /**
   * 用户登出
   */
  logout() {
    return request({
      url: '/api/users/logout',
      method: 'post'
    })
  },

  /**
   * 获取个人信息
   */
  getProfile() {
    return request({
      url: '/api/users/profile',
      method: 'get'
    })
  },

  /**
   * 修改个人信息
   */
  updateProfile(data) {
    return request({
      url: '/api/users/profile',
      method: 'put',
      data
    })
  },

  /**
   * 修改密码
   */
  updatePassword(data) {
    return request({
      url: '/api/users/password',
      method: 'put',
      data
    })
  },

  /**
   * 获取用户列表（管理员）
   */
  getUserList(params) {
    return request({
      url: '/api/users/list',
      method: 'get',
      params
    })
  },

  /**
   * 获取用户详情（管理员）
   */
  getUserDetail(id) {
    return request({
      url: `/api/users/${id}`,
      method: 'get'
    })
  },

  /**
   * 修改用户信息（管理员）
   */
  updateUser(id, data) {
    return request({
      url: `/api/users/${id}`,
      method: 'put',
      data,
      params: data.role ? { role: data.role } : {}
    })
  },

  /**
   * 修改用户状态（管理员）
   */
  updateUserStatus(id, status) {
    return request({
      url: `/api/users/${id}/status`,
      method: 'put',
      params: { status }
    })
  }
}
