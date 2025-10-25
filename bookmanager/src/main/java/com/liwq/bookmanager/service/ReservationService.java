package com.liwq.bookmanager.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.dto.ReservationDTO;
import com.liwq.bookmanager.dto.ReservationDetailDTO;

/**
 * 预约服务接口
 */
public interface ReservationService {

    /**
     * 预约图书
     */
    void reserveBook(Long userId, ReservationDTO reservationDTO);

    /**
     * 取消预约
     */
    void cancelReservation(Long userId, Long id);

    /**
     * 获取我的预约记录
     */
    Page<ReservationDetailDTO> getMyReservations(Long userId, int pageNum, int pageSize, String status);

    /**
     * 获取所有预约记录（管理员）
     */
    Page<ReservationDetailDTO> getAllReservations(int pageNum, int pageSize, Long userId, Long bookId, String status);

    /**
     * 获取预约详情
     */
    ReservationDetailDTO getReservationDetail(Long id);

    /**
     * 审核通过预约（管理员）
     */
    void approveReservation(Long id);

    /**
     * 拒绝预约（管理员）
     */
    void rejectReservation(Long id, String remark);

    /**
     * 图书归还后处理预约通知（由借阅服务调用）
     */
    void handleBookReturnNotification(Long bookId);
}
