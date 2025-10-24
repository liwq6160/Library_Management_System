package com.liwq.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.BorrowDetailDTO;
import com.liwq.bookmanager.dto.BorrowStatisticsDTO;
import com.liwq.bookmanager.mapper.BorrowRecordMapper;
import com.liwq.bookmanager.mapper.UserMapper;
import com.liwq.bookmanager.model.BorrowRecord;
import com.liwq.bookmanager.model.User;
import com.liwq.bookmanager.service.BorrowService;
import com.liwq.bookmanager.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 借阅服务实现
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordMapper borrowRecordMapper;
    private final BookService bookService;
    private final UserMapper userMapper;

    // 默认借阅期限（天）
    private static final int DEFAULT_BORROW_DAYS = 30;
    // 默认续借期限（天）
    private static final int DEFAULT_RENEW_DAYS = 15;
    // 最大续借次数
    private static final int MAX_RENEW_COUNT = 1;

    public BorrowServiceImpl(BorrowRecordMapper borrowRecordMapper,
                              BookService bookService,
                              UserMapper userMapper) {
        this.borrowRecordMapper = borrowRecordMapper;
        this.bookService = bookService;
        this.userMapper = userMapper;
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("无法获取请求上下文");
        }
        HttpServletRequest request = attributes.getRequest();
        return (Long) request.getAttribute("userId");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void borrowBook(Long bookId) {
        // 获取当前用户
        Long userId = getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用，无法借阅图书");
        }

        // 检查用户是否有逾期未还的图书
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, "overdue");
        Long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);
        if (overdueCount > 0) {
            throw new RuntimeException("您有逾期未还的图书，请先归还后再借阅");
        }

        // 检查用户当前借阅数量
        LambdaQueryWrapper<BorrowRecord> borrowingWrapper = new LambdaQueryWrapper<>();
        borrowingWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getStatus, "borrowing");
        Long borrowingCount = borrowRecordMapper.selectCount(borrowingWrapper);
        if (borrowingCount >= 5) {
            throw new RuntimeException("您当前借阅图书已达上限（5本），请先归还后再借阅");
        }

        // 检查是否已借阅该图书
        LambdaQueryWrapper<BorrowRecord> checkWrapper = new LambdaQueryWrapper<>();
        checkWrapper.eq(BorrowRecord::getUserId, userId)
                .eq(BorrowRecord::getBookId, bookId)
                .eq(BorrowRecord::getStatus, "borrowing");
        Long count = borrowRecordMapper.selectCount(checkWrapper);
        if (count > 0) {
            throw new RuntimeException("您已借阅该图书，不能重复借阅");
        }

        // 减少图书可借数量（会自动检查图书状态和库存）
        bookService.decreaseAvailableCount(bookId);

        // 创建借阅记录
        BorrowRecord record = new BorrowRecord();
        record.setUserId(userId);
        record.setBookId(bookId);
        record.setBorrowDate(LocalDateTime.now());
        record.setDueDate(LocalDateTime.now().plusDays(DEFAULT_BORROW_DAYS));
        record.setRenewCount(0);
        record.setOverdueDays(0);
        record.setStatus("borrowing");

        borrowRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnBook(Long id) {
        // 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        // 检查是否已归还
        if ("returned".equals(record.getStatus())) {
            throw new RuntimeException("该图书已归还");
        }

        // 检查归还权限（只能归还自己的或管理员可归还任何）
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        if (!record.getUserId().equals(currentUserId) && !"admin".equals(currentUser.getRole())) {
            throw new RuntimeException("您无权归还该图书");
        }

        // 设置归还时间
        LocalDateTime now = LocalDateTime.now();
        record.setReturnDate(now);

        // 计算逾期天数
        if (now.isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), now);
            record.setOverdueDays((int) overdueDays);
        } else {
            record.setOverdueDays(0);
        }

        // 更新状态
        record.setStatus("returned");

        // 增加图书可借数量
        bookService.increaseAvailableCount(record.getBookId());

        borrowRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renewBook(Long id) {
        // 查询借阅记录
        BorrowRecord record = borrowRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        // 检查是否为本人借阅
        Long currentUserId = getCurrentUserId();
        if (!record.getUserId().equals(currentUserId)) {
            throw new RuntimeException("只能续借自己的图书");
        }

        // 检查是否已归还
        if ("returned".equals(record.getStatus())) {
            throw new RuntimeException("该图书已归还，无需续借");
        }

        // 检查是否逾期
        if ("overdue".equals(record.getStatus())) {
            throw new RuntimeException("该图书已逾期，请先归还后再借阅");
        }

        // 检查续借次数
        if (record.getRenewCount() >= MAX_RENEW_COUNT) {
            throw new RuntimeException("续借次数已达上限");
        }

        // 延长应还日期
        record.setDueDate(record.getDueDate().plusDays(DEFAULT_RENEW_DAYS));
        record.setRenewCount(record.getRenewCount() + 1);

        borrowRecordMapper.updateById(record);
    }

    @Override
    public Page<BorrowDetailDTO> getMyBorrowRecords(int pageNum, int pageSize, String status) {
        Long userId = getCurrentUserId();
        return getAllBorrowRecords(pageNum, pageSize, userId, null, status);
    }

    @Override
    public Page<BorrowDetailDTO> getAllBorrowRecords(int pageNum, int pageSize, Long userId, Long bookId, String status) {
        int offset = (pageNum - 1) * pageSize;

        List<BorrowDetailDTO> records = borrowRecordMapper.selectBorrowDetailList(
                userId, bookId, status, offset, pageSize);
        Long total = borrowRecordMapper.countBorrowDetails(userId, bookId, status);

        Page<BorrowDetailDTO> page = new Page<>(pageNum, pageSize);
        page.setRecords(records);
        page.setTotal(total);

        return page;
    }

    @Override
    public BorrowDetailDTO getBorrowDetail(Long id) {
        BorrowDetailDTO detail = borrowRecordMapper.selectBorrowDetailById(id);
        if (detail == null) {
            throw new RuntimeException("借阅记录不存在");
        }

        // 检查查看权限（只能查看自己的或管理员可查看任何）
        Long currentUserId = getCurrentUserId();
        User currentUser = userMapper.selectById(currentUserId);
        if (!detail.getUserId().equals(currentUserId) && !"admin".equals(currentUser.getRole())) {
            throw new RuntimeException("您无权查看该借阅记录");
        }

        return detail;
    }

    @Override
    public Page<BorrowDetailDTO> getOverdueRecords(int pageNum, int pageSize) {
        return getAllBorrowRecords(pageNum, pageSize, null, null, "overdue");
    }

    @Override
    public BorrowStatisticsDTO getBorrowStatistics() {
        BorrowStatisticsDTO statistics = new BorrowStatisticsDTO();

        // 总借阅量
        Long totalBorrows = borrowRecordMapper.selectCount(null);
        statistics.setTotalBorrows(totalBorrows);

        // 当前借阅中数量
        LambdaQueryWrapper<BorrowRecord> borrowingWrapper = new LambdaQueryWrapper<>();
        borrowingWrapper.eq(BorrowRecord::getStatus, "borrowing");
        Long currentBorrowing = borrowRecordMapper.selectCount(borrowingWrapper);
        statistics.setCurrentBorrowing(currentBorrowing);

        // 逾期数量
        LambdaQueryWrapper<BorrowRecord> overdueWrapper = new LambdaQueryWrapper<>();
        overdueWrapper.eq(BorrowRecord::getStatus, "overdue");
        Long overdueCount = borrowRecordMapper.selectCount(overdueWrapper);
        statistics.setOverdueCount(overdueCount);

        // 已归还数量
        LambdaQueryWrapper<BorrowRecord> returnedWrapper = new LambdaQueryWrapper<>();
        returnedWrapper.eq(BorrowRecord::getStatus, "returned");
        Long returnedCount = borrowRecordMapper.selectCount(returnedWrapper);
        statistics.setReturnedCount(returnedCount);

        // 总用户数
        Long totalUsers = userMapper.selectCount(null);
        statistics.setTotalUsers(totalUsers);

        // 活跃用户数（有借阅记录的用户）
        // 这里简化处理，统计有借阅中或逾期记录的用户数
        LambdaQueryWrapper<BorrowRecord> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.in(BorrowRecord::getStatus, "borrowing", "overdue")
                .select(BorrowRecord::getUserId)
                .groupBy(BorrowRecord::getUserId);
        Long activeUsers = (long) borrowRecordMapper.selectList(activeWrapper).size();
        statistics.setActiveUsers(activeUsers);

        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOverdueRecords() {
        // 查询所有借阅中的记录
        LambdaQueryWrapper<BorrowRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BorrowRecord::getStatus, "borrowing");
        List<BorrowRecord> records = borrowRecordMapper.selectList(wrapper);

        LocalDateTime now = LocalDateTime.now();

        for (BorrowRecord record : records) {
            // 检查是否逾期
            if (now.isAfter(record.getDueDate())) {
                long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), now);
                record.setOverdueDays((int) overdueDays);
                record.setStatus("overdue");
                borrowRecordMapper.updateById(record);
            }
        }
    }
}
