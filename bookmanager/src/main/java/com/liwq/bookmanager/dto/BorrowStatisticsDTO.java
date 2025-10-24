package com.liwq.bookmanager.dto;

import lombok.Data;

/**
 * 借阅统计DTO
 */
@Data
public class BorrowStatisticsDTO {

    private Long totalBorrows;

    private Long currentBorrowing;

    private Long overdueCount;

    private Long returnedCount;

    private Long totalUsers;

    private Long activeUsers;
}
