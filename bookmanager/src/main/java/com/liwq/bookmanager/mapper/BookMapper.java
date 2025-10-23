package com.liwq.bookmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liwq.bookmanager.model.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书Mapper
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
