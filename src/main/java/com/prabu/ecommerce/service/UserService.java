package com.prabu.ecommerce.service;

import com.prabu.ecommerce.dto.requestdto.UserProfileUpdateRequestDTO;
import com.prabu.ecommerce.dto.responsedto.UserProfileResponse;
import com.prabu.ecommerce.model.User;

public interface UserService {

    UserProfileResponse getUserProfile(String email);

    UserProfileResponse updateProfile(String currentEmail, UserProfileUpdateRequestDTO request);
}
