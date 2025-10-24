package com.liwq.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.dto.BorrowDTO;
import com.liwq.bookmanager.dto.BorrowDetailDTO;
import com.liwq.bookmanager.dto.BorrowStatisticsDTO;
import com.liwq.bookmanager.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 借阅控制器
 */
@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * 借阅图书
     */
    @PostMapping
    public Result<Void> borrowBook(@Valid @RequestBody BorrowDTO borrowDTO) {
        borrowService.borrowBook(borrowDTO.getBookId());
        return Result.success();
    }

    /**
     * 归还图书
     */
    @PutMapping("/{id}/return")
    public Result<Void> returnBook(@PathVariable Long id) {
        borrowService.returnBook(id);
        return Result.success();
    }

    /**
     * 续借图书
     */
    @PutMapping("/{id}/renew")
    public Result<Void> renewBook(@PathVariable Long id) {
        borrowService.renewBook(id);
        return Result.success();
    }

    /**
     * 获取我的借阅记录
     */
    @GetMapping("/my")
    public Result<Page<BorrowDetailDTO>> getMyBorrowRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status) {
        Page<BorrowDetailDTO> page = borrowService.getMyBorrowRecords(pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 获取所有借阅记录（管理员）
     */
    @GetMapping
    @RequireAdmin
    public Result<Page<BorrowDetailDTO>> getAllBorrowRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String status) {
        Page<BorrowDetailDTO> page = borrowService.getAllBorrowRecords(pageNum, pageSize, userId, bookId, status);
        return Result.success(page);
    }

    /**
     * 获取借阅详情
     */
    @GetMapping("/{id}")
    public Result<BorrowDetailDTO> getBorrowDetail(@PathVariable Long id) {
        BorrowDetailDTO detail = borrowService.getBorrowDetail(id);
        return Result.success(detail);
    }

    /**
     * 获取逾期记录（管理员）
     */
    @GetMapping("/overdue")
    @RequireAdmin
    public Result<Page<BorrowDetailDTO>> getOverdueRecords(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<BorrowDetailDTO> page = borrowService.getOverdueRecords(pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取借阅统计数据（管理员）
     */
    @GetMapping("/statistics")
    @RequireAdmin
    public Result<BorrowStatisticsDTO> getBorrowStatistics() {
        BorrowStatisticsDTO statistics = borrowService.getBorrowStatistics();
        return Result.success(statistics);
    }
}
