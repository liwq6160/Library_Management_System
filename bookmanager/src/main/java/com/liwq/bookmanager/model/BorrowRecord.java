package com.liwq.bookmanager.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 借阅记录实体
 */
@Data
@TableName("borrow_records")
public class BorrowRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long bookId;

    private LocalDateTime borrowDate;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private Integer renewCount;

    private Integer overdueDays;

    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
