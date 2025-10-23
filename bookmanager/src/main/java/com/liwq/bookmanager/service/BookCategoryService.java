package com.liwq.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.CategoryDTO;
import com.liwq.bookmanager.model.BookCategory;

import java.util.List;

/**
 * 图书分类服务接口
 */
public interface BookCategoryService {

    /**
     * 添加分类
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 更新分类
     */
    void updateCategory(Long id, CategoryDTO categoryDTO);

    /**
     * 获取分类详情
     */
    BookCategory getCategoryById(Long id);

    /**
     * 分页查询分类列表
     */
    Page<BookCategory> getCategoryList(int pageNum, int pageSize, String categoryName);

    /**
     * 获取所有分类（不分页）
     */
    List<BookCategory> getAllCategories();
}
