package com.prabu.ecommerce.dto.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordRequestDTO {
    @NotBlank
    @Email(message = "Invalid email format")
    private String email;
}
