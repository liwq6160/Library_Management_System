package com.liwq.bookmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录详情DTO
 */
@Data
public class BorrowDetailDTO {

    private Long id;

    private Long userId;

    private String username;

    private String realName;

    private Long bookId;

    private String bookName;

    private String author;

    private String categoryName;

    private LocalDateTime borrowDate;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private Integer renewCount;

    private Integer overdueDays;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
