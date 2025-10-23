package com.liwq.bookmanager.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 */
@Data
@TableName("books")
public class Book {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 作者
     */
    private String author;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * ISBN编号
     */
    private String isbn;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 馆藏总数
     */
    private Integer totalCount;

    /**
     * 可借数量
     */
    private Integer availableCount;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 出版日期
     */
    private LocalDate publishDate;

    /**
     * 封面图片路径
     */
    private String coverImage;

    /**
     * 图书简介
     */
    private String description;

    /**
     * 状态：0下架，1上架
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
