package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.UserProfileUpdateRequestDTO;
import com.prabu.ecommerce.dto.responsedto.UserProfileResponse;
import com.prabu.ecommerce.exception.UserException;
import com.prabu.ecommerce.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try{
            UserProfileResponse user=userService.getUserProfile(getAuthenticatedEmail(authentication));
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(Authentication authentication, @Valid @RequestBody UserProfileUpdateRequestDTO request) {
        try{
            UserProfileResponse user = userService.updateProfile(
                    getAuthenticatedEmail(authentication),
                    request
            );
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch (UserException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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
