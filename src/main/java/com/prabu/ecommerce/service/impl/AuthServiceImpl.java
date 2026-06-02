package com.prabu.ecommerce.service.impl;

import com.prabu.ecommerce.dto.requestdto.*;
import com.prabu.ecommerce.dto.responsedto.LoginResponseDTO;
import com.prabu.ecommerce.exception.AuthException;
import com.prabu.ecommerce.model.User;
import com.prabu.ecommerce.repository.AuthRepository;
import com.prabu.ecommerce.service.EmailService;
import com.prabu.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(RegisterRequestDTO request) {
        if (authRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AuthException("Email already exist");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));

        user.setVerified(false);
        user.setRole(User.Role.USER);

        User savedUser = authRepository.save(user);

        try {
            emailService.sendOtp(savedUser.getEmail(), savedUser.getOtp());
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }

        return savedUser;
    }

    @Override
    public boolean verifyOtp(VerifyEmailRequestDTO otpDetails) {
        User userDetails=authRepository.findByEmail(otpDetails.getEmail()).get();
        if(userDetails==null) {
            throw new AuthException("User not found");
        }
        System.out.println(userDetails);
        if(userDetails.isVerified()){
            throw new AuthException("User is already verified");
        }
        if(!userDetails.getOtp().equals(otpDetails.getOtp())) {
            throw new AuthException("Invalid OTP");
        }
        if(userDetails.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("OTP expired");
        }

        userDetails.setVerified(true);
        userDetails.setOtp(null);
        userDetails.setOtpExpiresAt(null);

        authRepository.save(userDetails);
        return true;
    }

    @Override
    public void resendOtp(String email) {
        User user = authRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        User savedUser = authRepository.save(user);
        try {
            emailService.sendOtp(savedUser.getEmail(), savedUser.getOtp());
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        if (!user.isVerified()) {
            throw new AuthException("User email not verified");
        }

        return LoginResponseDTO.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return loadUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByEmail(String email) {
        User user=authRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public void forgotPassword(ForgotPasswordRequestDTO request) {
        User user=authRepository.findByEmail(request.getEmail()).get();

        if(user==null) {
            throw new AuthException("User not found");
        }

        String otp = String.format("%06d", ThreadLocalRandom.current().nextInt(100000, 1000000));
        user.setOtp(otp);
        user.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));

        authRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        User user = authRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(
                        "No account found with this email"
                ));

        if(user.getOtp() == null || !user.getOtp().equals(request.getOtp())){
            throw new AuthException("Invalid OTP");
        }
        if(user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("OTP expired");
        }

        user.setOtp(null);
        user.setOtpExpiresAt(null);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        authRepository.save(user);
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("User is not authenticated");
        }

        SecurityContextHolder.clearContext();
    }
}
