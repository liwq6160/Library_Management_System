package com.liwq.bookmanager.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约详情DTO（包含用户和图书信息）
 */
@Data
public class ReservationDetailDTO {

    /**
     * 预约记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 图书ID
     */
    private Long bookId;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 作者
     */
    private String author;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 预约日期
     */
    private LocalDateTime reservationDate;

    /**
     * 状态：pending待审核，approved已通过，cancelled已取消，completed已完成
     */
    private String status;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
