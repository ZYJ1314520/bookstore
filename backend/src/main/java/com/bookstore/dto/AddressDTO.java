package com.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址请求
 */
@Data
public class AddressDTO {

    private Long id;

    @NotBlank(message = "收件人姓名不能为空")
    private String receiverName;

    @NotBlank(message = "联系电话不能为空")
    private String phone;

    private String province;

    private String city;

    private String district;

    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    private Integer isDefault;
}
