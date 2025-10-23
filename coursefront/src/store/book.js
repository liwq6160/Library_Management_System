import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as bookApi from '@/api/book'

export const useBookStore = defineStore('book', () => {
  const books = ref([])
  const currentBook = ref(null)

  /**
   * 获取图书列表
   */
  const fetchBookList = async (params) => {
    try {
      const res = await bookApi.getBookList(params)
      books.value = res.data.records
      return res.data
    } catch (error) {
      console.error('获取图书列表失败:', error)
      throw error
    }
  }

  /**
   * 获取图书详情
   */
  const fetchBookDetail = async (id) => {
    try {
      const res = await bookApi.getBookById(id)
      currentBook.value = res.data
      return res.data
    } catch (error) {
      console.error('获取图书详情失败:', error)
      throw error
    }
  }

  /**
   * 添加图书
   */
  const addBook = async (data) => {
    try {
      await bookApi.addBook(data)
    } catch (error) {
      console.error('添加图书失败:', error)
      throw error
    }
  }

  /**
   * 更新图书
   */
  const updateBook = async (id, data) => {
    try {
      await bookApi.updateBook(id, data)
    } catch (error) {
      console.error('更新图书失败:', error)
      throw error
    }
  }

  /**
   * 删除图书
   */
  const deleteBook = async (id) => {
    try {
      await bookApi.deleteBook(id)
    } catch (error) {
      console.error('删除图书失败:', error)
      throw error
    }
  }

  /**
   * 批量删除图书
   */
  const deleteBatchBooks = async (ids) => {
    try {
      await bookApi.deleteBatchBooks(ids)
    } catch (error) {
      console.error('批量删除图书失败:', error)
      throw error
    }
  }

  /**
   * 更新图书状态
   */
  const updateBookStatus = async (id, status) => {
    try {
      await bookApi.updateBookStatus(id, status)
    } catch (error) {
      console.error('更新图书状态失败:', error)
      throw error
    }
  }

  /**
   * 上传图书封面
   */
  const uploadCover = async (file) => {
    try {
      const res = await bookApi.uploadCover(file)
      return res.data
    } catch (error) {
      console.error('上传图书封面失败:', error)
      throw error
    }
  }

  return {
    books,
    currentBook,
    fetchBookList,
    fetchBookDetail,
    addBook,
    updateBook,
    deleteBook,
    deleteBatchBooks,
    updateBookStatus,
    uploadCover
  }
})
