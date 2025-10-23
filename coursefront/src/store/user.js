import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import userApi from '@/api/user'
import { ElMessage } from 'element-plus'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const username = computed(() => userInfo.value?.username || '')
  const realName = computed(() => userInfo.value?.realName || '')

  // 登录
  const login = async (loginForm) => {
    try {
      const res = await userApi.login(loginForm)
      token.value = res.data.token
      userInfo.value = res.data

      // 保存到localStorage
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data))

      ElMessage.success('登录成功')
      return true
    } catch (error) {
      console.error('登录失败：', error)
      return false
    }
  }

  // 注册
  const register = async (registerForm) => {
    try {
      await userApi.register(registerForm)
      ElMessage.success('注册成功，请登录')
      return true
    } catch (error) {
      console.error('注册失败：', error)
      return false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await userApi.logout()
    } catch (error) {
      console.error('登出失败：', error)
    } finally {
      // 清除状态
      token.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')

      ElMessage.success('已退出登录')
      router.push('/login')
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const res = await userApi.getProfile()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
      return res.data
    } catch (error) {
      console.error('获取用户信息失败：', error)
      return null
    }
  }

  // 更新用户信息
  const updateUserInfo = async (data) => {
    try {
      await userApi.updateProfile(data)
      await getUserInfo()
      ElMessage.success('信息修改成功')
      return true
    } catch (error) {
      console.error('更新用户信息失败：', error)
      return false
    }
  }

  // 修改密码
  const updatePassword = async (data) => {
    try {
      await userApi.updatePassword(data)
      ElMessage.success('密码修改成功，请重新登录')
      await logout()
      return true
    } catch (error) {
      console.error('修改密码失败：', error)
      return false
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    realName,
    login,
    register,
    logout,
    getUserInfo,
    updateUserInfo,
    updatePassword
  }
})
