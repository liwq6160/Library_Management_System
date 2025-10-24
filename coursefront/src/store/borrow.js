import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as borrowApi from '@/api/borrow'

export const useBorrowStore = defineStore('borrow', () => {
  const borrowRecords = ref([])
  const currentRecord = ref(null)
  const statistics = ref(null)

  /**
   * 借阅图书
   */
  const borrowBook = async (bookId) => {
    try {
      await borrowApi.borrowBook({ bookId })
    } catch (error) {
      console.error('借阅图书失败:', error)
      throw error
    }
  }

  /**
   * 归还图书
   */
  const returnBook = async (id) => {
    try {
      await borrowApi.returnBook(id)
    } catch (error) {
      console.error('归还图书失败:', error)
      throw error
    }
  }

  /**
   * 续借图书
   */
  const renewBook = async (id) => {
    try {
      await borrowApi.renewBook(id)
    } catch (error) {
      console.error('续借图书失败:', error)
      throw error
    }
  }

  /**
   * 获取我的借阅记录
   */
  const fetchMyBorrowRecords = async (params) => {
    try {
      const res = await borrowApi.getMyBorrowRecords(params)
      borrowRecords.value = res.data.records
      return res.data
    } catch (error) {
      console.error('获取借阅记录失败:', error)
      throw error
    }
  }

  /**
   * 获取所有借阅记录（管理员）
   */
  const fetchAllBorrowRecords = async (params) => {
    try {
      const res = await borrowApi.getAllBorrowRecords(params)
      borrowRecords.value = res.data.records
      return res.data
    } catch (error) {
      console.error('获取借阅记录失败:', error)
      throw error
    }
  }

  /**
   * 获取借阅详情
   */
  const fetchBorrowDetail = async (id) => {
    try {
      const res = await borrowApi.getBorrowDetail(id)
      currentRecord.value = res.data
      return res.data
    } catch (error) {
      console.error('获取借阅详情失败:', error)
      throw error
    }
  }

  /**
   * 获取逾期记录（管理员）
   */
  const fetchOverdueRecords = async (params) => {
    try {
      const res = await borrowApi.getOverdueRecords(params)
      return res.data
    } catch (error) {
      console.error('获取逾期记录失败:', error)
      throw error
    }
  }

  /**
   * 获取借阅统计数据（管理员）
   */
  const fetchBorrowStatistics = async () => {
    try {
      const res = await borrowApi.getBorrowStatistics()
      statistics.value = res.data
      return res.data
    } catch (error) {
      console.error('获取借阅统计失败:', error)
      throw error
    }
  }

  return {
    borrowRecords,
    currentRecord,
    statistics,
    borrowBook,
    returnBook,
    renewBook,
    fetchMyBorrowRecords,
    fetchAllBorrowRecords,
    fetchBorrowDetail,
    fetchOverdueRecords,
    fetchBorrowStatistics
  }
})
