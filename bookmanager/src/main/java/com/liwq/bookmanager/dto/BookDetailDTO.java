package com.liwq.bookmanager.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书详情DTO（返回给前端）
 */
@Data
public class BookDetailDTO {

    private Long id;

    private String bookName;

    private String author;

    private String publisher;

    private String isbn;

    private Long categoryId;

    private String categoryName;

    private Integer totalCount;

    private Integer availableCount;

    private BigDecimal price;

    private LocalDate publishDate;

    private String coverImage;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
