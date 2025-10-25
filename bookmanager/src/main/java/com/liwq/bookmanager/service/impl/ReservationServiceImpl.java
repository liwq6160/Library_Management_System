package com.liwq.bookmanager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.ReservationDTO;
import com.liwq.bookmanager.dto.ReservationDetailDTO;
import com.liwq.bookmanager.mapper.BookMapper;
import com.liwq.bookmanager.mapper.ReservationRecordMapper;
import com.liwq.bookmanager.model.Book;
import com.liwq.bookmanager.model.ReservationRecord;
import com.liwq.bookmanager.service.ReservationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约服务实现类
 */
@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRecordMapper reservationRecordMapper;
    private final BookMapper bookMapper;

    public ReservationServiceImpl(ReservationRecordMapper reservationRecordMapper, BookMapper bookMapper) {
        this.reservationRecordMapper = reservationRecordMapper;
        this.bookMapper = bookMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reserveBook(Long userId, ReservationDTO reservationDTO) {
        // 验证图书是否存在
        Book book = bookMapper.selectById(reservationDTO.getBookId());
        if (book == null) {
            throw new RuntimeException("图书不存在");
        }

        // 验证图书是否可预约（库存为0表示已借出）
        if (book.getAvailableCount() > 0) {
            throw new RuntimeException("该图书当前有库存，无需预约，可直接借阅");
        }

        // 检查用户是否已预约该图书（待审核或已通过状态）
        LambdaQueryWrapper<ReservationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationRecord::getUserId, userId)
                .eq(ReservationRecord::getBookId, reservationDTO.getBookId())
                .in(ReservationRecord::getStatus, "pending", "approved");
        Long count = reservationRecordMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("您已预约过该图书，请勿重复预约");
        }

        // 创建预约记录
        ReservationRecord record = new ReservationRecord();
        record.setUserId(userId);
        record.setBookId(reservationDTO.getBookId());
        record.setReservationDate(LocalDateTime.now());
        record.setStatus("pending");
        record.setRemark(reservationDTO.getRemark());
        reservationRecordMapper.insert(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long userId, Long id) {
        // 验证预约记录是否存在
        ReservationRecord record = reservationRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 验证预约记录归属权限
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的预约记录");
        }

        // 检查预约状态是否可取消（待审核或已通过状态）
        if (!"pending".equals(record.getStatus()) && !"approved".equals(record.getStatus())) {
            throw new RuntimeException("当前状态不允许取消预约");
        }

        // 更新预约状态为已取消
        record.setStatus("cancelled");
        reservationRecordMapper.updateById(record);
    }

    @Override
    public Page<ReservationDetailDTO> getMyReservations(Long userId, int pageNum, int pageSize, String status) {
        Page<ReservationDetailDTO> page = new Page<>(pageNum, pageSize);
        return reservationRecordMapper.selectMyReservationPage(page, userId, status);
    }

    @Override
    public Page<ReservationDetailDTO> getAllReservations(int pageNum, int pageSize, Long userId, Long bookId, String status) {
        Page<ReservationDetailDTO> page = new Page<>(pageNum, pageSize);
        return reservationRecordMapper.selectAllReservationPage(page, userId, bookId, status);
    }

    @Override
    public ReservationDetailDTO getReservationDetail(Long id) {
        ReservationDetailDTO detail = reservationRecordMapper.selectReservationDetailById(id);
        if (detail == null) {
            throw new RuntimeException("预约记录不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveReservation(Long id) {
        // 验证预约记录是否存在
        ReservationRecord record = reservationRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 检查预约状态是否为待审核
        if (!"pending".equals(record.getStatus())) {
            throw new RuntimeException("只能审核待审核状态的预约记录");
        }

        // 更新预约状态为已通过
        record.setStatus("approved");
        reservationRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectReservation(Long id, String remark) {
        // 验证预约记录是否存在
        ReservationRecord record = reservationRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("预约记录不存在");
        }

        // 检查预约状态是否为待审核
        if (!"pending".equals(record.getStatus())) {
            throw new RuntimeException("只能审核待审核状态的预约记录");
        }

        // 更新预约状态为已取消，并填写拒绝原因
        record.setStatus("cancelled");
        if (remark != null && !remark.isEmpty()) {
            record.setRemark(remark);
        }
        reservationRecordMapper.updateById(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleBookReturnNotification(Long bookId) {
        // 查询该图书的已通过预约记录，按创建时间排序（先预约先通知）
        LambdaQueryWrapper<ReservationRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReservationRecord::getBookId, bookId)
                .eq(ReservationRecord::getStatus, "approved")
                .orderByAsc(ReservationRecord::getCreateTime);
        List<ReservationRecord> records = reservationRecordMapper.selectList(wrapper);

        // 如果有预约记录，将第一条更新为已完成状态（表示可借通知）
        if (!records.isEmpty()) {
            ReservationRecord firstRecord = records.get(0);
            firstRecord.setStatus("completed");
            firstRecord.setRemark("图书已归还，您可以前往借阅");
            reservationRecordMapper.updateById(firstRecord);
        }
    }
}
