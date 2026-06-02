package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体
 */
@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long shopId;

    private Long categoryId;

    private String title;

    private String author;

    private String isbn;

    private String cover;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private Integer sales;

    private String publisher;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    private String description;

    private String detail;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
