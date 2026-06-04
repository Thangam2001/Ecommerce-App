package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.*;
import com.prabu.ecommerce.exception.AuthException;
import com.prabu.ecommerce.model.User;
import com.prabu.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request) {
        User isRegistered = authService.registerUser(request);
        if (isRegistered != null) {
            return ResponseEntity.ok("Registration successful. OTP sent to " + isRegistered.getEmail());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequestDTO otpDetails) {
        boolean isVerified = authService.verifyOtp(otpDetails);
        if (isVerified) {
            return ResponseEntity.ok("Verification successful. Now you can login");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.resendOtp(request.getEmail());
        return ResponseEntity.ok("OTP resent to " + request.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("Forgot Password successful. Check your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Reset Password successful.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Logout successful.");
    }

}
