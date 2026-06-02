package com.bookstore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求
 */
@Data
public class OrderDTO {

    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long bookId;
        private Integer quantity;
    }
}
