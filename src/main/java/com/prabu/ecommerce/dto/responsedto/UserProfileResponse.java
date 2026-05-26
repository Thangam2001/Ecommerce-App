package com.prabu.ecommerce.dto.responsedto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private String name;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
