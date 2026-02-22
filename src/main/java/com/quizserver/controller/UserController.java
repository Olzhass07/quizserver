package com.quizserver.controller;

import com.quizserver.dto.ChangePasswordRequest;
import com.quizserver.dto.SignupRequest;
import com.quizserver.dto.UserDTO;
import com.quizserver.enteties.User;
import com.quizserver.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/auth/sign-up
     * Body: { "name": "...", "email": "...", "password": "...", "confirmPassword":
     * "..." }
     *
     * Returns 406 if:
     * - email already taken
     * - password < 6 characters
     * - password != confirmPassword
     */
    @PostMapping("/sign-up")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest request) {
        if (userService.hasUserWithEmail(request.getEmail())) {
            return new ResponseEntity<>("User already exists with this email.", HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            User createdUser = userService.createUser(request);
            return new ResponseEntity<>(createdUser, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * POST /api/auth/login
     * Body: { "email": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User dbUser = userService.login(user);
        if (dbUser == null)
            return new ResponseEntity<>("Wrong email or password.", HttpStatus.NOT_ACCEPTABLE);
        return new ResponseEntity<>(dbUser, HttpStatus.OK);
    }

    // --- PROFILE ENDPOINTS ---

    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(userService.getUserProfile(userId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        try {
            return new ResponseEntity<>(userService.updateProfile(userId, userDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok("Password changed successfully.");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
