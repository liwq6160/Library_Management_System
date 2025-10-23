package com.liwq.bookmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 添加/修改图书DTO
 */
@Data
public class BookDTO {

    @NotBlank(message = "图书名称不能为空")
    private String bookName;

    @NotBlank(message = "作者不能为空")
    private String author;

    private String publisher;

    private String isbn;

    @NotNull(message = "分类ID不能为空")
    private Long categoryId;

    @NotNull(message = "馆藏总数不能为空")
    @Min(value = 0, message = "馆藏总数不能小于0")
    private Integer totalCount;

    @NotNull(message = "可借数量不能为空")
    @Min(value = 0, message = "可借数量不能小于0")
    private Integer availableCount;

    private BigDecimal price;

    private LocalDate publishDate;

    private String coverImage;

    private String description;

    private Integer status;
}
