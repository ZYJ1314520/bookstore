package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private Long bookId;
    private String bookName;
    private String bookCover;
    private BigDecimal price;
    private Integer quantity;
    private Long shopId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
