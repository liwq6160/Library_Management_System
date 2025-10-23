package com.liwq.bookmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 添加/修改分类DTO
 */
@Data
public class CategoryDTO {

    @NotBlank(message = "分类名称不能为空")
    private String categoryName;

    private String description;

    private Integer sortOrder;
}
