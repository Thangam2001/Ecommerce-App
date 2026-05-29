package com.prabu.ecommerce.service.impl;

import com.prabu.ecommerce.dto.requestdto.*;
import com.prabu.ecommerce.dto.responsedto.LoginResponseDTO;
import com.prabu.ecommerce.dto.responsedto.RefreshTokenResponseDTO;
import com.prabu.ecommerce.exception.AuthException;
import com.prabu.ecommerce.model.RefreshToken;
import com.prabu.ecommerce.model.User;
import com.prabu.ecommerce.repository.AuthRepository;
import com.prabu.ecommerce.repository.RefreshTokenRepository;
import com.prabu.ecommerce.service.EmailService;
import com.prabu.ecommerce.service.AuthService;
import com.prabu.ecommerce.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService, UserDetailsService {
    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;
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

        refreshTokenRepository.deleteByUser(user);
        String refreshTokenValue = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);

        String accessToken = jwtUtil.generateToken(user);
        return LoginResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
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
    @Transactional
    public RefreshTokenResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        RefreshToken storedToken=refreshTokenRepository.findByToken(request.getRefreshToken()).orElseThrow(() -> new AuthException("Invalid refresh token"));

        if(LocalDateTime.now().isAfter(storedToken.getExpiredAt())){
            refreshTokenRepository.delete(storedToken);
            throw new AuthException("Refresh token expired. Please login again.");
        }

        User user = storedToken.getUser();

        String newAccessToken = jwtUtil.generateToken(user);

        refreshTokenRepository.delete(storedToken);

        String newRefreshToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(newRefreshToken)
                .user(user)
                .expiredAt(LocalDateTime.now().plus(Duration.ofMillis(jwtUtil.getRefreshTokenExpiration())))
                .build();
        refreshTokenRepository.save(refreshToken);

        return RefreshTokenResponseDTO.builder()
                .token(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
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
        refreshTokenRepository.deleteByUser(user);
    }

    @Override
    @Transactional
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String email;
        if (principal instanceof UserDetails userDetails) {
            email = userDetails.getUsername();
        } else if (principal instanceof String username) {
            email = username;
        } else {
            throw new AuthException("Invalid authenticated user");
        }

        User currentUser = authRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("User not found"));

        refreshTokenRepository.deleteByUser(currentUser);
        SecurityContextHolder.clearContext();
    }
}
