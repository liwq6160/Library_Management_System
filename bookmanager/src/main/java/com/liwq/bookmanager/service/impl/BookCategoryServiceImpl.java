package com.liwq.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.CategoryDTO;
import com.liwq.bookmanager.mapper.BookCategoryMapper;
import com.liwq.bookmanager.mapper.BookMapper;
import com.liwq.bookmanager.model.Book;
import com.liwq.bookmanager.model.BookCategory;
import com.liwq.bookmanager.service.BookCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 图书分类服务实现
 */
@Service
public class BookCategoryServiceImpl implements BookCategoryService {

    private final BookCategoryMapper bookCategoryMapper;
    private final BookMapper bookMapper;

    public BookCategoryServiceImpl(BookCategoryMapper bookCategoryMapper, BookMapper bookMapper) {
        this.bookCategoryMapper = bookCategoryMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getCategoryName, categoryDTO.getCategoryName());
        Long count = bookCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        BookCategory category = new BookCategory();
        BeanUtils.copyProperties(categoryDTO, category);
        bookCategoryMapper.insert(category);
    }

    @Override
    public void deleteCategory(Long id) {
        // 检查是否存在
        BookCategory category = bookCategoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查该分类下是否有图书
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Book::getCategoryId, id);
        Long count = bookMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该分类下存在图书，无法删除");
        }

        bookCategoryMapper.deleteById(id);
    }

    @Override
    public void updateCategory(Long id, CategoryDTO categoryDTO) {
        // 检查是否存在
        BookCategory category = bookCategoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }

        // 检查分类名称是否与其他分类重复
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookCategory::getCategoryName, categoryDTO.getCategoryName())
                .ne(BookCategory::getId, id);
        Long count = bookCategoryMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("分类名称已存在");
        }

        BeanUtils.copyProperties(categoryDTO, category);
        category.setId(id);
        bookCategoryMapper.updateById(category);
    }

    @Override
    public BookCategory getCategoryById(Long id) {
        BookCategory category = bookCategoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        return category;
    }

    @Override
    public Page<BookCategory> getCategoryList(int pageNum, int pageSize, String categoryName) {
        Page<BookCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(categoryName)) {
            wrapper.like(BookCategory::getCategoryName, categoryName);
        }

        wrapper.orderByAsc(BookCategory::getSortOrder)
                .orderByDesc(BookCategory::getCreateTime);

        return bookCategoryMapper.selectPage(page, wrapper);
    }

    @Override
    public List<BookCategory> getAllCategories() {
        LambdaQueryWrapper<BookCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(BookCategory::getSortOrder)
                .orderByDesc(BookCategory::getCreateTime);
        return bookCategoryMapper.selectList(wrapper);
    }
}
