package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.*;
import com.prabu.ecommerce.dto.responsedto.RefreshTokenResponseDTO;
import com.prabu.ecommerce.exception.AuthException;
import com.prabu.ecommerce.service.AuthService;
import com.prabu.ecommerce.utils.JwtUtil;
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
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDTO request){
        try{
            var isRegistered=authService.registerUser(request);
            if(isRegistered != null){
                return ResponseEntity.ok("Registration successful. OTP sent to "+isRegistered.getEmail());
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch(AuthException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@Valid @RequestBody VerifyEmailRequestDTO otpDetails){
        try{
            boolean isVerified=authService.verifyOtp(otpDetails);
            if(isVerified){
                return ResponseEntity.ok("Verification successful. Now you can login");
            }else{
                return ResponseEntity.badRequest().build();
            }
        }catch(AuthException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> resendOtp(@Valid @RequestBody ForgotPasswordRequestDTO request){
        try{
            authService.resendOtp(request.getEmail());
            return ResponseEntity.ok("otp resented to "+request.getEmail());
        }catch(AuthException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request){
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO){
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequestDTO));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request){
        try{
            authService.forgotPassword(request);
            return ResponseEntity.ok("Forgot Password successful. Check your email");
        }catch (AuthException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request){
        try{
            authService.resetPassword(request);
            return ResponseEntity.ok("Reset Password successful.");
        }catch (AuthException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        authService.logout();
        return ResponseEntity.ok("Logout successful.");
    }

}
