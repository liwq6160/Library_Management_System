package com.liwq.bookmanager.task;

import com.liwq.bookmanager.service.BorrowService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 借阅相关定时任务
 */
@Component
public class BorrowTask {

    private final BorrowService borrowService;

    public BorrowTask(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 每天凌晨1点更新逾期记录
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateOverdueRecords() {
        borrowService.updateOverdueRecords();
    }
}
