package com.liwq.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liwq.bookmanager.model.BookCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书分类Mapper
 */
@Mapper
public interface BookCategoryMapper extends BaseMapper<BookCategory> {
}
