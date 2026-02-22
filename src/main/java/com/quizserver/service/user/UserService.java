package com.quizserver.service.user;

import com.quizserver.dto.ChangePasswordRequest;
import com.quizserver.dto.SignupRequest;
import com.quizserver.dto.UserDTO;
import com.quizserver.enteties.User;

public interface UserService {

    /**
     * Creates a new user from a SignupRequest.
     * Validates password length (min 6) and confirmPassword match before saving.
     */
    User createUser(SignupRequest request);

    Boolean hasUserWithEmail(String email);

    User login(User user);

    // Profile and Progress
    UserDTO getUserProfile(Long userId);

    UserDTO updateProfile(Long userId, UserDTO updates);

    void changePassword(ChangePasswordRequest request);
}
