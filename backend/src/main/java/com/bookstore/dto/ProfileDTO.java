package com.bookstore.dto;

import lombok.Data;

/**
 * 用户资料修改
 */
@Data
public class ProfileDTO {
    private String nickname;
    private String phone;
    private String email;
}
