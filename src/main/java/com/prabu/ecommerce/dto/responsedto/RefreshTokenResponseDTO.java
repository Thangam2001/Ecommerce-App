package com.prabu.ecommerce.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponseDTO {
    private String token;
    private String refreshToken;
    private String tokenType;
}
