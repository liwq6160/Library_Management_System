package com.liwq.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.BookDTO;
import com.liwq.bookmanager.dto.BookDetailDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService {

    /**
     * 添加图书
     */
    void addBook(BookDTO bookDTO);

    /**
     * 删除图书
     */
    void deleteBook(Long id);

    /**
     * 批量删除图书
     */
    void deleteBatchBooks(List<Long> ids);

    /**
     * 更新图书
     */
    void updateBook(Long id, BookDTO bookDTO);

    /**
     * 获取图书详情
     */
    BookDetailDTO getBookById(Long id);

    /**
     * 分页查询图书列表
     */
    Page<BookDetailDTO> getBookList(int pageNum, int pageSize, String bookName,
                                     String author, String publisher, Long categoryId, Integer status);

    /**
     * 更新图书状态
     */
    void updateBookStatus(Long id, Integer status);

    /**
     * 上传图书封面
     */
    String uploadCover(MultipartFile file);

    /**
     * 增加可借数量（归还时调用）
     */
    void increaseAvailableCount(Long bookId);

    /**
     * 减少可借数量（借阅时调用）
     */
    void decreaseAvailableCount(Long bookId);
}
