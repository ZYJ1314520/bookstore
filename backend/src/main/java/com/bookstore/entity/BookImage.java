package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书详情图片实体
 */
@Data
@TableName("book_image")
public class BookImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;

    private String imageUrl;

    private Integer sort;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
