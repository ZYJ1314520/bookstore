package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 图书请求
 */
@Data
public class BookDTO {

    private Long id;

    private Long categoryId;

    @NotNull(message = "书名不能为空")
    private String title;

    private String author;

    private String isbn;

    private String cover;

    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer stock;

    private String publisher;

    private String publishDate;

    private String description;

    private String detail;
}
