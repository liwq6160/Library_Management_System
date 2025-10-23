package com.liwq.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.BookDTO;
import com.liwq.bookmanager.dto.BookDetailDTO;
import com.liwq.bookmanager.mapper.BookCategoryMapper;
import com.liwq.bookmanager.mapper.BookMapper;
import com.liwq.bookmanager.model.Book;
import com.liwq.bookmanager.model.BookCategory;
import com.liwq.bookmanager.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 图书服务实现
 */
@Service
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookCategoryMapper bookCategoryMapper;

    @Value("${file.upload.path:uploads/covers}")
    private String uploadPath;

    public BookServiceImpl(BookMapper bookMapper, BookCategoryMapper bookCategoryMapper) {
        this.bookMapper = bookMapper;
        this.bookCategoryMapper = bookCategoryMapper;
    }

    @Override
    public void addBook(BookDTO bookDTO) {
        // 验证分类是否存在
        BookCategory category = bookCategoryMapper.selectById(bookDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("图书分类不存在");
        }

        // 检查ISBN是否已存在
        if (StringUtils.hasText(bookDTO.getIsbn())) {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Book::getIsbn, bookDTO.getIsbn());
            Long count = bookMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException("ISBN已存在");
            }
        }

        // 验证可借数量不能大于馆藏总数
        if (bookDTO.getAvailableCount() > bookDTO.getTotalCount()) {
            throw new RuntimeException("可借数量不能大于馆藏总数");
        }

        Book book = new Book();
        BeanUtils.copyProperties(bookDTO, book);
        bookMapper.insert(book);
    }

    @Override
    public void deleteBook(Long id) {
        // 检查图书是否存在
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // TODO: 检查是否有未归还的借阅记录
        // 这部分需要在借阅模块开发后实现
        // LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq(BorrowRecord::getBookId, id)
        //        .eq(BorrowRecord::getReturnStatus, 0);
        // Long count = borrowRecordMapper.selectCount(wrapper);
        // if (count > 0) {
        //     throw new RuntimeException("该图书存在未归还的借阅记录，无法删除");
        // }

        // 软删除：将状态设置为下架
        book.setStatus(0);
        bookMapper.updateById(book);
    }

    @Override
    public void deleteBatchBooks(List<Long> ids) {
        for (Long id : ids) {
            deleteBook(id);
        }
    }

    @Override
    public void updateBook(Long id, BookDTO bookDTO) {
        // 检查图书是否存在
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 验证分类是否存在
        BookCategory category = bookCategoryMapper.selectById(bookDTO.getCategoryId());
        if (category == null) {
            throw new RuntimeException("图书分类不存在");
        }

        // 检查ISBN是否与其他图书重复
        if (StringUtils.hasText(bookDTO.getIsbn())) {
            LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Book::getIsbn, bookDTO.getIsbn())
                    .ne(Book::getId, id);
            Long count = bookMapper.selectCount(wrapper);
            if (count > 0) {
                throw new RuntimeException("ISBN已存在");
            }
        }

        // 验证可借数量不能大于馆藏总数
        if (bookDTO.getAvailableCount() > bookDTO.getTotalCount()) {
            throw new RuntimeException("可借数量不能大于馆藏总数");
        }

        BeanUtils.copyProperties(bookDTO, book);
        book.setId(id);
        bookMapper.updateById(book);
    }

    @Override
    public BookDetailDTO getBookById(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        BookDetailDTO detailDTO = new BookDetailDTO();
        BeanUtils.copyProperties(book, detailDTO);

        // 获取分类名称
        BookCategory category = bookCategoryMapper.selectById(book.getCategoryId());
        if (category != null) {
            detailDTO.setCategoryName(category.getCategoryName());
        }

        return detailDTO;
    }

    @Override
    public Page<BookDetailDTO> getBookList(int pageNum, int pageSize, String bookName,
                                            String author, String publisher, Long categoryId, Integer status) {
        Page<Book> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(bookName)) {
            wrapper.like(Book::getBookName, bookName);
        }
        if (StringUtils.hasText(author)) {
            wrapper.like(Book::getAuthor, author);
        }
        if (StringUtils.hasText(publisher)) {
            wrapper.like(Book::getPublisher, publisher);
        }
        if (categoryId != null) {
            wrapper.eq(Book::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(Book::getStatus, status);
        }

        wrapper.orderByDesc(Book::getCreateTime);

        Page<Book> bookPage = bookMapper.selectPage(page, wrapper);

        // 转换为BookDetailDTO
        Page<BookDetailDTO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setTotal(bookPage.getTotal());

        List<BookDetailDTO> detailList = new ArrayList<>();
        for (Book book : bookPage.getRecords()) {
            BookDetailDTO detailDTO = new BookDetailDTO();
            BeanUtils.copyProperties(book, detailDTO);

            // 获取分类名称
            BookCategory category = bookCategoryMapper.selectById(book.getCategoryId());
            if (category != null) {
                detailDTO.setCategoryName(category.getCategoryName());
            }

            detailList.add(detailDTO);
        }
        resultPage.setRecords(detailList);

        return resultPage;
    }

    @Override
    public void updateBookStatus(Long id, Integer status) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        book.setStatus(status);
        bookMapper.updateById(book);
    }

    @Override
    public String uploadCover(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!extension.matches("\\.(jpg|jpeg|png|gif)$")) {
            throw new RuntimeException("只支持图片格式：jpg、jpeg、png、gif");
        }

        // 生成新文件名
        String newFilename = UUID.randomUUID().toString() + extension;

        try {
            // 创建上传目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 返回相对路径
            return uploadPath + File.separator + newFilename;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public void increaseAvailableCount(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        if (book.getAvailableCount() >= book.getTotalCount()) {
            throw new RuntimeException("可借数量已达到馆藏总数");
        }

        book.setAvailableCount(book.getAvailableCount() + 1);
        bookMapper.updateById(book);
    }

    @Override
    public void decreaseAvailableCount(Long bookId) {
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        if (book.getAvailableCount() <= 0) {
            throw new RuntimeException("该图书已无可借数量");
        }

        book.setAvailableCount(book.getAvailableCount() - 1);
        bookMapper.updateById(book);
    }
}
