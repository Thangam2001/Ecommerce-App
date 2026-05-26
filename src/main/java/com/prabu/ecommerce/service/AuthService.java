package com.prabu.ecommerce.service;

import com.prabu.ecommerce.dto.requestdto.*;
import com.prabu.ecommerce.dto.responsedto.LoginResponseDTO;
import com.prabu.ecommerce.dto.responsedto.RefreshTokenResponseDTO;
import com.prabu.ecommerce.model.User;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService {

    boolean verifyOtp(@Valid VerifyEmailRequestDTO otpDetails);

    User registerUser(@Valid User user);

    LoginResponseDTO login(@Valid LoginRequestDTO request);

    UserDetails loadUserByEmail(String email);

    RefreshTokenResponseDTO refreshToken(@Valid RefreshTokenRequestDTO refreshTokenRequestDTO);

    void forgotPassword(@Valid ForgotPasswordRequestDTO request);

    void resetPassword(@Valid ResetPasswordRequestDTO request);

    void logout();

    void resendOtp(@Valid String email);
}
