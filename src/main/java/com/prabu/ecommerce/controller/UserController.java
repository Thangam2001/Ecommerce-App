package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.UserProfileUpdateRequestDTO;
import com.prabu.ecommerce.dto.responsedto.UserProfileResponse;
import com.prabu.ecommerce.exception.UserException;
import com.prabu.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserProfileResponse user = userService.getUserProfile(getAuthenticatedEmail(authentication));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUser(Authentication authentication, @Valid @RequestBody UserProfileUpdateRequestDTO request) {
        UserProfileResponse user = userService.updateProfile(
                getAuthenticatedEmail(authentication),
                request
        );
        return ResponseEntity.ok(user);
    }

    private String getAuthenticatedEmail(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        if (principal instanceof String username) {
            return username;
        }

        throw new UserException("Invalid authenticated user");
    }
}
