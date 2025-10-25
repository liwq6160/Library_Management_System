package com.liwq.bookmanager.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liwq.bookmanager.annotation.RequireAdmin;
import com.liwq.bookmanager.common.Result;
import com.liwq.bookmanager.dto.ReservationDTO;
import com.liwq.bookmanager.dto.ReservationDetailDTO;
import com.liwq.bookmanager.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    /**
     * 预约图书
     */
    @PostMapping
    public Result<Void> reserveBook(@Valid @RequestBody ReservationDTO reservationDTO, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservationService.reserveBook(userId, reservationDTO);
        return Result.success();
    }

    /**
     * 取消预约
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancelReservation(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        reservationService.cancelReservation(userId, id);
        return Result.success();
    }

    /**
     * 获取我的预约记录
     */
    @GetMapping("/my")
    public Result<Page<ReservationDetailDTO>> getMyReservations(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<ReservationDetailDTO> page = reservationService.getMyReservations(userId, pageNum, pageSize, status);
        return Result.success(page);
    }

    /**
     * 获取所有预约记录（管理员）
     */
    @GetMapping
    @RequireAdmin
    public Result<Page<ReservationDetailDTO>> getAllReservations(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String status) {
        Page<ReservationDetailDTO> page = reservationService.getAllReservations(pageNum, pageSize, userId, bookId, status);
        return Result.success(page);
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<ReservationDetailDTO> getReservationDetail(@PathVariable Long id) {
        ReservationDetailDTO detail = reservationService.getReservationDetail(id);
        return Result.success(detail);
    }

    /**
     * 审核通过预约（管理员）
     */
    @PutMapping("/{id}/approve")
    @RequireAdmin
    public Result<Void> approveReservation(@PathVariable Long id) {
        reservationService.approveReservation(id);
        return Result.success();
    }

    /**
     * 拒绝预约（管理员）
     */
    @PutMapping("/{id}/reject")
    @RequireAdmin
    public Result<Void> rejectReservation(@PathVariable Long id, @RequestParam(required = false) String remark) {
        reservationService.rejectReservation(id, remark);
        return Result.success();
    }
}
