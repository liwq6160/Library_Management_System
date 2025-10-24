package com.liwq.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.BorrowDetailDTO;
import com.liwq.bookmanager.dto.BorrowStatisticsDTO;

/**
 * 借阅服务接口
 */
public interface BorrowService {

    /**
     * 借阅图书
     */
    void borrowBook(Long bookId);

    /**
     * 归还图书
     */
    void returnBook(Long id);

    /**
     * 续借图书
     */
    void renewBook(Long id);

    /**
     * 获取我的借阅记录
     */
    Page<BorrowDetailDTO> getMyBorrowRecords(int pageNum, int pageSize, String status);

    /**
     * 获取所有借阅记录（管理员）
     */
    Page<BorrowDetailDTO> getAllBorrowRecords(int pageNum, int pageSize, Long userId, Long bookId, String status);

    /**
     * 获取借阅详情
     */
    BorrowDetailDTO getBorrowDetail(Long id);

    /**
     * 获取逾期记录（管理员）
     */
    Page<BorrowDetailDTO> getOverdueRecords(int pageNum, int pageSize);

    /**
     * 获取借阅统计数据（管理员）
     */
    BorrowStatisticsDTO getBorrowStatistics();

    /**
     * 更新逾期记录（定时任务调用）
     */
    void updateOverdueRecords();
}
