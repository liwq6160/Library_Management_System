package com.liwq.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.dto.CategoryDTO;
import com.liwq.bookmanager.model.BookCategory;
import com.liwq.bookmanager.service.BookCategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 图书分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final BookCategoryService bookCategoryService;

    public CategoryController(BookCategoryService bookCategoryService) {
        this.bookCategoryService = bookCategoryService;
    }

    /**
     * 添加分类（管理员）
     */
    @PostMapping
    @RequireAdmin
    public Result<Void> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        bookCategoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteCategory(@PathVariable Long id) {
        bookCategoryService.deleteCategory(id);
        return Result.success();
    }

    /**
     * 更新分类（管理员）
     */
    @PutMapping("/{id}")
    @RequireAdmin
    public Result<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        bookCategoryService.updateCategory(id, categoryDTO);
        return Result.success();
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<BookCategory> getCategoryById(@PathVariable Long id) {
        BookCategory category = bookCategoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 分页查询分类列表
     */
    @GetMapping
    public Result<Page<BookCategory>> getCategoryList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String categoryName) {
        Page<BookCategory> page = bookCategoryService.getCategoryList(pageNum, pageSize, categoryName);
        return Result.success(page);
    }

    /**
     * 获取所有分类（不分页，用于下拉选择）
     */
    @GetMapping("/all")
    public Result<List<BookCategory>> getAllCategories() {
        List<BookCategory> categories = bookCategoryService.getAllCategories();
        return Result.success(categories);
    }
}
