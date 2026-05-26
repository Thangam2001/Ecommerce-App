package com.prabu.ecommerce.service.impl;

import com.prabu.ecommerce.dto.requestdto.UserProfileUpdateRequestDTO;
import com.prabu.ecommerce.dto.responsedto.UserProfileResponse;
import com.prabu.ecommerce.exception.UserException;
import com.prabu.ecommerce.model.User;
import com.prabu.ecommerce.repository.UserRepository;
import com.prabu.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfileResponse getUserProfile(String email) {
        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

        return toProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String currentEmail, UserProfileUpdateRequestDTO request) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new UserException("User not found"));

        String requestedEmail = request.getEmail().trim();
        if (!user.getEmail().equalsIgnoreCase(requestedEmail)
                && userRepository.findByEmail(requestedEmail).isPresent()) {
            throw new UserException("Email already exists");
        }

        user.setName(request.getName().trim());
        user.setEmail(requestedEmail);

        return toProfileResponse(userRepository.save(user));
    }

    private UserProfileResponse toProfileResponse(User user) {
        UserProfileResponse userProfileResponse = new UserProfileResponse();
        userProfileResponse.setEmail(user.getEmail());
        userProfileResponse.setName(user.getName());
        userProfileResponse.setRole(user.getRole().toString());
        userProfileResponse.setCreatedAt(user.getCreatedAt());
        return userProfileResponse;
    }

}
