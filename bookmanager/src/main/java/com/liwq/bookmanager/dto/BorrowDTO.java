package com.liwq.bookmanager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 借阅图书DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BorrowDTO {

    @NotNull(message = "图书ID不能为空")
    private Long bookId;
}
