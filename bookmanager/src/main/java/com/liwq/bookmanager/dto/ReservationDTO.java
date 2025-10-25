package com.liwq.bookmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建预约DTO
 */
@Data
public class ReservationDTO {

    /**
     * 图书ID
     */
    @NotNull(message = "图书ID不能为空")
    private Long bookId;

    /**
     * 备注信息
     */
    private String remark;
}
