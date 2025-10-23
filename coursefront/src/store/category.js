import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as categoryApi from '@/api/category'

export const useCategoryStore = defineStore('category', () => {
  const categories = ref([])
  const allCategories = ref([])

  /**
   * 获取所有分类（不分页）
   */
  const fetchAllCategories = async () => {
    try {
      const res = await categoryApi.getAllCategories()
      allCategories.value = res.data
      return res.data
    } catch (error) {
      console.error('获取分类列表失败:', error)
      throw error
    }
  }

  /**
   * 获取分类列表（分页）
   */
  const fetchCategoryList = async (params) => {
    try {
      const res = await categoryApi.getCategoryList(params)
      categories.value = res.data.records
      return res.data
    } catch (error) {
      console.error('获取分类列表失败:', error)
      throw error
    }
  }

  /**
   * 添加分类
   */
  const addCategory = async (data) => {
    try {
      await categoryApi.addCategory(data)
    } catch (error) {
      console.error('添加分类失败:', error)
      throw error
    }
  }

  /**
   * 更新分类
   */
  const updateCategory = async (id, data) => {
    try {
      await categoryApi.updateCategory(id, data)
    } catch (error) {
      console.error('更新分类失败:', error)
      throw error
    }
  }

  /**
   * 删除分类
   */
  const deleteCategory = async (id) => {
    try {
      await categoryApi.deleteCategory(id)
    } catch (error) {
      console.error('删除分类失败:', error)
      throw error
    }
  }

  return {
    categories,
    allCategories,
    fetchAllCategories,
    fetchCategoryList,
    addCategory,
    updateCategory,
    deleteCategory
  }
})
