package com.liwq.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.dto.BookDTO;
import com.liwq.bookmanager.dto.BookDetailDTO;
import com.liwq.bookmanager.service.BookService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图书控制器
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * 添加图书（管理员）
     */
    @PostMapping
    @RequireAdmin
    public Result<Void> addBook(@Valid @RequestBody BookDTO bookDTO) {
        bookService.addBook(bookDTO);
        return Result.success();
    }

    /**
     * 删除图书（管理员）
     */
    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.success();
    }

    /**
     * 批量删除图书（管理员）
     */
    @DeleteMapping("/batch")
    @RequireAdmin
    public Result<Void> deleteBatchBooks(@RequestBody List<Long> ids) {
        bookService.deleteBatchBooks(ids);
        return Result.success();
    }

    /**
     * 更新图书（管理员）
     */
    @PutMapping("/{id}")
    @RequireAdmin
    public Result<Void> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
        bookService.updateBook(id, bookDTO);
        return Result.success();
    }

    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Result<BookDetailDTO> getBookById(@PathVariable Long id) {
        BookDetailDTO book = bookService.getBookById(id);
        return Result.success(book);
    }

    /**
     * 分页查询图书列表
     */
    @GetMapping
    public Result<Page<BookDetailDTO>> getBookList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        Page<BookDetailDTO> page = bookService.getBookList(pageNum, pageSize, bookName, author, publisher, categoryId, status);
        return Result.success(page);
    }

    /**
     * 更新图书状态（管理员）
     */
    @PatchMapping("/{id}/status")
    @RequireAdmin
    public Result<Void> updateBookStatus(@PathVariable Long id, @RequestParam Integer status) {
        bookService.updateBookStatus(id, status);
        return Result.success();
    }

    /**
     * 上传图书封面（管理员）
     */
    @PostMapping("/upload-cover")
    @RequireAdmin
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        String coverUrl = bookService.uploadCover(file);
        return Result.success(coverUrl);
    }
}
